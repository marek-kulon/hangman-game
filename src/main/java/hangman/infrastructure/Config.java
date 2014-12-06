package hangman.infrastructure;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config {
	@Resource
	private Environment environment;
	
	
	//FIXME
	@Bean
	public DBMapAccess dBMapAccess() {
		DBMapAccess res = new DBMapAccess();
		res.file = "";
		res.user = "";
		res.password = "";
		return res; 
	}

	
	//FIXME
	public static class DBMapAccess {
		String file, user, password;
	}

}
