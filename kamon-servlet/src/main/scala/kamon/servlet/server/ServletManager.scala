package kamon.servlet.server

trait ServletManager {

}

trait RequestServlet {
  def isAsync: Boolean
  def addListener(handler: KamonResponseHandler)
  def getMethod: String
  def uri: String
  def url: String
  def headers: Map[String, String]
}

trait ResponseServlet {
  def status: Int

}

trait FilterDelegation[Request  <: RequestServlet, Response <: ResponseServlet] {

  def chain(request: Request, response: Response): Unit
}
