package org.gatlin.jumpre;

import org.gatlin.jumpre.util.SpringContextUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jumper")
public class JumperConfig {
	
	private String appType;
	private String gameStartUrl;
	private String gameEndUrl;
	
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getGameStartUrl() {
		return gameStartUrl;
	}

	public void setGameStartUrl(String gameStartUrl) {
		this.gameStartUrl = gameStartUrl;
	}

	public String getGameEndUrl() {
		return gameEndUrl;
	}

	public void setGameEndUrl(String gameEndUrl) {
		this.gameEndUrl = gameEndUrl;
	}
	
	public static JumperConfig instance() {
		JumperConfig jumperConfig = (JumperConfig)SpringContextUtil.getBean("jumperConfig");
		return jumperConfig;
	}
	
	public static String appType() {
		return instance().getAppType();
	}
	
	public static String gameStartUrl() {
		return instance().getGameStartUrl();
	}
	
	public static String gameEndUrl() {
		return instance().getGameEndUrl();
	}

}
