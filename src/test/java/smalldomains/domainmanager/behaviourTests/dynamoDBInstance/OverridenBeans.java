package smalldomains.domainmanager.behaviourTests.dynamoDBInstance;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/**
 * TestConfiguration class holding all beans used for testing. These beans will override the beans from the main
 * application code.
 *
 * Intended to override beans which have any type of external dependency/calls; its beans allows our tests to be run
 * isolated and dependency-free
 */
@TestConfiguration
public class OverridenBeans {

    @Bean
    @Primary
    public DynamoDbAsyncClient dynamoDbAsyncClient(final DynamoDBLocalServer dynamoDBLocalServer) {
        return dynamoDBLocalServer.getClient();
    }

}
