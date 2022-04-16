package smalldomains.domainmanager.behaviourTests;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import smalldomains.domainmanager.behaviourTests.dynamoDBInstance.OverridenBeans;

/**
 * This class configures Spring to work with the Cucumber tests. It enables Spring's dependency injection, allows for
 * additional bean creations and allows for additional Spring configuration. It also defines the TestConfiguration classes
 * holding test beans which will be used to override the main application beans.
 *
 * It will be responsible for starting up the ApplicationContext used by all Cucumber feature tests
 *
 * https://thepracticaldeveloper.com/cucumber-tests-spring-boot-dependency-injection/
 */
@CucumberContextConfiguration
@SpringBootTest(classes = {OverridenBeans.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

}
