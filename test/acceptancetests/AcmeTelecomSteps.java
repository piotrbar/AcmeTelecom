package acceptancetests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jsoup.Jsoup;

import com.acmetelecom.BillingSystem;

public class AcmeTelecomSteps {

    private BillingSystem billingSystem;
    private PrintStream tempOut;
    private ByteArrayOutputStream tempOutContent;
    private final PrintStream out = System.out;

    @BeforeStories
    public void init() {
	billingSystem = new BillingSystem();
	tempOutContent = new ByteArrayOutputStream();
	tempOut = new PrintStream(tempOutContent);
	System.setOut(tempOut);
    }

    @AfterStories
    public void cleanUp() {
	System.out.flush();
	System.setOut(out);
    }

    /* -------------- GIVEN ---------------- */

    @Given("user $user1No called user $user2No for $seconds seconds")
    public void given_user_called_another_user(final String user1No, final String user2No, final int seconds) throws InterruptedException {
	billingSystem.callInitiated(user1No, user2No);
	sleepSeconds(seconds);
	billingSystem.callCompleted(user1No, user2No);
    }

    /* -------------- WHEN ---------------- */

    @When("all the bills are generated")
    public void when_the_bills_are_generated() {
	billingSystem.createCustomerBills();
    }

    /* -------------- THEN ---------------- */

    @Then("the bill for user $userNo contains $calls calls")
    public void then_user_is_charged(final String userNo, final int expectedCalls) {
	final String html = tempOutContent.toString();
	final int actualCalls = Jsoup.parse(html).getElementsContainingOwnText(userNo).first().nextElementSibling().childNodes().size();
	Assert.assertEquals(expectedCalls, actualCalls);
    }

    /* -------------- HELPERS ---------------- */

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * 1000);
    }
}