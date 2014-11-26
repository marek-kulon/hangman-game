package hangman.web;

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
@WebServlet(urlPatterns = { "/", "/index", "/index.html" })
public class HomeController extends HttpServlet {

	private static final long serialVersionUID = 4957595326486125688L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		
		ControllerUtils.sendJsp("/pages/index.jsp", req, res);
	}
}
