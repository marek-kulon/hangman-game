package hangman.web.database;

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
@WebServlet(urlPatterns = { "/database" })
public class DatabaseHomeController extends HttpServlet {
	
	private static final long serialVersionUID = 7492312687092254837L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		
		ControllerUtils.sendJsp("/pages/hangman-database.jsp", req, res);
	}
}
