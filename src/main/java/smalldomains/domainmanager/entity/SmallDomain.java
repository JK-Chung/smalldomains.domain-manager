package smalldomains.domainmanager.entity;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

/**
 * A POJO representing the item's structure in the DynamoDB table.
 * Additionally, it represents the expected data structure for the REST service this application exposes
 * (may need to create two separate classes for this if the two structures deviate)
 */
@Value
@Jacksonized @Builder
public class SmallDomain {
    @NotBlank
    String smallDomain;
    @NotBlank
    String bigDomain;
}
