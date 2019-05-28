package org.gatlin.jumpre;

import java.util.ResourceBundle;

public interface Config {
	public static final String appType = ResourceBundle.getBundle("conf/config").getString("appType");
	public static final String gameStartUrl = ResourceBundle.getBundle("conf/config").getString("gameStartUrl");
	public static final String gameEndUrl = ResourceBundle.getBundle("conf/config").getString("gameEndUrl");
	
	public static void main(String[] args) {
		System.out.println(appType);
	}
}
