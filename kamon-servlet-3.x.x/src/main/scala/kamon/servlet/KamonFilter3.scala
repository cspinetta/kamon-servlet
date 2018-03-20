package kamon.servlet

import javax.servlet._
import kamon.servlet.server.{FilterDelegation3, RequestServlet3, ResponseServlet3}


case class KamonFilter3() extends Filter with KamonFilter {

  override type Request  = RequestServlet3
  override type Response = ResponseServlet3
  override type Chain    = FilterDelegation3

  override def init(filterConfig: FilterConfig): Unit = ()

  override def destroy(): Unit = ()

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    executeAround(RequestServlet3(request), ResponseServlet3(response), FilterDelegation3(chain))
  }
}
