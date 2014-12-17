package demo.util.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class BaseSetupAjaxFromHttpToHttpsFilter extends BaseFilter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	protected abstract String getOrigin(HttpServletRequest req, HttpServletResponse resp);
	
	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		String origin = getOrigin(req, resp);
		if(origin != null){
			resp.setHeader("Access-Control-Allow-Origin", origin);
			resp.setHeader("Access-Control-Allow-Methods", "POST");
			resp.setHeader("Access-Control-Allow-Credentials", "true");
		}
        
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {}



}
