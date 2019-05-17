package org.gatlin.jumpre.util;

import org.gatlin.jumpre.Code;
import org.gatlin.jumpre.CoreCode;
import org.gatlin.jumpre.http.exception.CodeException;

public class Assert {
	
	public static final <T> T isNull(Code code, T param) {
		if (null != param)
			throw new CodeException(code);
		return param;
	}

	public static final <T> T notNull(T param) {
		return notNull("notNull assert failure!", param);
	}
	
	public static final <T> T notNull(String desc, T param) {
		if (null == param)
			throw new CodeException(desc);
		return param;
	}
	
	public static final <T> T notNull(Code code, T param) {
		if (null == param)
			throw new CodeException(code);
		return param;
	}
	
	public static final void notNull(Code code, Object... params) {
		for (Object param : params) {
			if (null == param)
				throw new CodeException(code);
		}
	}
	
	public static final boolean isTrue(boolean expression) { 
		return isTrue(expression, "isTrue asset failure!");
	}
	
	public static final boolean isTrue(boolean expression, String desc) { 
		if (!expression)
			throw new CodeException(desc);
		return expression;
	}
	
	public static final boolean isTrue(Code code, boolean expression) { 
		if (!expression)
			throw new CodeException(code);
		return expression;
	}
	
	public static final String hasText(String content) {
		if (!StringUtil.hasText(content))
			throw new CodeException(CoreCode.SYSTEM_ERR);
		return content;
	}
	
	public static final String hasText(Code code, String content) {
		if (!StringUtil.hasText(content))
			throw new CodeException(code);
		return content;
	}
	
	public static final String hasNoText(Code code, String content) {
		if (StringUtil.hasText(content))
			throw new CodeException(code);
		return content;
	}
}
