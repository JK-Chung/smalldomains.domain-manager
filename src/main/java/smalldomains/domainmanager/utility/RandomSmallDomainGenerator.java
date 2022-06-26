package smalldomains.domainmanager.utility;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class RandomSmallDomainGenerator {

    private static final int MAX_RANDOM_NUMBER = 62; // 26 lowercase alpha + 26 uppercase alpha + 10 numeric digits
    private static final int NO_CHARS_IN_RANDOM_SMALL_DOMAIN = 7;

    private final Random random = new Random();

    public String generateRandomSmallDomain() {
        return Arrays.stream(generateRandomNumbers())
                .mapToObj(RandomSmallDomainGenerator::encodeInteger)
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static char encodeInteger(final int toEncode) {
        if(!(0 <= toEncode && toEncode < MAX_RANDOM_NUMBER)) {
            throw new IllegalArgumentException("toEncode argument (%d) must be within range 0 (inclusive) and %d (inclusive)".formatted(toEncode, MAX_RANDOM_NUMBER - 1));
        }

        final int asciiCodepoint;
        if(toEncode < 26) {
            // encode into lowercase alpha
            asciiCodepoint = toEncode + (int) 'a';
        } else if(toEncode < 52) {
            // encode into uppercase alpha
            asciiCodepoint = toEncode - 26 + (int) 'A';
        } else {
            // encode into numeric digits
            asciiCodepoint = toEncode - 52 + (int) '0';
        }

        return (char) asciiCodepoint;
    }

    private int[] generateRandomNumbers() {
        return IntStream.range(0, NO_CHARS_IN_RANDOM_SMALL_DOMAIN)
                .map(ignorable -> random.nextInt(MAX_RANDOM_NUMBER + 1))
                .toArray();
    }

}
