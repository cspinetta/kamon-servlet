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

import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import kamon.servlet.server.RequestServlet

case class RequestServletV25(underlineRequest: HttpServletRequest) extends RequestServlet {

  override def getMethod: String = underlineRequest.getMethod

  override def uri: String = underlineRequest.getRequestURI

  override def url: String = underlineRequest.getRequestURL.toString

  override def headers: Map[String, String] = {
//    val headersIterator = underlineRequest.getHeaderNames
//    val headers = Map.newBuilder[String, String]
//    while (headersIterator.hasMoreElements) {
//      val name = headersIterator.nextElement()
//      headers += (name -> underlineRequest.getHeader(name))
//    }
//    headers.result()
    ??? // TODO
  }
}

object RequestServletV25 {

  def apply(request: ServletRequest): RequestServletV25 = {
    new RequestServletV25(request.asInstanceOf[HttpServletRequest])
  }
}
