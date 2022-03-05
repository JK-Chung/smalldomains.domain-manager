package smalldomains.domainmanager.exception;

import lombok.Getter;

@Getter
public class NoSmallDomainExists extends RuntimeException {
    private final String nameOfNonExistentDomain;

    public NoSmallDomainExists(final String nameOfNonExistentDomain) {
        super(nameOfNonExistentDomain + " does not exist as small domain");
        this.nameOfNonExistentDomain = nameOfNonExistentDomain;
    }
}
