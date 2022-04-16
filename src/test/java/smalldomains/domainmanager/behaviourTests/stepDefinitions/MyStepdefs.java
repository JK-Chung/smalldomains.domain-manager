package smalldomains.domainmanager.behaviourTests.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import smalldomains.domainmanager.behaviourTests.dynamoDBInstance.DynamoDBInstance;

public class MyStepdefs {

    @Autowired
    private DynamoDBInstance dynamoDBInstance;

    @Given("^I tag a scenario$")
    public void MyStepdefs1() {
        System.out.println(dynamoDBInstance);
    }

    @When("^I select tests with that tag for execution$")
    public void MyStepdefs2() {

    }

    @Then("^my tagged scenario is executed$")
    public void MyStepdefs3() {

    }
}
