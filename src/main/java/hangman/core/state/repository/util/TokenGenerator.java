package hangman.core.state.repository.util;

import java.util.UUID;

public final class TokenGenerator {

    // it's utility class
    private TokenGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
