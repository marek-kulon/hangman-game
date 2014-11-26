package hangman.web.browser;

import hangman.web.util.ControllerUtils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Marek Kulon
 *
 */
@WebServlet(urlPatterns = { "/browser" })
public class BrowserHomeController extends HttpServlet {
	
	private static final long serialVersionUID = 28727494700693205L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		
		ControllerUtils.sendJsp("/pages/hangman-browser.jsp", req, res);
	}
}
