package smalldomains.domainmanager.behaviourTests.dynamoDBInstance;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.ServerSocket;
import java.net.URI;

/**
 * This bean is responsible for starting up a local DynamoDB servers on a random local port.
 *
 * Exposes a DynamoDBAsyncClient intended for autowiring and overriding its corresponding application bean. Using this
 * bean ensures that tests have no external dependency... the local DynamoDB will always be used
 */
@Slf4j
@Component
class LocalDynamoDBServer implements AutoCloseable {
    private final DynamoDBProxyServer dynamoDbServer;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final int port;

    public LocalDynamoDBServer() throws Exception {
        // Local DynamoDB uses sqlite... this property is required to allow DynamoDB to use sqlite
        System.setProperty("sqlite4java.library.path", ".native-libs");

        this.port = findAvailablePort();
        final String availablePort = String.valueOf(this.port);
        this.dynamoDbServer = ServerRunner.createServerFromCommandLineArgs(
                new String[] {"-inMemory", "-port", availablePort}
        );

        this.dynamoDbAsyncClient = createClient();

        dynamoDbServer.start();
        log.info("Started Local DynamoDB Server on port: " + port);
    }

    public DynamoDbAsyncClient getClient() {
        return this.dynamoDbAsyncClient;
    }

    @Override
    public void close() throws Exception {
        dynamoDbServer.stop();
        log.info("Stopped Local DynamoDB Server on port: " + port);
    }

    private DynamoDbAsyncClient createClient() {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        // defining placeholder credentials here so that real credentials cannot accidentally be used
                        AwsBasicCredentials.create("fakeAccessKey", "fakeSecretKey")
                ))
                .region(Region.EU_WEST_1)
                .endpointOverride(URI.create("http://localhost:%d".formatted(port)))
                .build();
    }

    private int findAvailablePort() throws Exception {
        try(final ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

}
