package smalldomains.domainmanager.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import smalldomains.domainmanager.entity.SmallDomainEntity;
import smalldomains.domainmanager.restDto.SmallDomainDto;
import smalldomains.domainmanager.mapper.SmallDomainMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Manages all operations (CRUD, etc) regarding the SmallDomains DynamoDB table
 */
@Slf4j
@Repository
public class SmallDomainRepository {
    private final DynamoDbAsyncClient client;
    private final String tableName;

    public SmallDomainRepository(DynamoDbAsyncClient client, @Value("${dynamodb.tablename}") String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public CompletableFuture<Optional<SmallDomainEntity>> getSmallDomain(final String smallDomain) {
        final var request = GetItemRequest.builder()
                .tableName(tableName)
                .key(generateKey(smallDomain))
                .consistentRead(false)
                .build();

        return client.getItem(request)
                .thenApply(SmallDomainRepository::extractSmallDomainFromItem);
    }

    public CompletableFuture<SmallDomainEntity> saveSmallDomain(final SmallDomainEntity toSave) {
        final var saveable = SmallDomainMapper.entityToItem(toSave);

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
                        log.error("unable to save {} due to exception", newSmallDomain, possibleException);
                    }
                });
    }

    public CompletableFuture<Boolean> isTableStillAvailable() {
        final var request = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();

        return client.describeTable(request)
                .thenApply(DescribeTableResponse::table)
                .thenApply(TableDescription::tableStatus)
                .thenApply(TableStatus.ACTIVE::equals)
                .exceptionally(exception -> {
                    // this will also handle the case that the table does not exist at all (ResourceNotFoundException)
                    log.error("Encountered the following exception when checking access to table {}", tableName, exception);
                    return false;
                });
    }

    private Map<String, AttributeValue> generateKey(final String smallDomain) {
        return Map.of("smallDomain", AttributeValue.builder().s(smallDomain).build());
    }

    private static Optional<SmallDomainEntity> extractSmallDomainFromItem(GetItemResponse r) {
        if (r.hasItem()) {
            return Optional.of(SmallDomainMapper.itemToEntity(r.item()));
        } else {
            return Optional.empty();
        }
    }
}
