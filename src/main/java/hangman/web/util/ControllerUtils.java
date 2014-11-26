package hangman.web.util;

import hangman.util.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Marek Kulon
 *
 */
public class ControllerUtils {

	public static void sendJsp(String jspName, HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		Validate.notBlank(jspName);
		
		req.getRequestDispatcher(jspName).forward(req, res);
	}

	public static <T> void sendJson(T responseObject, HttpServletResponse res) 
			throws IOException {
		Validate.notNull(responseObject);
		Validate.notNull(res);

		res.setContentType("application/json");
		res.setHeader("Cache-Control", "nocache");
		res.setCharacterEncoding("UTF-8");
		PrintWriter writer = res.getWriter();
		writer.print(JsonConverter.convert(responseObject));
		writer.flush();
		writer.close();
	}

}
