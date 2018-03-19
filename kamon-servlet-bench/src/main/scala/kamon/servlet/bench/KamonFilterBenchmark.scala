package kamon.servlet.bench

import java.util.concurrent.TimeUnit

import kamon.Kamon
import kamon.servlet.KamonFilter
import kamon.servlet.server.JettyServer
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole


class KamonFilterBenchmark {

  import com.softwaremill.sttp._
  implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  val server = new JettyServer()
  var port: Int = 0

  @Setup(Level.Trial)
  def setup(): Unit = {
    Kamon.config()
    server.start()
    port = server.selectedPort
  }

  @TearDown(Level.Trial)
  def doTearDown(): Unit = {
    server.stop()
  }

  private def get(path: String): Id[Response[String]] = {
    sttp.get(Uri("localhost", port).path(path)).send()
  }

  /**
    * This benchmark attempts to measure the performance without any context propagation.
    *
    * @param blackhole a { @link Blackhole} object supplied by JMH
    */
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @Fork
  def tracing(blackhole: Blackhole): Unit = {
    blackhole.consume(get("/tracing/ok").code)
  }

}
