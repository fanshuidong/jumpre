package org.gatlin.jumpre.util.menu;

/**
 * 文档类型
 * 
 * @author fansd
 */
public enum ContentType {

	APPLICATION_JSON_UTF_8("application/json;charset=utf-8"),
	
	APPLICATION_FORM_URLENCODED_UTF_8("application/x-www-form-urlencoded;charset=utf-8");
	
	private String mark;
	
	private ContentType(String mark) {
		this.mark = mark;
	}
	
	public String mark() {
		return mark;
	}
}
