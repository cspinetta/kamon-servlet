package kamon.servlet.server

import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest

case class RequestServletV3(underlineRequest: HttpServletRequest) extends RequestServlet {
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

object RequestServletV3 {

  def apply(request: ServletRequest): RequestServletV3 = {
    new RequestServletV3(request.asInstanceOf[HttpServletRequest])
  }
}
