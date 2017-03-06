package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.FMoney;
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
		webDriver = WebDriverFactory.createJBrowserDriver();
		//webDriver = WebDriverFactory.createFirefoxDriver();
		contractForm = new MoexContractForm(webDriver);
	}
	
	@After
	public void tearDown() throws Exception {
		webDriver.close();
	}
	
	@Test
	public void testGetInstrumentDescription() throws Exception {
		Map<Integer, Object> actual = contractForm.getInstrumentDescription("RTS-6.16");
		
		Map<Integer, Object> expected = new LinkedHashMap<>();
		expected.put(MoexContractField.SYMBOL, "RTS-6.16");
		expected.put(MoexContractField.SYMBOL_CODE, "RIM6");
		expected.put(MoexContractField.CONTRACT_DESCR, "RTS Index Futures");
		expected.put(MoexContractField.TYPE, MoexContractType.FUTURES);
		expected.put(MoexContractField.SETTLEMENT, MoexSettlementType.CASH_SETTLED);
		expected.put(MoexContractField.LOT_SIZE, 1);
		expected.put(MoexContractField.QUOTATION, MoexQuotationType.POINTS);
		expected.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2015, 3, 18));
		expected.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2016, 6, 15));
		expected.put(MoexContractField.DELIVERY, LocalDate.of(2016, 6, 15));
		expected.put(MoexContractField.FEE, new FMoney("2.81", "RUB"));
		expected.put(MoexContractField.INTRADAY_FEE, new FMoney("1.405", "RUB"));
		expected.put(MoexContractField.NEGOTIATION_FEE, new FMoney("2.81", "RUB"));
		expected.put(MoexContractField.EXERCISE_FEE, new FMoney("2.0", "RUB"));
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
		int passed = 0;
		for ( String contractCode : futures ) {
			contractForm.getInstrumentDescription(contractCode);
			Thread.sleep(1000);
			passed ++;
			if ( passed % 20 == 0 ) {
				logger.debug("Passed {} of {}", passed, futures.size());
			}
		}
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
		
		Set<String> dummy_list = new HashSet<>(finam_list);
		dummy_list.removeAll(moex_list);
		logger.debug("Those elements ({}/{}) are not available via MOEX API: ", dummy_list.size(), finam_list.size());
		for ( String dummy : dummy_list ) {
			logger.debug("Not available via MOEX: {}", dummy);
		}
		double availability = moex_list.size() * 100 / finam_list.size();
		logger.debug("Current availability {}% of minimum {}%", availability, EXPECTED_AVAILABILITY);
		assertTrue("Availability is less than expected", availability >= EXPECTED_AVAILABILITY);
	}

}
