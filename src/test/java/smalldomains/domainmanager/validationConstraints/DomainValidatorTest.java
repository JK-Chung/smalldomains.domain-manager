package smalldomains.domainmanager.validationConstraints;

import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
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
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"://", "://google", "://google.com", "://google.com/"})
    void areUrlsWithEmptyDomainRejected(final String invalidUrl) {
        final boolean isValid = new DomainValidator().isValid(invalidUrl, null);
        assertFalse(isValid);
    }

}