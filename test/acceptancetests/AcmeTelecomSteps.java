package acceptancetests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Assert;

import com.acmetelecom.BillingSystem;
import com.acmetelecom.ListCallLog;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public class AcmeTelecomSteps {

    private BillingSystem billingSystem;
    private PrintStream tempOut;
    private ByteArrayOutputStream tempOutContent;
    private final PrintStream out = System.out;

    @BeforeScenario
    public void init() {
	billingSystem = new BillingSystem(new ListCallLog());
	tempOutContent = new ByteArrayOutputStream();
	tempOut = new PrintStream(tempOutContent);
	System.setOut(tempOut);
    }

    @AfterScenario
    public void cleanUp() {
	System.out.flush();
	System.setOut(out);
    }

    /* -------------- GIVEN ---------------- */

    @Given("user $user1No called user $user2No for $seconds seconds")
    public void given_user_called_another_user(final String user1No,
	    final String user2No, final int seconds)
	    throws InterruptedException {
	billingSystem.callInitiated(user1No, user2No);
	sleepSeconds(seconds);
	billingSystem.callCompleted(user1No, user2No);
    }

    /* -------------- WHEN ---------------- */

    @When("all the bills are generated")
    public void when_the_bills_are_generated() {
	billingSystem.createCustomerBills();
    }

    @When("user $user1No calls user $user2No")
    public void when_user_calls(final String user1No, final String user2No)
	    throws InterruptedException {
	billingSystem.callInitiated(user1No, user2No);
    }

    /* -------------- THEN ---------------- */

    @Then("the bill for user $userNo contains $calls calls")
    public void then_the_bill_contains_calls(final String userNo,
	    final int expectedCalls) {
	final Elements elements = Jsoup.parse(tempOutContent.toString())
		.getElementsContainingOwnText(userNo);
	int actualCalls = 0;
	if (elements != null && elements.first() != null
		&& elements.first().nextElementSibling() != null
		&& elements.first().nextElementSibling().childNodeSize() > 1) {
	    actualCalls = Iterables.size(Iterables.filter(elements.first()
		    .nextElementSibling().childNode(1).childNodes(),
		    new Predicate<Node>() {
			@Override
			public boolean apply(final Node n) {
			    return Strings.isNullOrEmpty(n.toString().trim());
			}
		    })) - 1;
	}
	Assert.assertEquals(expectedCalls, actualCalls);
    }

    /* -------------- HELPERS ---------------- */

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * 1000);
    }
}