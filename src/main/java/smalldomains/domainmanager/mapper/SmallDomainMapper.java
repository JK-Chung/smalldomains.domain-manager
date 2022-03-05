package smalldomains.domainmanager.mapper;

import smalldomains.domainmanager.entity.SmallDomain;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class SmallDomainMapper {

    public static SmallDomain fromItem(final Map<String, AttributeValue> item) {
        return SmallDomain.builder()
                .smallDomain(item.get("small_url").s())
                .bigDomain(item.get("big_url").s())
                .build();
    }

    public static Map<String, AttributeValue> toItem(final SmallDomain smallDomain) {
        return Map.of(
                "small_url", generateStringAttributeValue(smallDomain.getSmallDomain()),
                "big_url", generateStringAttributeValue(smallDomain.getBigDomain())
        );
    }

    private static AttributeValue generateStringAttributeValue(final String value) {
        return AttributeValue.builder()
                .s(value)
                .build();
    }
}
