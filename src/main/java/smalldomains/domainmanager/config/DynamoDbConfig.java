package smalldomains.domainmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class DynamoDbConfig {

    /**
     * Configures the DynamoDBClient to call whichever table is necessary
     * @param region location of table
     * @return an async client
     */
    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(@Value("${dynamodb.region}") String region) {
        return DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}
