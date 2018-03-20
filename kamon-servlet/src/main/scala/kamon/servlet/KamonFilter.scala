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

package kamon.servlet

import kamon.Kamon
import kamon.servlet.server._

import scala.language.postfixOps

trait KamonFilter {

  type Request  <: RequestServlet
  type Response <: ResponseServlet
  type Chain    <: FilterDelegation[Request, Response]

  val servletMetrics = ServletMetrics()

  def executeAround(request: Request, response: Response, next: Chain): Unit = {
    val start = Kamon.clock().instant()

    servletMetrics.withMetrics(start, request, response) { metricsContinuation =>
      ServletTracing.withTracing(request, response, metricsContinuation) { tracingContinuation =>
        next.chain(request, response)(tracingContinuation)
      }
    } get

  }

}
