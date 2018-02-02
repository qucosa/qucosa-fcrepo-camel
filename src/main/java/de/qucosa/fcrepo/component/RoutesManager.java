package de.qucosa.fcrepo.component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class RoutesManager implements ServletContextListener {
	CamelContext camelContext;

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			camelContext = new DefaultCamelContext();
			camelContext.addRoutes(new Routes());
			camelContext.start();
//			Thread.sleep(6000);
//			camelContext.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			camelContext.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
