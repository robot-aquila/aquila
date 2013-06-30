package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;
import java.util.Date;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;

public class PortfolioValueEntryTest {
	private PortfolioValueEntry entry;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		entry = new PortfolioValueEntry("TEST", "Net", "USD", "215.48");
	}
	
	@Test
	public void testEntryTime() throws Exception {
		entry = new PortfolioValueEntry("TEST", "Net", "USD", "215.48");
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetAccountName() throws Exception {
		assertEquals("TEST", entry.getAccountName());
	}
	
	@Test
	public void testGetAccount() throws Exception {
		assertEquals(new Account("TEST"), entry.getAccount());
	}
	
	@Test
	public void testGetKey() throws Exception {
		assertEquals("Net", entry.getKey());
	}
	
	@Test
	public void testCurrency() throws Exception {
		assertEquals("USD", entry.getCurrency());
	}
	
	@Test
	public void testGetDouble() throws Exception {
		assertEquals(215.48d, entry.getDouble(), 0.01d);
	}
	
	@Test
	public void testGetDouble_NullIfNumberFormatException() throws Exception {
		entry = new PortfolioValueEntry("TEST", "Net", "EUR", "foo");
		assertNull(entry.getDouble());
	}
	
	@Test
	public void testGetString() throws Exception {
		assertEquals("215.48", entry.getString());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vAcc = new Variant<String>()
			.add("TEST")
			.add("BEST");
		Variant<String> vKey = new Variant<String>(vAcc)
			.add("Net")
			.add("Set");
		Variant<String> vCur = new Variant<String>(vKey)
			.add("USD")
			.add("EUR");
		Variant<String> vVal = new Variant<String>(vCur)
			.add("215.48")
			.add("null");
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		PortfolioValueEntry x, found = null;
		do {
			x = new PortfolioValueEntry(vAcc.get(), vKey.get(),
					vCur.get(), vVal.get());
			if ( entry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("TEST", found.getAccountName());
		assertEquals("Net", found.getKey());
		assertEquals("USD", found.getCurrency());
		assertEquals("215.48", found.getString());
	}

}
