package smalldomains.domainmanager.behaviourTests.dynamoDBInstance;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.concurrent.TimeUnit;

/**
 * Integration/behaviour tests have been designed around the use of ONE local DynamoDB server. This class acts as an
 * abstraction over this server which creates tables and deletes tables for each Cucumber scenario: it ensures a clean
 * DynamoDB for each scenario.
 *
 * Intended for direct use by the Cucumber Step-Definitions.
 */
@Slf4j
@Component
public class LocalDynamoDBOperations {
    private final DynamoDbAsyncClient dynamoDbClient;
    private final String dynamoDbTableName;

    public LocalDynamoDBOperations(
            LocalDynamoDBServer localDynamoDBServer,
            @Value("${dynamodb.tablename}") String dynamoDbTableName
    ) {
        this.dynamoDbClient = localDynamoDBServer.getClient();
        this.dynamoDbTableName = dynamoDbTableName;
    }

    @SneakyThrows
    public void createTable() {
        final var request = CreateTableRequest.builder()
                .tableName(dynamoDbTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("smallDomain")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("smallDomain")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(1L)
                        .writeCapacityUnits(1L)
                        .build())
                .build();

        dynamoDbClient.createTable(request).get(5, TimeUnit.SECONDS);
        log.info("Created new " + dynamoDbTableName + " table");
    }

    @SneakyThrows
    public void destroyTable() {
        final var request = DeleteTableRequest.builder()
                .tableName(dynamoDbTableName)
                .build();

        dynamoDbClient.deleteTable(request).get(5, TimeUnit.SECONDS);
        log.info("Deleted " + dynamoDbTableName + " table");
    }

}
