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

package kamon.servlet.v25.server

import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse
import kamon.servlet.server.ResponseServlet

case class ResponseServletV25(underlineResponse: HttpServletResponse) extends ResponseServlet {
  override def status: Int = ??? //TODO: underlineResponse.getStatus
}

object ResponseServletV25 {

  def apply(request: ServletResponse): ResponseServletV25 = {
    new ResponseServletV25(request.asInstanceOf[HttpServletResponse])
  }
}
