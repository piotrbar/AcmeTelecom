package acceptancetests;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private int start;
    private int finish;
    private int offpeakPrice;
    private int peakPrice;

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
    public void given_user_called_another_user(final String user1No, final String user2No, final int seconds) throws InterruptedException {
	billingSystem.callInitiated(user1No, user2No);
	sleepSeconds(seconds);
	billingSystem.callCompleted(user1No, user2No);
    }

    @Given("peak starts at $start and finishes at $finish")
    public void peak_starts_and_finishes(final String start, final String finish) {
	this.start = convertToHour(start);
	this.finish = convertToHour(finish);
	assert (this.start <= this.finish);
	assert (this.start > 0);
    }

    @Given("peak charge is $peakPrice p/min and offpeak charge is $offpeakPrice p/min")
    public void price_setup(final int peakPrice, final int offpeakPrice) {
	this.peakPrice = peakPrice;
	this.offpeakPrice = offpeakPrice;
    }

    @Given("user $user1No called user $user2No for $seconds seconds at $hour")
    public void given_user_called_another_user_at_hour(final String user1No, final String user2No, final int seconds, final String hour) {
	fail("Not yet implemented"); // TODO
    }

    /* -------------- WHEN ---------------- */

    @When("all the bills are generated")
    public void when_the_bills_are_generated() {
	billingSystem.createCustomerBills();
    }

    @When("user $user1No calls user $user2No")
    public void when_user_calls(final String user1No, final String user2No) throws InterruptedException {
	billingSystem.callInitiated(user1No, user2No);
    }

    /* -------------- THEN ---------------- */

    @Then("the bill for user $userNo contains $calls calls")
    public void then_the_bill_contains_calls(final String userNo, final int expectedCalls) {
	final Elements elements = Jsoup.parse(tempOutContent.toString()).getElementsContainingOwnText(userNo);
	int actualCalls = 0;
	if (elements != null && elements.first() != null && elements.first().nextElementSibling() != null
		&& elements.first().nextElementSibling().childNodeSize() > 1) {
	    actualCalls = Iterables.size(Iterables.filter(elements.first().nextElementSibling().childNode(1).childNodes(), new Predicate<Node>() {
		@Override
		public boolean apply(final Node n) {
		    return Strings.isNullOrEmpty(n.toString().trim());
		}
	    })) - 1;
	}
	Assert.assertEquals(expectedCalls, actualCalls);
    }

    @Then("Then the total for user $user is $total")
    public void then_the_total_is(final String user, final double total) {
	fail("Not yet implemented"); // TODO
    }

    /* -------------- HELPERS ---------------- */

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * 1000);
    }

    private int convertToHour(final String hourString) {
	final Pattern pattern = Pattern.compile("([1-9]{1,2})(am|pm)");
	final Matcher matcher = pattern.matcher(hourString);

	int hour = Integer.parseInt(matcher.group(1));
	assert (hour > 0 && hour <= 12);
	if (matcher.group(2).equals("pm")) {
	    hour += 12;
	}
	return hour;
    }
}