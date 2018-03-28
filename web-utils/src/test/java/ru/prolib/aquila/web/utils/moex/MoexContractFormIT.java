package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.finam.Fidexp;

public class MoexContractFormIT {
	private static final double EXPECTED_AVAILABILITY = 85;
	private static final org.slf4j.Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MoexContractFormIT.class);
	}
	
	private WebDriver webDriver;
	private MoexContractForm contractForm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
		Logger.getLogger("ru.prolib.aquila.web.utils").setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		//webDriver = WebDriverFactory.createFirefoxDriver();
		webDriver = WebDriverFactory.createJBrowserDriver();
		
		contractForm = new MoexContractForm(webDriver);
	}
	
	protected void closeWebDriver() {
		if ( webDriver != null ) {
			try {
				webDriver.close();
			} catch ( WebDriverException e ) {
				// JBrowserDriver bug when closing
			}
		}
	}
	
	@After
	public void tearDown() throws Exception {
		closeWebDriver();
	}
	
	@Test
	public void testGetInstrumentDescription() throws Exception {
		Map<Integer, Object> actual = contractForm.getInstrumentDescription("RTS-12.17");
		
		Map<Integer, Object> expected = new LinkedHashMap<>();
		expected.put(MoexContractField.SYMBOL, "RTS-12.17");
		expected.put(MoexContractField.SYMBOL_CODE, "RIZ7");
		expected.put(MoexContractField.CONTRACT_DESCR, "RTS Index Futures");
		expected.put(MoexContractField.TYPE, "Futures");
		expected.put(MoexContractField.SETTLEMENT, "Cash-Settled");
		expected.put(MoexContractField.LOT_SIZE, CDecimalBD.of(1L));
		expected.put(MoexContractField.QUOTATION, "points");
		expected.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2015, 12, 25));
		expected.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2017, 12, 21));
		expected.put(MoexContractField.DELIVERY, LocalDate.of(2017, 12, 21));
		// Some fields are not available for archived contracts
		//expected.put(MoexContractField.TICK_SIZE, CDecimalBD.of(10L));
		//expected.put(MoexContractField.TICK_VALUE, CDecimalBD.ofRUB5("11.67354"));
		//expected.put(MoexContractField.LOWER_PRICE_LIMIT, CDecimalBD.of("106710"));
		//expected.put(MoexContractField.UPPER_PRICE_LIMIT, CDecimalBD.of("116770"));
		//expected.put(MoexContractField.SETTLEMENT_PRICE, CDecimalBD.of("111740"));
		//expected.put(MoexContractField.FEE, CDecimalBD.ofRUB2("2.61"));
		//expected.put(MoexContractField.INTRADAY_FEE, CDecimalBD.of("1.305", "RUB"));
		//expected.put(MoexContractField.NEGOTIATION_FEE, CDecimalBD.ofRUB2("2.61"));
		expected.put(MoexContractField.EXERCISE_FEE, CDecimalBD.of("2", "RUB"));
		//expected.put(MoexContractField.INITIAL_MARGIN, CDecimalBD.of("13153.5", "RUB"));
		//expected.put(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2017, 11, 2));
		expected.put(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 45));
		expected.put(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 30));
		expected.put(MoexContractField.SETTLEMENT_PROC_DESCR, "Cash settlement. An average "
			+ "value of RTS Index calculated during the period from 15:00 to "
			+ "16:00 Moscow time of the last trading day multiplied by 100 is "
			+ "taken as a settlement price. The tick value equals 20% of the "
			+ "USD/RUB exchange rate determined in accordance with the "
			+ "Methodology at 6:30 pm MSK on the last trading day.");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetInstrumentDescription_AvailableContractsConversion() throws Exception {
		List<String> futures = contractForm.getActiveFuturesList();
		logger.debug("Go through {} futures. It may hold up to several minutes.", futures.size());
		int passed = 0, errors = 0;
		for ( String contractCode : futures ) {
			try {
				contractForm.getInstrumentDescription(contractCode);
			} catch ( Exception e ) {
				logger.error("Error obtaining contract info: {}", contractCode, e);
				errors ++;
			}
			Thread.sleep(1000);
			passed ++;
			if ( passed % 20 == 0 ) {
				logger.debug("Passed {} of {}", passed, futures.size());
			}
		}
		logger.debug("Total obtained: {}, errors: {}", futures.size(), errors);
		Thread.sleep(5000L);
	}
	
	@Test
	public void testGetInstrumentDescription_ThrowsIfContractNotFound() throws Exception {
		try {
			contractForm.getInstrumentDescription("ZU1201");
			fail("Expected exception: " + WUWebPageException.class.getSimpleName());
		} catch ( WUWebPageException e ) {
			assertEquals("Contract not exists or page has changed its structure: ZU1201", e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveFuturesList() throws Exception {
		Set<String> finam_list = null;
		try ( Fidexp finam = new Fidexp() ) {
			finam_list = new HashSet<>(finam.getTrueFuturesQuotes(true).values());
		}
		
		Set<String> moex_list = new HashSet<>(contractForm.getActiveFuturesList());
		
		Set<String> x = new HashSet<>(finam_list);
		x.removeAll(moex_list);
		List<String> dummy_list = new ArrayList<>(x);
		Collections.sort(dummy_list);
		logger.debug("Those elements ({}/{}) are not available via MOEX API: ", dummy_list.size(), finam_list.size());
		for ( String dummy : dummy_list ) {
			logger.debug("Not available via MOEX: {}", dummy);
		}
		double availability = moex_list.size() * 100 / finam_list.size();
		logger.debug("Current availability {}% of minimum {}%", availability, EXPECTED_AVAILABILITY);
		assertTrue("Availability is less than expected", availability >= EXPECTED_AVAILABILITY);
	}

}
