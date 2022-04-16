package smalldomains.domainmanager.behaviourTests.stepDefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import smalldomains.domainmanager.behaviourTests.dynamoDBInstance.LocalDynamoDBOperations;
import smalldomains.domainmanager.entity.SmallDomain;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class SuccessStepDefs {

    // even though this field isn't used, it should be included to ensure that local DynamoDB is refreshed after every scenario
    private final LocalDynamoDBOperations localDynamoDBOperations;
    private final DynamoDbAsyncClient dynamoClient;
    private final String dynamoDbTableName;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;
    private final HttpRequest.Builder appRequestBuilder;
    private HttpResponse<String> appResponse;

    @Autowired
    public SuccessStepDefs(
            LocalDynamoDBOperations localDynamoDBOperations,
            DynamoDbAsyncClient dynamoClient,
            ObjectMapper objectMapper,
            HttpClient httpClient,
            @Value("${dynamodb.tablename}") String dynamoDbTableName,
            @Value("${local.server.port}") int appPort
    ) {
        this.localDynamoDBOperations = localDynamoDBOperations;
        this.dynamoClient = dynamoClient;
        this.dynamoDbTableName = dynamoDbTableName;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.appRequestBuilder = HttpRequest.newBuilder(URI.create("http://localhost:%d/".formatted(appPort)));
    }

    @Before
    public void before() {
        this.localDynamoDBOperations.createTable();
    }

    @After
    public void after() {
        this.localDynamoDBOperations.destroyTable();
    }

    @Given("the application is ready")
    public void theApplicationIsReady() {
        assertTrue(isDynamoTableEmpty());
    }

    @SneakyThrows
    @And("a SmallDomain of {word} does not yet exist")
    public void aSmallDomainOfSmallDoesNotYetExist(final String smallDomain) {
        final var response = dynamoClient.getItem(GetItemRequest.builder()
                        .tableName(dynamoDbTableName)
                        .consistentRead(true)
                        .key(generateKey(smallDomain))
                .build())
                .get(5, SECONDS);

         assertFalse(response.hasItem());
    }

    @SneakyThrows
    @And("my request is to create a SmallDomain of {word} \\(redirecting to {word})")
    public void myRequestIsToCreateASmallDomainOfSmallRedirectingToLarge(final String small, final String large) {
        final SmallDomain newSmallDomain = SmallDomain.builder()
                .smallDomain(small)
                .bigDomain(large)
                .build();
        final String smallDomainJson = objectMapper.writeValueAsString(newSmallDomain);

        final var request = appRequestBuilder
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(smallDomainJson))
                .build();

        this.appResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @SneakyThrows
    @Then("a SmallDomain of {word} \\(redirecting to {word}) should be created")
    public void aSmallDomainOfSmallRedirectingToLargeShouldBeCreated(final String small, final String large) {
        final var request = GetItemRequest.builder()
                .tableName(dynamoDbTableName)
                .key(generateKey(small))
                .consistentRead(true)
                .build();

        final var response = dynamoClient
                .getItem(request)
                .get(5, SECONDS)
                .item()
                .entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().s()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertAll(
                () -> assertEquals(2, response.size()),
                () -> assertEquals(small, response.get("small_url")),
                () -> assertEquals(large, response.get("big_url"))
        );
    }

    @And("my response code should be {int}")
    public void myResponseCodeShouldBe(final int expectedResponseCode) {
        assertEquals(expectedResponseCode, appResponse.statusCode());
    }

    @SneakyThrows
    @And("my response body should be the SmallDomain of {word} \\(redirecting to {word})")
    public void myResponseBodyShouldBeTheNewlyCreatedSmallDomain(final String small, final String large) {
        final Map<String, Object> responseJson = objectMapper.readValue(
                appResponse.body(),
                new TypeReference<>() {}
        );

        assertAll(
                () -> assertEquals(2, responseJson.size()),
                () -> assertEquals(small, responseJson.get("smallDomain")),
                () -> assertEquals(large, responseJson.get("bigDomain"))
        );
    }

    @SneakyThrows
    private boolean isDynamoTableEmpty() {
        final var response = dynamoClient.scan(
                ScanRequest
                    .builder()
                    .tableName(dynamoDbTableName)
                    .build())
                .get(5, SECONDS);

        return response.count() == 0;
    }

    private Map<String, AttributeValue> generateKey(final String smallDomain) {
        return Map.of("small_url", AttributeValue.builder().s(smallDomain).build());
    }

}
