package smalldomains.domainmanager.validationConstraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

public class DomainValidator implements ConstraintValidator<ValidDomain, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        final String toTest = value.startsWith("https://") || value.startsWith("http://") ? value : "https://" + value;

        try {
            final var url = new URL(toTest);
            return !url.getHost().isBlank() && usingValidScheme(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean usingValidScheme(final URL url) {
        final String scheme = url.getProtocol();
        return scheme.equals("https") || scheme.equals("http");
    }

}
