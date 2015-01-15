package hangman.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Marek Kulon
 *
 */
@Controller
public class HomeController {

	@RequestMapping(value={ "/", "/index", "/index.html" }, method={GET, HEAD})
	protected String doGet() {
		return "index";
	}
}
