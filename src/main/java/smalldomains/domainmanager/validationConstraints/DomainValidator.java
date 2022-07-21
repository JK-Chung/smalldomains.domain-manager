package smalldomains.domainmanager.validationConstraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class DomainValidator implements ConstraintValidator<ValidDomain, String> {

    private static final Pattern HAS_TLD_REGEX = Pattern.compile("[^.]+\\.[^.]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        final String toTest = value.startsWith("https://") || value.startsWith("http://") ? value : "https://" + value;

        try {
            final var url = new URL(toTest);
            return !url.getHost().isBlank() && usingValidScheme(url) && hasTLD(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasTLD(final URL url) {
        final String host = url.getHost();
        return HAS_TLD_REGEX.matcher(host).find();
    }

    private boolean usingValidScheme(final URL url) {
        final String scheme = url.getProtocol();
        return scheme.equals("https") || scheme.equals("http");
    }

}
