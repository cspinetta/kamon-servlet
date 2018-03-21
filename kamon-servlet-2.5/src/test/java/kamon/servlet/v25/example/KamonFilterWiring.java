package kamon.servlet.v25.example;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import kamon.Kamon;
import kamon.servlet.v25.KamonFilterV25;


/**
 * Example of wiring KamonFilterV25 on a servlet-based web app
 *
 * Also you need to configure it on the web.xml:
 *
 * <pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;kamonFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;kamon.servlet.v25.example.KamonFilterWiring&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;kamonFilter&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 */
public class KamonFilterWiring implements Filter {

  private final KamonFilterV25 kamonFilter = new KamonFilterV25();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Kamon.config();
    // subscribe all your reporters here!
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    kamonFilter.doFilter(request, response, chain);
  }

  @Override public void destroy() {
    Kamon.stopAllReporters();
  }
}
