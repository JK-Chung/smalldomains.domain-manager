package smalldomains.domainmanager.validationConstraints;

import org.apache.commons.validator.routines.UrlValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.validator.routines.DomainValidator.ArrayType.GENERIC_PLUS;

public class DomainValidator implements ConstraintValidator<ValidDomain, String> {

    private static final String IANA_VALID_TLDS_FILENAME = "/iana_valid_domains.txt";
    private final UrlValidator urlValidator = new UrlValidator();

    static {
        // to ensure this.urlValidator knows all IANA TLDs, we have to statically configure validator.routines.DomainValidator
        // https://stackoverflow.com/questions/35757298/adding-domain-to-urlvalidator-in-apache-commons
        org.apache.commons.validator.routines.DomainValidator.updateTLDOverride(GENERIC_PLUS, getIANAValidTLDs().toArray(String[]::new));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.startsWith("ftp://")) {
            return false;
        }
        final String toTest = value.startsWith("https://") || value.startsWith("http://") ? value : "https://" + value;

        try {
            new URL(toTest);
            return urlValidator.isValid(toTest);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * File sourced from: <a href="https://data.iana.org/TLD/tlds-alpha-by-domain.txt">...</a>
     * @return an authoritative collection of valid TLDs according to IANA
     */
    private static Set<String> getIANAValidTLDs() {
        final var inputStream = Objects.requireNonNull(DomainValidator.class.getResourceAsStream(IANA_VALID_TLDS_FILENAME));
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .map(String::toLowerCase)
                    // force the use of HashSet to ensure constant lookup times... long validation times will impact user experience
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (IOException e) {
            throw new IllegalStateException("Expected this file to exist in resources directory: " + IANA_VALID_TLDS_FILENAME, e);
        }
    }


}
