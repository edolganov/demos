package filescan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestServlet extends HttpServlet {
	
	

	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(true);
		
		PrintWriter writer = resp.getWriter();
		writer.println("<html><body><pre>");
		writer.println(""+ new FileScanner().getFilesList());
		writer.println("</pre></body></html>");
		writer.close();
	}

}
