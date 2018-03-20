/*
 * =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.servlet.v3.server

import javax.servlet._
import kamon.servlet.server.{FilterDelegation, KamonResponseHandler}


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
