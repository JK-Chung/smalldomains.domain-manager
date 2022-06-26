package smalldomains.domainmanager.restDto;

import smalldomains.domainmanager.validationConstraints.ValidDomain;

import javax.validation.constraints.NotBlank;

public record CreateRandomSmallDomainRequest(
        @NotBlank @ValidDomain String largeDomain
) {

}
