package org.gatlin.jumpre.websocket.config;


import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.websocket.server.WsSci;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class TomcatConfiguration {
	
	private static Logger logger = LoggerFactory.getLogger(TomcatConfiguration.class);
	public static final String CONNECTOR = "org.apache.coyote.http11.Http11NioProtocol";
	@Value("${server.http.port}")
	private int httpPort;
	@Value("${server.port}")
	private int httpsPort;

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint constraint = new SecurityConstraint();
//                constraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                constraint.addCollection(collection);
//                context.addConstraint(constraint);
//            }
        };
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
		return tomcat;
	}
	
	private Connector createSslConnector() {
		Connector connector = new Connector(CONNECTOR);
		connector.setScheme("http");
		//Connector监听的http的端口号
		connector.setPort(httpPort);
		connector.setSecure(false);
		//监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(httpsPort);
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
