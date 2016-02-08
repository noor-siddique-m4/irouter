package com.metafour.itrak.router;

import org.apache.catalina.connector.Connector;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;


@Configuration
public class TomcatConnectors {

	@Value("${server.port.http:0}")
	int port;

	@Bean
	public Integer port() {
		return SocketUtils.findAvailableTcpPort();
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector());
		return tomcat;
	}

	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		if (port <= 0) {
			port = port();
		}
		connector.setPort(port);
		return connector;
	}

	/* TODO: Redirect HTTP to HTTPS 
	 * https://drissamri.be/blog/java/enable-https-in-spring-boot/ 
	 */
}
