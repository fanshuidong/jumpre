package org.gatlin.jumpre.websocket.config;


import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.websocket.server.WsSci;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class TomcatConfiguration {
	
	private static Logger logger = LoggerFactory.getLogger(TomcatConfiguration.class);
	public static final String CONNECTOR = "org.apache.coyote.http11.Http11NioProtocol";

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomact = new TomcatServletWebServerFactory();
		tomact.addAdditionalTomcatConnectors(createSslConnector());
		return tomact;
	}
	
	private Connector createSslConnector() {
		Connector connector = new Connector(CONNECTOR);
		connector.setScheme("http");
		//Connector监听的http的端口号
		connector.setPort(8089);
		connector.setSecure(false);
		//监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(8443);
		return connector;
	}
	
	/**
	 * 创建wss协议接口
	 * @return
	 */
	@Bean
	public TomcatContextCustomizer tomcatContextCustomizer() {
		logger.info("创建wss协议 init");
        return new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
            	logger.info("init customize");
                context.addServletContainerInitializer(new WsSci(), null);
            }
        };
    }
}
