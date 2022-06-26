package smalldomains.domainmanager.mapper;

import org.springframework.stereotype.Component;
import smalldomains.domainmanager.entity.SmallDomainEntity;
import smalldomains.domainmanager.restDto.SmallDomainDto;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Maps between the SmallDomain POJO and its equivalent in the DynamoDB table
 */
@Component
public class SmallDomainMapper {

    private SmallDomainMapper() {
        // disallow instantiation
    }

    public static SmallDomainDto entityToDto(final SmallDomainEntity entity) {
        return new SmallDomainDto(
                entity.smallDomain(),
                entity.largeDomain(),
                entity.createdAt(),
                entity.expiringAt()
        );
    }

    public static SmallDomainEntity itemToEntity(final Map<String, AttributeValue> item) {
        return new SmallDomainEntity(
                item.get("smallDomain").s(),
                item.get("largeDomain").s(),
                Long.parseLong(item.get("createdAt").n()),
                Long.parseLong(item.get("expiringAt").n())
        );
    }

    public static Map<String, AttributeValue> entityToItem(final SmallDomainEntity entity) {
        return Map.of(
                "smallDomain", generateStringAttributeValue(entity.smallDomain()),
                "largeDomain", generateStringAttributeValue(entity.largeDomain()),
                "createdAt", generateLongAttributeValue(entity.createdAt()),
                "expiringAt", generateLongAttributeValue(entity.expiringAt())
        );
    }

    private static AttributeValue generateStringAttributeValue(final String value) {
        return AttributeValue.builder()
                .s(value)
                .build();
    }

    private static AttributeValue generateLongAttributeValue(final long value) {
        return AttributeValue.builder()
                .n(value + "")
                .build();
    }

}
