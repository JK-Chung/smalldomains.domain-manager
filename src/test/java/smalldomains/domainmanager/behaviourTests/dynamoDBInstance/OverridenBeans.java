package smalldomains.domainmanager.behaviourTests.dynamoDBInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.http.HttpClient;

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
    public DynamoDbAsyncClient dynamoDbAsyncClient(final LocalDynamoDBServer localDynamoDBServer) {
        return localDynamoDBServer.getClient();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
