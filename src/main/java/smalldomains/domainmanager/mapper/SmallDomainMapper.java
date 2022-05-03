package smalldomains.domainmanager.mapper;

import smalldomains.domainmanager.entity.SmallDomain;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Maps between the SmallDomain POJO and its equivalent in the DynamoDB table
 */
public class SmallDomainMapper {

    public static SmallDomain fromItem(final Map<String, AttributeValue> item) {
        return new SmallDomain(
                item.get("small-url").s(),
                item.get("large-url").s()
        );
    }

    public static Map<String, AttributeValue> toItem(final SmallDomain smallDomain) {
        return Map.of(
                "small-url", generateStringAttributeValue(smallDomain.smallDomain()),
                "large-url", generateStringAttributeValue(smallDomain.bigDomain())
        );
    }

    private static AttributeValue generateStringAttributeValue(final String value) {
        return AttributeValue.builder()
                .s(value)
                .build();
    }
}
