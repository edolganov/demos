package demo.util.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseCrossDomainAllowAllFilter extends BaseFilter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		resp.setHeader("Access-Control-Allow-Origin", "*");
		//resp.setHeader("Access-Control-Allow-Credentials", "true");
        
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {}



}
