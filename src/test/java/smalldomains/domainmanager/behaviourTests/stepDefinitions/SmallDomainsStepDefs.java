package smalldomains.domainmanager.behaviourTests.stepDefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import smalldomains.domainmanager.behaviourTests.dynamoDBInstance.LocalDynamoDBOperations;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

public class SmallDomainsStepDefs {

    private final LocalDynamoDBOperations localDynamoDBOperations;
    private final DynamoDbAsyncClient dynamoClient;
    private final String dynamoDbTableName;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;
    private final HttpRequest.Builder appRequestBuilder;
    private HttpResponse<String> appResponse;

    @Autowired
    public SmallDomainsStepDefs(
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
        this.appRequestBuilder = HttpRequest.newBuilder(URI.create("http://localhost:%d/smalldomains/".formatted(appPort)));
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
    @Given("a SmallDomain of {word} does not yet exist")
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
    @When("I try to retrieve a SmallDomain of {word}")
    public void myRequestIsToCreateASmallDomainOfSmallRedirectingToLarge(final String small) {
        final var request = appRequestBuilder
                .uri(appRequestBuilder.build().uri().resolve(small))
                .GET()
                .build();

        this.appResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @Then("my response code should be {int}")
    public void myResponseCodeShouldBe(final int expectedResponseCode) {
        assertEquals(expectedResponseCode, appResponse.statusCode());
    }

    @SneakyThrows
    @Then("my response body should be the SmallDomain of {word} \\(redirecting to {word})")
    public void myResponseBodyShouldBeTheNewlyCreatedSmallDomain(final String small, final String large) {
        final Map<String, Object> responseJson = objectMapper.readValue(
                appResponse.body(),
                new TypeReference<>() {}
        );

        assertAll(
                () -> assertEquals(2, responseJson.size()),
                () -> assertEquals(small, responseJson.get("smallDomain")),
                () -> assertEquals(large, responseJson.get("largeDomain"))
        );
    }

    @SneakyThrows
    @Then("my response body should match the standard error response")
    public void myResponseBodyShouldMatchTheStandardErrorResponse() {
        final Map<String, Object> responseJson = objectMapper.readValue(
                appResponse.body(),
                new TypeReference<>() {}
        );

        assertAll(
                () -> assertTrue(responseJson.size() >= 5),
                () -> assertTrue(responseJson.containsKey("timestamp")),
                () -> assertTrue(responseJson.containsKey("requestId")),
                () -> assertTrue(responseJson.containsKey("path")),
                () -> assertTrue(responseJson.containsKey("method")),
                () -> assertTrue(responseJson.containsKey("error"))
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
        return Map.of("smallDomain", AttributeValue.builder().s(smallDomain).build());
    }

}
