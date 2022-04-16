package smalldomains.domainmanager.behaviourTests.dynamoDBInstance;

import io.cucumber.spring.ScenarioScope;
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
// Annotated with ScenarioScope - meaning that this component is started up and torn down before and after each Cucumber scenario
@ScenarioScope
public class DynamoDBInstance implements AutoCloseable {
    private final DynamoDbAsyncClient dynamoDbClient;
    private final String dynamoDbTableName;

    public DynamoDBInstance(
            DynamoDBLocalServer dynamoDBLocalServer,
            @Value("${dynamodb.tablename}") String dynamoDbTableName
    ) {
        this.dynamoDbClient = dynamoDBLocalServer.getClient();
        this.dynamoDbTableName = dynamoDbTableName;

        this.createTable();
    }

    @Override
    public void close() throws Exception {
        this.destroyTable();
    }

    @SneakyThrows
    private void createTable() {
        final var request = CreateTableRequest.builder()
                .tableName(dynamoDbTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("small_url")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("small_url")
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
    private void destroyTable() {
        final var request = DeleteTableRequest.builder()
                .tableName(dynamoDbTableName)
                .build();

        dynamoDbClient.deleteTable(request).get(5, TimeUnit.SECONDS);
        log.info("Deleted " + dynamoDbTableName + " table");
    }

}
