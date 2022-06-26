package smalldomains.domainmanager.entity;

public record SmallDomainEntity(
        String smallDomain,
        String largeDomain,
        Long createdAt,
        Long expiringAt
) {

}
