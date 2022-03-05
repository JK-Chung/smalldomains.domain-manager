package smalldomains.domainmanager.exception;

import lombok.Getter;

@Getter
public class SmallDomainAlreadyExistsException extends RuntimeException {
    private final String alreadyExistingDomain;

    public SmallDomainAlreadyExistsException(String alreadyExistingDomain) {
        super(alreadyExistingDomain + " already exists as a small domain");
        this.alreadyExistingDomain = alreadyExistingDomain;
    }

}
