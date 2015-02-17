package hangman.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Marek Kulon
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


    public static String readFileToString(String path) {
        Validate.notBlank(path);

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String result = null;
        try (InputStream is = loader.getResourceAsStream(path)) {
            result = IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            log.error("reading file exc, {}", e);
        }
        return result;
    }


    public static File getFileByPath(String path) {
        Validate.notBlank(path);

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        URL url = loader.getResource(path);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (Exception e) {
            log.error("getting file exc, {}", e);
        }
        return file;
    }

}
