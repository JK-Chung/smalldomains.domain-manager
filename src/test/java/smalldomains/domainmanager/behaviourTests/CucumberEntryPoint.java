package smalldomains.domainmanager.behaviourTests;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Acts as the Cucumber Entrypoint for JUnit 5. Configures things like where to find step-definitions and feature files
 *
 * https://github.com/cucumber/cucumber-jvm/tree/main/junit-platform-engine#suites-with-different-configurations
 */
@Suite
@SuiteDisplayName("Cucumber Behaviour Tests")
@IncludeEngines("cucumber")
// Select Resource Path holding our feature files
@SelectClasspathResource("behaviourTests")
// Select Module holding Glue classes: step definitions, cucumber config classes, etc
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "smalldomains.domainmanager.behaviourTests")
public class CucumberEntryPoint {
}
