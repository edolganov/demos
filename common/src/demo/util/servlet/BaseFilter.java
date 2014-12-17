package demo.util.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseFilter implements Filter {
	
	@Override
	public final void doFilter(ServletRequest req_, ServletResponse resp_,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)req_;
		HttpServletResponse resp = (HttpServletResponse)resp_;
		doFilter(req, resp, chain);
		
	}
	
	protected abstract void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) 
			throws IOException, ServletException;

}
