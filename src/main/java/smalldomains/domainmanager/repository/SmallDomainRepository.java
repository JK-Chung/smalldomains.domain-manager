package smalldomains.domainmanager.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.mapper.SmallDomainMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Repository
public class SmallDomainRepository {
    private final DynamoDbAsyncClient client;
    private final String tableName;

    public SmallDomainRepository(DynamoDbAsyncClient client, @Value("${dynamodb.tablename}") String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public CompletableFuture<Optional<SmallDomain>> getSmallDomain(final String smallDomain) {
        final var request = GetItemRequest.builder()
                .tableName(tableName)
                .key(generateKey(smallDomain))
                .consistentRead(false)
                .build();

        return client.getItem(request)
                .thenApply(SmallDomainRepository::extractSmallDomainFromItem);
    }

    public CompletableFuture<SmallDomain> saveSmallDomain(final SmallDomain toSave) {
        final var saveable = SmallDomainMapper.toItem(toSave);

        final var request = PutItemRequest.builder()
                .tableName(tableName)
                .item(saveable)
                .build();

        return client.putItem(request)
                .thenApply(response -> toSave)
                .whenComplete((newSmallDomain, possibleException) -> {
                    if (possibleException == null) {
                        log.info("successfully saved new small domain {}", newSmallDomain);
                    } else {
                        log.error("unable to save {} due to exception {}", newSmallDomain, possibleException);
                    }
                });
    }

    private Map<String, AttributeValue> generateKey(final String smallDomain) {
        return Map.of("small_url", AttributeValue.builder().s(smallDomain).build());
    }

    private static Optional<SmallDomain> extractSmallDomainFromItem(GetItemResponse r) {
        if (r.hasItem()) {
            return Optional.of(SmallDomainMapper.fromItem(r.item()));
        } else {
            return Optional.empty();
        }
    }
}
