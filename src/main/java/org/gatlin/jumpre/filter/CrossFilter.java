package org.gatlin.jumpre.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * 跨域filer
 * 
 * @author fansd
 */
public class CrossFilter implements Filter {

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;  
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");  
        response.setHeader("Access-Control-Allow-Origin", "*");  
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");  
        response.setHeader("Access-Control-Max-Age", "3600");  
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Accept, Origin, Token");  
        response.setHeader("Access-Control-Allow-Credentials", "true");  
        if (request.getMethod() == HttpMethod.OPTIONS.name()) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
			return;
        }
        arg2.doFilter(arg0, arg1);  
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
