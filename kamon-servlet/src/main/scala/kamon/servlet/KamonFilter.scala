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
import kamon.servlet.Metrics.{GeneralMetrics, RequestTimeMetrics, ResponseTimeMetrics, ServiceMetrics}
import kamon.servlet.server.{RequestServlet, _}

import scala.language.postfixOps
import scala.util.Try

trait KamonFilter {

  type Request  <: RequestServlet
  type Response <: ResponseServlet
  type Chain    <: FilterDelegation[Request, Response]

  val servletMetrics = ServletMetrics(ServiceMetrics(GeneralMetrics(), RequestTimeMetrics(), ResponseTimeMetrics()))

  def executeAround(request: Request, response: Response, next: Chain): Unit = {
    val start = Kamon.clock().instant()

    servletMetrics.withMetrics(start, request, response) { metricsContinuation =>
      ServletTracing.withTracing(request, response, metricsContinuation) { tracingContinuation =>
        process(request, response, tracingContinuation) {
          next.chain(request, response)
        }
      }
    } get

  }

  private def process(request: Request, response: Response,
                      tracingContinuation: TracingContinuation)(thunk: => Unit): Try[Unit] = {
    val result = Try(thunk)
    onFinish(request, response)(result, tracingContinuation)
  }

  private def onFinish(request: Request, response: Response): (Try[Unit], TracingContinuation) => Try[Unit] = {
    if (request.isAsync) handleAsync(request, response)
    else                        handleSync(request, response)
  }

  private def handleAsync(request: Request, response: Response)
                         (result: Try[Unit], continuation: TracingContinuation): Try[Unit] = {
    val handler = FromTracingResponseHandler(continuation)
    request.addListener(handler)
    result
  }

  private def handleSync(request: Request, response: Response)
                        (result: Try[Unit], continuation: TracingContinuation): Try[Unit] = {
    val handler = FromTracingResponseHandler(continuation)
    result
      .map { value =>
        handler.onComplete()
        value
      }
      .recover {
        case error: Throwable =>
          handler.onError(Some(error))
          error
      }
  }

}

case class FromTracingResponseHandler(continuation: TracingContinuation) extends KamonResponseHandler {
  override def onError(error: Option[Throwable]): Unit = continuation.onError(Kamon.clock().instant()) // FIXME: save Throwable
  override def onComplete(): Unit = continuation.onSuccess(Kamon.clock().instant())
  override def onStartAsync(): Unit = ()
}
