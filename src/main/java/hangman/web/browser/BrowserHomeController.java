package hangman.web.browser;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Marek Kulon
 *
 */
@Controller
public class BrowserHomeController {
	
	@RequestMapping(value="/browser", method={GET, HEAD})
	protected String doGet(HttpServletRequest req, HttpServletResponse res) {
		
		return "browser";
	}
}
