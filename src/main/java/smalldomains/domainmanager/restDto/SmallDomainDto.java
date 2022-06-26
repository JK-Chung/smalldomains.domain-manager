package smalldomains.domainmanager.restDto;

public record SmallDomainDto(
        String smallDomain,
        String largeDomain,
        Long createdAt,
        Long expiringAt
) {

}