package kamon.servlet

import javax.servlet._
import kamon.servlet.server.{FilterDelegationV3, RequestServletV3, ResponseServletV3}


class KamonFilterV3 extends Filter with KamonFilter {

  override type Request  = RequestServletV3
  override type Response = ResponseServletV3
  override type Chain    = FilterDelegationV3

  override def init(filterConfig: FilterConfig): Unit = ()

  override def destroy(): Unit = ()

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    executeAround(RequestServletV3(request), ResponseServletV3(response), FilterDelegationV3(chain))
  }
}
