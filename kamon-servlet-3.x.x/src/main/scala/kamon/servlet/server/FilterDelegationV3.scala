package kamon.servlet.server

import javax.servlet._


case class FilterDelegationV3(chain: FilterChain) extends FilterDelegation[RequestServletV3, ResponseServletV3] {

  override def chain(request: RequestServletV3, response: ResponseServletV3): Unit = {
    chain.doFilter(request.underlineRequest, response.underlineResponse)
  }
}

final case class KamonAsyncListener(handler: KamonResponseHandler) extends AsyncListener {
  override def onError(event: AsyncEvent): Unit = handler.onError(Option(event.getThrowable))
  override def onComplete(event: AsyncEvent): Unit = handler.onComplete()
  override def onStartAsync(event: AsyncEvent): Unit = handler.onStartAsync()
  override def onTimeout(event: AsyncEvent): Unit = handler.onError(Option(event.getThrowable))
}
