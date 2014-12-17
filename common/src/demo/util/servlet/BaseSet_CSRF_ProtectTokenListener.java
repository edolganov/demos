package demo.util.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static demo.util.servlet.WebUtil.*;

public abstract class BaseSet_CSRF_ProtectTokenListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		createAndSet_CSRF_ProtectToken(session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {}

}
