package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.finam.Fidexp;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class MoexDataExportIT {
	private static final org.slf4j.Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MoexDataExportIT.class);
	}
	
	private WebDriver webDriver;
	private MoexContractForm contractForm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		webDriver = WebDriverFactory.createJBrowserDriver();
		contractForm = new MoexContractForm(webDriver);
	}
	
	@After
	public void tearDown() throws Exception {
		webDriver.close();
	}
	
	protected WebDriver createFirefoxDriver() {
		FirefoxProfile profile = new FirefoxProfile();
    	File ffBinary = new File("D:/Program Files (x86)/Mozilla Firefox/firefox.exe");
    	return new FirefoxDriver(new FirefoxBinary(ffBinary), profile);
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
		expected.put(MoexContractField.FEE, 2.0d);
		expected.put(MoexContractField.INTRADAY_FEE, 1.0d);
		expected.put(MoexContractField.NEGOTIATION_FEE, 2.0d);
		expected.put(MoexContractField.EXERCISE_FEE, 2.0d);
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
		Map<Integer, String> futures;
		try ( Fidexp finam = new Fidexp() ) {
			futures = finam.getTrueFuturesQuotes(true);
		}
		logger.debug("Go through {} futures. It may hold up to several minutes.", futures.size());
		for ( String contractCode : futures.values() ) {
			contractForm.getInstrumentDescription(contractCode);
			Thread.sleep(1000);
		}
	}	

}
