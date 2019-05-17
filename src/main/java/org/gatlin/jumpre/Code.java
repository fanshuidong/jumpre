package org.gatlin.jumpre;

import org.gatlin.jumpre.bean.option.StrOption;

/**
 * 错误码
 * 
 * @author fansd
 */
public class Code extends StrOption {
	
	private static final long serialVersionUID = -8458025331329472294L;
	
	public Code() {}
	
	public Code(String key, String desc) {
		super(key, desc);
	}
}
