package smalldomains.domainmanager.restDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record CreateRandomSmallDomainRequest(
        @NotBlank @Pattern(regexp = ALPHA_NUM_REGEX) String largeDomain
) {
    private static final String ALPHA_NUM_REGEX = "^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$";
}
