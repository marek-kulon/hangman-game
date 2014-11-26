package hangman.web.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Found somewhere on Internet
 *
 */
public class MD5Util {
	private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

	public static String generate(String val) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(val.getBytes("UTF-8"));

			byte byteData[] = md.digest();

			// convert the byte to hex format method
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (Exception e) {
			log.error("check sum error", e);
		}
		return null;

	}

}
