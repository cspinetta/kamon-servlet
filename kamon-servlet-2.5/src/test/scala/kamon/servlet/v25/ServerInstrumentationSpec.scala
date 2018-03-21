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

package kamon.servlet.v25

import kamon.Kamon
import kamon.servlet.v25.server.{JettySupport, SyncTestServlet}
import kamon.trace.Span
import kamon.trace.Span.TagValue
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpec}

import scala.concurrent.duration._

class ServerInstrumentationSpec extends WordSpec
  with Matchers
  with BeforeAndAfterAll
  with Eventually
  with OptionValues
  with SpanReporter
  with JettySupport {

  import com.softwaremill.sttp._
  implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  override val servlet = SyncTestServlet()

  override protected def beforeAll(): Unit = {
    Kamon.config()
    startServer()
    startRegistration()
  }

  override protected def afterAll(): Unit = {
    stopRegistration()
    stopServer()
  }

  private def get(path: String, headers: Seq[(String, String)] = Seq()): Id[Response[String]] = {
    sttp.get(Uri("localhost", port).path(path)).headers(headers: _*).send()
  }

  "The Server instrumentation on Servlet 2.5" should {
    "propagate the current context and respond to the ok action" in {

      get("/sync/tracing/ok").code shouldBe 200

      eventually(timeout(3 seconds)) {

        val span = reporter.nextSpan().value
        val spanTags = stringTag(span) _

        span.operationName shouldBe "sync.tracing.ok.get"
        spanTags("span.kind") shouldBe "server"
        spanTags("component") shouldBe "servlet.server"
        spanTags("http.method") shouldBe "GET"
        spanTags("http.url") shouldBe "/sync/tracing/ok"
        span.tags("http.status_code") shouldBe TagValue.Number(200)

        span.context.parentID.string shouldBe ""
      }
    }

    "propagate the current context and respond to the not-found action" in {

      get("/sync/tracing/not-found").code shouldBe 404

      eventually(timeout(3 seconds)) {
        val span = reporter.nextSpan().value
        val spanTags = stringTag(span) _

        span.operationName shouldBe "not-found"
        spanTags("span.kind") shouldBe "server"
        spanTags("component") shouldBe "servlet.server"
        spanTags("http.method") shouldBe "GET"
        spanTags("http.url") shouldBe "/sync/tracing/not-found"
        span.tags("http.status_code") shouldBe TagValue.Number(404)

        span.context.parentID.string shouldBe ""
      }
    }

    "propagate the current context and respond to the error action" in {
      get("/sync/tracing/error").code shouldBe 500

      eventually(timeout(3 seconds)) {
        val span = reporter.nextSpan().value
        val spanTags = stringTag(span) _

        span.operationName shouldBe "sync.tracing.error.get"
        spanTags("span.kind") shouldBe "server"
        spanTags("component") shouldBe "servlet.server"
        spanTags("http.method") shouldBe "GET"
        spanTags("http.url") shouldBe "/sync/tracing/error"
        span.tags("error") shouldBe TagValue.True
        span.tags("http.status_code") shouldBe TagValue.Number(500)

        span.context.parentID.string shouldBe ""
      }
    }

    "resume the incoming context and respond to the ok action" in {
      get("/sync/tracing/ok", IncomingContext.headersB3).code shouldBe 200

      eventually(timeout(3 seconds)) {

        val span = reporter.nextSpan().value
        val spanTags = stringTag(span) _

        span.operationName shouldBe "sync.tracing.ok.get"
        spanTags("span.kind") shouldBe "server"
        spanTags("component") shouldBe "servlet.server"
        spanTags("http.method") shouldBe "GET"
        spanTags("http.url") shouldBe "/sync/tracing/ok"
        span.tags("http.status_code") shouldBe TagValue.Number(200)

        span.context.parentID.string shouldBe IncomingContext.SpanId
        span.context.traceID.string shouldBe IncomingContext.TraceId
      }
    }
  }

  def stringTag(span: Span.FinishedSpan)(tag: String): String = {
    span.tags(tag).asInstanceOf[TagValue.String].string
  }

  object IncomingContext {
    import kamon.trace.SpanCodec.B3.{Headers => B3Headers}

    val TraceId = "1234"
    val ParentSpanId = "2222"
    val SpanId = "4321"
    val Sampled = "1"
    val Flags = "some=baggage;more=baggage"


    val headersB3 = Seq(
      (B3Headers.TraceIdentifier, TraceId),
      (B3Headers.ParentSpanIdentifier, ParentSpanId),
      (B3Headers.SpanIdentifier, SpanId),
      (B3Headers.Sampled, Sampled),
      (B3Headers.Flags, Flags))

  }

}
