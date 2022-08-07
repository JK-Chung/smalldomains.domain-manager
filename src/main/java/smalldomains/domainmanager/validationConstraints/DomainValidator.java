package smalldomains.domainmanager.validationConstraints;

import io.netty.util.internal.StringUtil;
import org.apache.commons.validator.UrlValidator;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DomainValidator implements ConstraintValidator<ValidDomain, String> {

    private static final String IANA_VALID_TLDS_FILENAME = "/iana_valid_domains.txt";
    private static final Pattern HAS_TLD_REGEX = Pattern.compile("[^.]+\\.[^.]+$");
    private final Set<String> VALID_TLDS = getIANAValidTLDs();
    private final UrlValidator urlValidator = new UrlValidator();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.startsWith("ftp://")) {
            return false;
        }

        final String toTest = value.startsWith("https://") || value.startsWith("http://") ? value : "https://" + value;

        try {
            final URL url = new URL(toTest);
            return urlValidator.isValid(toTest) && hasValidTLD(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasValidTLD(final URL url) {
        final String[] tokenizedHost = url.getHost().split("\\.");
        if (tokenizedHost.length == 0) return false;
        final String tld = tokenizedHost[tokenizedHost.length - 1].toLowerCase();
        return !StringUtil.isNullOrEmpty(tld) && VALID_TLDS.contains(tld);
    }

    /**
     * File sourced from: https://data.iana.org/TLD/tlds-alpha-by-domain.txt
     * @return an authoritative collection of valid TLDs according to IANA
     */
    private Set<String> getIANAValidTLDs() {
        final var inputStream = Objects.requireNonNull(this.getClass().getResourceAsStream(IANA_VALID_TLDS_FILENAME));
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (IOException e) {
            throw new IllegalStateException("Expected this file to exist in resources directory: " + IANA_VALID_TLDS_FILENAME, e);
        }
    }


}
