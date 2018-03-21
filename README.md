# Kamon-Servlet <img align="right" src="https://rawgit.com/kamon-io/Kamon/master/kamon-logo.svg" height="150px" style="padding-left: 20px"/>
[![Build Status](https://travis-ci.org/kamon-io/kamon-servlet.svg?branch=master)](https://travis-ci.org/kamon-io/kamon-servlet)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kamon-io/Kamon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kamon/kamon-servlet_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kamon/kamon-servlet_2.12)


### Getting Started

The `kamon-servlet` module brings traces and metrics to your [servlet][1] based applications.

<b>Kamon Servlet</b> is currently available for Scala 2.10, 2.11 and 2.12.

Supported releases and dependencies are shown below.

| kamon-servlet-2.5  | status | jdk        | scala            
|:---------------:|:------:|:----------:|------------------
|  -          | - | 1.7+, 1.8+ | 2.10, 2.11, 2.12

| kamon-servlet-3.x.x  | status | jdk        | scala   
|:---------------:|:------:|:----------:|------------------
|  -          | - | 1.7+, 1.8+       | 2.10, 2.11, 2.12  

To get `kamon-servlet` in your project:

*TODO*


### Setting up

#### Servlet v3+

To enable `kamon-servlet` on your app all you need to do is to add the 
Kamon filter `kamon.servlet.v3.KamonFilterV3` in your web app. A simple way is to introduce a ApplicationContextListener.
In `kamon.servlet.v3.example.KamonContextListene`[2] you can find an example:

```java
package kamon.servlet.v3.example;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import kamon.Kamon;
import kamon.servlet.v3.KamonFilterV3;

public class KamonContextListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    Kamon.config();
    // subscribe all your reporters here!
    servletContextEvent
        .getServletContext()
        .addFilter("KamonFilter", new KamonFilterV3())
        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    Kamon.stopAllReporters();
    System.out.println("KamonContextListener destroyed");
  }
}
```

#### Servlet v2.5

For servlet 2.5 there isn't a programmatically way to achieve it, but you can enable it
adding a filter to install Kamon and add its Filter.
In `kamon.servlet.v25.example.KamonFilterWiring`[3] you can find an example:

```java
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
 * Also you need to configure it on your web.xml
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

```

Finally in your `web.xml`:

```xml
<filter>
   <filter-name>kamonFilter</filter-name>
   <filter-class>kamon.servlet.v25.example.KamonFilterWiring</filter-class>
 </filter>
 <filter-mapping>
   <filter-name>kamonFilter</filter-name>
   <url-pattern>/*</url-pattern>
 </filter-mapping>
```

### Micro Benchmarks

Execute from your terminal:

```bash
sbt
project benchmarks-3 # or benchmarks-25
jmh:run -i 50 -wi 20 -f1 -t1 .*Benchmark.*
```


[1]: http://www.oracle.com/technetwork/java/index-jsp-135475.html
[2]: kamon-servlet-3.x.x/src/test/java/kamon/servlet/v3/example/KamonContextListener.java
[3]: kamon-servlet-2.5/src/test/java/kamon/servlet/v25/example/KamonFilterWiring.java
