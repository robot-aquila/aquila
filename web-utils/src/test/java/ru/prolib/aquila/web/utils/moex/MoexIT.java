package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.finam.Fidexp;
import ru.prolib.aquila.web.utils.finam.FidexpFactory;
import ru.prolib.aquila.web.utils.finam.FidexpFactorySTD;

public class MoexIT {
	private static final boolean STRICT_COMPLETE_TEST = false;
	
	private static final File	JBROWSER_CONF_FILE =			new File("it-config/jbd.ini"); 
	private static final File	CONTRACT_DATA_FILE = 			new File("it-config/moexIT.contract.ini");
	private static final File	CONTRACT_DATA_FILE_TEMPLATE =	new File("it-config/moexIT.contract.ini-template");
	private static final String	CONTRACT_DATA_INI_SECTION =		"test-contract";
	// Period when test contract data is valid
	// 10 hrs 30 mins = 630 mins = 37800 secs = 37800000 msecs 
	private static final long	CONTRACT_DATA_FILE_TTL = 37800000L; 
	
	private static final String ERMSG_REFER_TO_DATA_FILE = "Refer to " + CONTRACT_DATA_FILE_TEMPLATE + " for detailed info.";
	private static final String ERMSG_POSSIBLE_OUTDATED = "Possible " + CONTRACT_DATA_FILE + " is outdated.\n"
			+ ERMSG_REFER_TO_DATA_FILE;
	private static final String ERMSG_CONTRACT_DATA_MISMATCH = "Expected contract data mismatch to actual data obtained from MICEX.\n"
			+ ERMSG_POSSIBLE_OUTDATED + "\n"
			+ ERMSG_REFER_TO_DATA_FILE;
	
	private static final double EXPECTED_AVAILABILITY = 85;
	private static final org.slf4j.Logger logger;
	private static final Map<Integer, Object> TEST_CONTRACT_DATA = new LinkedHashMap<>();
	private static final boolean firefox = true;
	
	static {
		logger = LoggerFactory.getLogger(MoexIT.class);
	}
	
