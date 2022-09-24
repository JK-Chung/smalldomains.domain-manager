package smalldomains.domainmanager.validationConstraints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainValidatorTest {

    @Test
    void areValidUrlsWithoutSchemeAccepted() {
        final boolean isValid = new DomainValidator().isValid("google.com", null);
        assertTrue(isValid);
    }

    @Test
    void areValidUrlsWitSchemeAccepted() {
        final boolean isValid = new DomainValidator().isValid("https://google.com", null);
        assertTrue(isValid);
    }

    @Test
    void ftpSchemeRejected() {
        final boolean isValid = new DomainValidator().isValid("ftp://google.com", null);
        assertFalse(isValid);
    }

    @Test
    void areNonIANATLDsRejected() {
        final boolean isValid = new DomainValidator().isValid("google.d", null);
        assertFalse(isValid);
    }

    @Test
    void areIPAddressesRejected() {
        final boolean isValid = new DomainValidator().isValid("256.231.129.9", null);
        assertFalse(isValid);
    }

    @Test
    void areCommaOnlysRejected() {
        final boolean isValid = new DomainValidator().isValid(",,,,,.,,,,,,com", null);
        assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"://", "google", "://google", "://google.com", "://google.com/"})
    void areUrlsWithEmptyDomainRejected(final String invalidUrl) {
        final boolean isValid = new DomainValidator().isValid(invalidUrl, null);
        assertFalse(isValid);
    }

    @Test
    void areAllIANATLDsAllowed() {
        final DomainValidator underTest = new DomainValidator();

        final Set<String> failedTLDs = getIANAValidTLDs().stream()
                .filter(tld -> !underTest.isValid("hostname." + tld, null))
                .collect(Collectors.toSet());

        if(!failedTLDs.isEmpty()) {
            throw new AssertionError("These TLDs were erroneously considered invalid: " + failedTLDs);
        }
    }

    private static Set<String> getIANAValidTLDs() {
        final String IANA_VALID_TLDS_FILENAME = "/iana_valid_domains.txt";
        final var inputStream = Objects.requireNonNull(DomainValidatorTest.class.getResourceAsStream(IANA_VALID_TLDS_FILENAME));
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException("Expected this file to exist in resources directory: " + IANA_VALID_TLDS_FILENAME, e);
        }
    }

}