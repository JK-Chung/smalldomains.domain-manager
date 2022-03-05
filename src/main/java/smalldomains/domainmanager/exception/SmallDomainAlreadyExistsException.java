package smalldomains.domainmanager.exception;

import lombok.Getter;

/**
 * An exception representing the fact that a small domain already exists
 */
@Getter
public class SmallDomainAlreadyExistsException extends RuntimeException {
    private final String alreadyExistingDomain;

    public SmallDomainAlreadyExistsException(String alreadyExistingDomain) {
        super(alreadyExistingDomain + " already exists as a small domain");
        this.alreadyExistingDomain = alreadyExistingDomain;
    }

}
