package smalldomains.domainmanager.entity;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized @Builder
public class SmallDomain {
    String smallDomain;
    String bigDomain;
}
