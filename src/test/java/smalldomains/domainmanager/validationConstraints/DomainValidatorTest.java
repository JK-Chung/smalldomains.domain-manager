package smalldomains.domainmanager.validationConstraints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

}