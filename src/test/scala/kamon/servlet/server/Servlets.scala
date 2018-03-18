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

package kamon.servlet.server

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

object Servlets {

  val defaultDelay = 1000 // millis

  def withDelay[A](timeInMillis: Long)(thunk: => A): A = {
    Thread.sleep(timeInMillis)
    thunk
  }
}

class AsyncTestServlet extends HttpServlet {
  import Servlets._


  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val asyncContext = req.startAsync(req, resp)

    asyncContext.start(new Runnable {
      override def run(): Unit = {
        asyncContext.getRequest.asInstanceOf[HttpServletRequest].getRequestURI match {
          case "/async/tracing/not-found" ⇒ withDelay(defaultDelay) { resp.setStatus(404) }
          case "/async/tracing/error"     ⇒ withDelay(defaultDelay) { resp.setStatus(500) }
          case "/async/tracing/ok"        ⇒ withDelay(defaultDelay) { resp.setStatus(200) }
          case other                      ⇒
            resp.getOutputStream.println(s"Something wrong on the test. Endpoint unmapped: $other")
            resp.setStatus(404)
        }
        asyncContext.complete()
      }
    })
  }
}

class SyncTestServlet extends HttpServlet {
  import Servlets._

  val defaultDelay = 1000 // millis

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = req.getRequestURI match {
    case "/sync/tracing/not-found" ⇒ resp.setStatus(404)
    case "/sync/tracing/error"     ⇒ resp.setStatus(500)
    case "/sync/tracing/ok"        ⇒ resp.setStatus(200)
    case "/sync/tracing/slowly"    ⇒ withDelay(defaultDelay) { resp.setStatus(200) }
    case other                     ⇒
      resp.getOutputStream.println(s"Something wrong on the test. Endpoint unmapped: $other")
      resp.setStatus(404)
  }
}