	private static MoexFactory factory;
	private static FidexpFactory finamFactory;
	private Moex service;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
		Logger.getLogger("ru.prolib.aquila.web.utils").setLevel(Level.DEBUG);
		loadTestContractDetails();
		if ( firefox ) {
			factory = MoexFactorySTD.newFactoryFF(JBROWSER_CONF_FILE, false);
			finamFactory = FidexpFactorySTD.newFactoryFF(JBROWSER_CONF_FILE, false);
		} else {
			factory = MoexFactorySTD.newFactoryJBD(JBROWSER_CONF_FILE, false);
			finamFactory = FidexpFactorySTD.newFactoryJBD(JBROWSER_CONF_FILE, false);
		}
	}
	
	private static void loadTestContractDetails() throws Exception {
		if ( STRICT_COMPLETE_TEST ) {
			// This test checks all fields of active contract.
			// Make sure that CONTRACT_DATA_FILE contain actual data to pass the test.
			loadTestContractDetails_Full();
		} else {
			// This test checks archived contract. Unfortunately, some fields aren't
			// available for such contracts. Drawback - we can test our code partially.
			// Advantage - information of this contract not changed with time and we do
			// not need actualize CONTRACT_DATA_FILE each time it run.
			loadTestContractDetails_Part();
		}
	}
	
	private static void loadTestContractDetails_Part() throws Exception {
		Map<String, String> expected = new LinkedHashMap<>();
		expected.put("Contract Symbol", "RTS-3.19");
		expected.put("Contract Trading Symbol", "RIH9");
		expected.put("Contract Description", "RTS Index Futures");
		expected.put("Type", "Futures");
		expected.put("Settlement", "Cash-Settled");
		expected.put("Сontract size (lot)", "1");
		expected.put("Quotation", "points");
		expected.put("First Trading Day", "10.03.2017");
		expected.put("Last Trading Day", "21.03.2019");
		expected.put("Delivery", "21.03.2019");
		// expected.put("Price tick");	// N/A for archived contracts
		// expected.put("Value of price tick, RUB");	// N/A for archived contracts
		// expected.put("Lower limit");	// N/A for archived contracts
		// expected.put("Upper limit");	// N/A for archived contracts
		// expected.put("Settlement price of last clearing session");	// N/A for archived contracts
		// expected.put("Contract buy/sell fee, RUB");	// N/A for archived contracts
		// expected.put("Intraday (scalper) fee, RUB");	// N/A for archived contracts
		// expected.put("Negotiated trade fee, RUB");	// N/A for archived contracts
		expected.put("Contract exercise Clearing fee, RUB", "2.00");
		// expected.put("First level of Initial margin concentration limit*"); // N/A for archived contracts
		// expected.put("IM value on");	// N/A for archived contracts
		expected.put("FX for intraday clearing", "13:45 Moscow time");
		expected.put("FX for evening clearing", "18:30 Moscow time");
		expected.put("Settlement procedure", "Cash settlement. An average value of RTS Index calculated during the period from 15:00 to 16:00 Moscow time of the last trading day multiplied by 100 is taken as a settlement price. The tick value equals 20% of the USD/RUB exchange rate determined in accordance with the Methodology at 6:30 pm MSK on the last trading day.");
		MoexContractFormUtils mcfUtils = new MoexContractFormUtils();
		MoexContractPtmlConverter ptmlConverter = new MoexContractPtmlConverter();
		for ( Map.Entry<String, String> entry : expected.entrySet() ) {
			int field_id = mcfUtils.toContractField(entry.getKey());
			TEST_CONTRACT_DATA.put(field_id, ptmlConverter.toObject(field_id, entry.getValue()));
		}
	}
	
	private static void loadTestContractDetails_Full() throws Exception {
		Ini ini = null;
		try {
			ini = new Ini(CONTRACT_DATA_FILE);
		} catch ( FileNotFoundException e ) {
			throw new IllegalStateException("Contract data file not found: " + CONTRACT_DATA_FILE
					+ " " + ERMSG_REFER_TO_DATA_FILE, e);
		}
		if ( System.currentTimeMillis() - CONTRACT_DATA_FILE.lastModified() > CONTRACT_DATA_FILE_TTL ) {
			throw new IllegalStateException("Contract data file TTL expired. " + ERMSG_POSSIBLE_OUTDATED);
		}
		if ( ! ini.containsKey(CONTRACT_DATA_INI_SECTION) ) {
			throw new IllegalStateException("Contract data section "
					+ CONTRACT_DATA_INI_SECTION + " not found in " + CONTRACT_DATA_FILE
					+ " " + ERMSG_REFER_TO_DATA_FILE); 
		}
		Section sec = ini.get(CONTRACT_DATA_INI_SECTION);
		List<String> expectedKeys = new ArrayList<>();
		expectedKeys.add("Contract Symbol");
		expectedKeys.add("Contract Trading Symbol");
		expectedKeys.add("Contract Description");
		expectedKeys.add("Type");
		expectedKeys.add("Settlement");
		expectedKeys.add("Сontract size (lot)");
		expectedKeys.add("Quotation");
		expectedKeys.add("First Trading Day");
		expectedKeys.add("Last Trading Day");
		expectedKeys.add("Delivery");
		expectedKeys.add("Price tick");
		expectedKeys.add("Value of price tick, RUB");
		expectedKeys.add("Lower limit");
		expectedKeys.add("Upper limit");
		expectedKeys.add("Settlement price of last clearing session");
		expectedKeys.add("Contract buy/sell fee*, RUB");
		expectedKeys.add("Intraday (scalper) fee*, RUB");
		expectedKeys.add("Negotiated trade fee*, RUB");
		expectedKeys.add("Contract exercise Clearing fee, RUB");
		expectedKeys.add("First level of Initial margin concentration limit**");
		expectedKeys.add("IM value on");
		expectedKeys.add("FX for intraday clearing");
		expectedKeys.add("FX for evening clearing");
		expectedKeys.add("Settlement procedure");
		MoexContractFormUtils mcfUtils = new MoexContractFormUtils();
		MoexContractPtmlConverter ptmlConverter = new MoexContractPtmlConverter();
		for ( String key : expectedKeys ) {
			if ( ! sec.containsKey(key) ) {
				throw new IllegalStateException("Expected key not found: " + key + ERMSG_REFER_TO_DATA_FILE);
			}
			int fieldID = 0;
			try {
				fieldID = mcfUtils.toContractField(key);
			} catch ( WUWebPageException e ) {
				throw new IllegalStateException("Integration test failure. Cannot convert key to field ID: " + key, e);
			}
			Object value = null;
			try {
				value = ptmlConverter.toObject(fieldID, sec.get(key));
			} catch ( Exception e ) {
				throw new IllegalStateException("Integration test failure. Cannot obtain an internal representation of the key value: "
						+ key, e);
			}
			TEST_CONTRACT_DATA.put(fieldID, value);
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
	
	@Before
	public void setUp() throws Exception {
		service = factory.createInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		service.close();
	}
	
	@Test
	public void testStub() {
		
	}
	
	@Test
	public void testGetContractDetails() throws Exception {
		Map<Integer, Object> expected = TEST_CONTRACT_DATA;
		Symbol symbol = new Symbol((String) expected.get(MoexContractField.SYMBOL));
		Map<Integer, Object> actual = service.getContractDetails(symbol);
		assertEquals(ERMSG_CONTRACT_DATA_MISMATCH, expected, actual);
		//System.out.println(actual);
	}

	@Test
	public void testGetInstrumentDescription_AvailableContractsConversion() throws Exception {
		List<String> futures = service.getActiveFuturesList();
		logger.debug("Go through {} futures. It may hold up to several minutes.", futures.size());
		int passed = 0, errors = 0;
		for ( String contractCode : futures ) {
			try {
				service.getContractDetails(new Symbol(contractCode));
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
	public void testGetInstrumentDescription_PerformanceTest() throws Exception {
		List<String> futures = new ArrayList<>(service.getActiveFuturesList().subList(0, 20));
		long started = System.currentTimeMillis();
		int numberPassed = 0;
		logger.debug("Symbol list obtained. Start fetching contracts.");
		for ( String contractCode : futures ) {
			try {
				service.getContractDetails(new Symbol(contractCode));
				numberPassed ++;
			} catch ( Exception e ) {
				
			}
			Thread.sleep(2000L);
		}
		long used = System.currentTimeMillis() - started;
		logger.debug("Number of passed contracts {}", numberPassed);
		logger.debug("Total time used: {}ms.", used);
		logger.debug("Average per contract: {}ms.", used / numberPassed);
	}
	
	@Test
	public void testGetInstrumentDescription_ThrowsIfContractNotFound() throws Exception {
		try {
			service.getContractDetails(new Symbol("ZU1201"));
			fail("Expected exception: " + WUWebPageException.class.getSimpleName());
		} catch ( WUWebPageException e ) {
			assertEquals("Contract not exists or page has changed its structure: ZU1201", e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveFuturesList() throws Exception {
		Set<String> finam_list = null;
		try ( Fidexp finam = finamFactory.createInstance() ) {
			finam_list = new HashSet<>(finam.getTrueFuturesQuotes(true).values());
		}
		
		Set<String> moex_list = new HashSet<>(service.getActiveFuturesList());
		
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
