package kamon.servlet.server

import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

case class ResponseServletV3(underlineResponse: HttpServletResponse) extends ResponseServlet {
  override def status: Int = underlineResponse.getStatus
}

object ResponseServletV3 {

  def apply(request: ServletResponse): ResponseServletV3 = {
    new ResponseServletV3(request.asInstanceOf[HttpServletResponse])
  }
}
