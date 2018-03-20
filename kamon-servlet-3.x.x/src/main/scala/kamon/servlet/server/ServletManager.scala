package kamon.servlet.server

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet._


case class RequestServlet3(underlineRequest: HttpServletRequest) extends RequestServlet {
  override def isAsync: Boolean = underlineRequest.isAsyncStarted

  override def addListener(handler: KamonResponseHandler): Unit = {
    underlineRequest.getAsyncContext.addListener(KamonAsyncListener(handler))
  }

  override def getMethod: String = underlineRequest.getMethod

  override def uri: String = underlineRequest.getRequestURI

  override def url: String = underlineRequest.getRequestURL.toString

  override def headers: Map[String, String] = {
    val headersIterator = underlineRequest.getHeaderNames
    val headers = Map.newBuilder[String, String]
    while (headersIterator.hasMoreElements) {
      val name = headersIterator.nextElement()
      headers += (name -> underlineRequest.getHeader(name))
    }
    headers.result()
  }
}

object RequestServlet3 {

  def apply(request: ServletRequest): RequestServlet3 = {
    new RequestServlet3(request.asInstanceOf[HttpServletRequest])
  }
}

case class ResponseServlet3(underlineResponse: HttpServletResponse) extends ResponseServlet {
  override def status: Int = underlineResponse.getStatus
}

object ResponseServlet3 {

  def apply(request: ServletResponse): ResponseServlet3 = {
    new ResponseServlet3(request.asInstanceOf[HttpServletResponse])
  }
}

case class FilterDelegation3(chain: FilterChain) extends FilterDelegation[RequestServlet3, ResponseServlet3] {

  override def chain(request: RequestServlet3, response: ResponseServlet3): Unit = {
    chain.doFilter(request.underlineRequest, response.underlineResponse)
  }
}

final case class KamonAsyncListener(handler: KamonResponseHandler) extends AsyncListener {
  override def onError(event: AsyncEvent): Unit = handler.onError(Option(event.getThrowable))
  override def onComplete(event: AsyncEvent): Unit = handler.onComplete()
  override def onStartAsync(event: AsyncEvent): Unit = handler.onStartAsync()
  override def onTimeout(event: AsyncEvent): Unit = handler.onError(Option(event.getThrowable))
}
