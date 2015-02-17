package hangman.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FileUtilsTest {

    @Test
    public void fileReadOk() {
        assertTrue(FileUtils.readFileToString("secrets-test.json") != null);
    }

    @Test
    public void fileReadNotOk() {
        assertTrue(FileUtils.readFileToString("secrets-test-XXX.json") == null);
    }

    @Test
    public void fileGetOk() {
        assertTrue(FileUtils.getFileByPath("game-sate-db-test.db") != null);
    }

    @Test
    public void fileGetNotOk() {
        assertTrue(FileUtils.getFileByPath("game-sate-db-test-XXX.db") == null);
    }

}
