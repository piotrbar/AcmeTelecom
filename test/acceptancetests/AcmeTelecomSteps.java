package acceptancetests;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Assert;

import com.acmetelecom.BillGenerator;
import com.acmetelecom.Biller;
import com.acmetelecom.BillingSystem;
import com.acmetelecom.Call;
import com.acmetelecom.CallLog;
import com.acmetelecom.CallTracker;
import com.acmetelecom.FilePrinter;
import com.acmetelecom.ListCallLog;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;
import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class AcmeTelecomSteps {

    private final BillingSystem billingSystem;
    private final CallLog callLog;
    private final Biller biller;
    private final CallTracker tracker;
    private final BillGenerator generator;
    private final FilePrinter printer;

    final JUnit4Mockery context;
    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;
    private final List<Customer> customers;

    private static final int MILLIS_PER_HOUR = 3600000;
    private static final int MILLIS_PER_SECOND = 1000;
    private static final Random RANDOM = new Random();

    private static final Logger LOG = LogManager.getLogger(AcmeTelecomSteps.class);

    public AcmeTelecomSteps() {
	this.context = new JUnit4Mockery() {
	    {
		this.setThreadingPolicy(new Synchroniser());
	    }
	};
	this.customerDatabase = this.context.mock(CustomerDatabase.class);
	this.tariffLibrary = this.context.mock(TariffLibrary.class);
	this.customers = Lists.newArrayList();

	this.callLog = new ListCallLog();
	this.tracker = new CallTracker(this.callLog);
	this.printer = (FilePrinter) FilePrinter.getInstance();
	this.generator = new BillGenerator(this.printer);
	this.biller = new Biller(this.callLog, this.tariffLibrary, this.customerDatabase, this.generator);
	this.billingSystem = new BillingSystem(this.biller, this.tracker);
    }

    @AfterScenario
    public void cleanUp() {
	this.printer.flush();
	this.callLog.clearCompletedCalls();
	this.customers.clear();
    }

    /* -------------- GIVEN ---------------- */

    @Given("users $users with $tariffType tariff exist in the database")
    public void given_user_exists(final List<String> users, final String tariffType) {
	for (final String user : users) {
	    this.customers.add(new Customer(user, String.valueOf(this.nextRandomPhoneNumber()), tariffType));
	}
	this.context.checking(new Expectations() {
	    {
		this.allowing(AcmeTelecomSteps.this.customerDatabase).getCustomers();
		this.will(returnValue(AcmeTelecomSteps.this.customers));
	    }
	});
	this.context.checking(new Expectations() {
	    {
		for (final Customer customer : AcmeTelecomSteps.this.customers) {
		    this.allowing(AcmeTelecomSteps.this.tariffLibrary).tarriffFor(customer);
		    this.will(returnValue(Tariff.valueOf(customer.getPricePlan())));
		}
	    }
	});
    }

    @Given("user $user1 called user $user2 for $seconds seconds")
    public void given_user_called_another_user(final String user1, final String user2, final int seconds) throws InterruptedException {
	final String user1No = this.getUserNumber(user1);
	final String user2No = this.getUserNumber(user2);
	this.billingSystem.callInitiated(user1No, user2No);
	sleepSeconds(seconds);
	this.billingSystem.callCompleted(user1No, user2No);
    }

    @Given("user $user1 called user $user2 for $seconds seconds at $hour")
    public void given_user_called_another_user_at_hour(final String user1, final String user2, final int seconds, final String hour) {
	final String user1No = this.getUserNumber(user1);
	final String user2No = this.getUserNumber(user2);

	final long startTime = new DateTime().withTimeAtStartOfDay().getMillis() + this.convertToHour(hour) * MILLIS_PER_HOUR;
	final Call call = new Call(user1No, user2No, startTime, startTime + seconds * MILLIS_PER_SECOND);
	this.callLog.addCall(call);
    }

    @Given("user $user1 did not make any calls")
    public void given_user_did_not_make_any_calls(final String user1) {
    }

    /* -------------- WHEN ---------------- */

    @When("all the bills are generated")
    public void when_the_bills_are_generated() {
	this.billingSystem.createCustomerBills();
    }

    @When("user $user1 calls user $user2")
    public void when_user_calls(final String user1, final String user2) throws InterruptedException {
	final String user1No = this.getUserNumber(user1);
	final String user2No = this.getUserNumber(user2);

	this.billingSystem.callInitiated(user1No, user2No);
    }

    /* -------------- THEN ---------------- */

    @Then("the bill for user $user contains $calls calls")
    public void then_the_bill_contains_calls(final String user, final int expectedCalls) {
	final String userPattern = user + "/" + this.getUserNumber(user);
	final Elements elements = Jsoup.parse(this.printer.getBill()).getElementsContainingOwnText(userPattern);
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

    @Then("the total for user $user is $expectedTotal")
    public void then_the_total_is(final String user, final double expectedTotal) {
	final String userNo = this.getUserNumber(user);
	final Elements elements = Jsoup.parse(this.printer.getBill()).getElementsContainingOwnText(userNo);
	double actualTotal = 0;
	if (elements != null && elements.first() != null && elements.first().nextElementSibling() != null) {
	    final String totalString = elements.first().nextElementSibling().nextElementSibling().text();
	    actualTotal = this.retrieveDouble(totalString);
	}
	Assert.assertEquals(expectedTotal, actualTotal, 0.001);
    }

    /* -------------- HELPERS ---------------- */

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * MILLIS_PER_SECOND);
    }

    private double retrieveDouble(final String totalString) {
	final String theDigits = CharMatcher.DIGIT.or(CharMatcher.anyOf(".")).retainFrom(totalString);
	if (Strings.isNullOrEmpty(theDigits)) {
	    final String errorMessage = String.format("Given string: '%s' could not be matched to a double value", totalString);
	    LOG.error(errorMessage);
	    throw new IllegalArgumentException(errorMessage);
	}
	return Double.parseDouble(theDigits);
    }

    private int convertToHour(final String hourString) {
	final Pattern pattern = Pattern.compile("([0-9]{1,2})(am|pm)");
	final Matcher matcher = pattern.matcher(hourString);
	if (!matcher.matches()) {
	    final String errorMessage = String.format("Given string: '%s' could not be matched to an hour", hourString);
	    LOG.error(errorMessage);
	    throw new IllegalArgumentException(errorMessage);
	}
	int hour = Integer.parseInt(matcher.group(1));
	assert (hour > 0 && hour <= 12);
	if (matcher.group(2).equals("pm")) {
	    hour += 12;
	}
	return hour;
    }

    private String getUserNumber(final String name) {
	return Iterables.find(this.customers, new Predicate<Customer>() {
	    @Override
	    public boolean apply(final Customer c) {
		return c.getFullName().equals(name);
	    }
	}).getPhoneNumber();
    }

    private long nextRandomPhoneNumber() {
	return Math.abs(RANDOM.nextLong());
    }
}