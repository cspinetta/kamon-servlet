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
