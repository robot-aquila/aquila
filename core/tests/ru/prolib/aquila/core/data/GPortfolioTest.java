package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-27<br>
 * $Id: GPortfolioTest.java 332 2012-12-09 12:06:25Z whirlwind $
 */
public class GPortfolioTest {
	private static IMocksControl control;
	private static G<Account> gAccount;
	private static Portfolios portfolios;
	private static Portfolio portfolio;
	private static GPortfolio getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gAccount = control.createMock(G.class);
		portfolios = control.createMock(Portfolios.class);
		portfolio = control.createMock(Portfolio.class);
		getter = new GPortfolio(gAccount, portfolios);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gAccount, getter.getAccountGetter());
		assertSame(portfolios, getter.getPortfolios());
	}
	
	@Test
	public void testGet_IfNoCode() throws Exception {
		expect(gAccount.get(this)).andReturn(null);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_IfNoPortfolio() throws Exception {
		expect(gAccount.get(this)).andReturn(new Account("LX001"));
		expect(portfolios.isPortfolioAvailable(new Account("LX001")))
			.andReturn(false);
		control.replay();
		assertNull(getter.get(this));
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		expect(gAccount.get(this)).andReturn(new Account("LX001"));
		expect(portfolios.isPortfolioAvailable(new Account("LX001")))
			.andReturn(true);
		expect(portfolios.getPortfolio(new Account("LX001")))
			.andReturn(portfolio);
		control.replay();
		assertSame(portfolio, getter.get(this));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<Account>> vAcc = new Variant<G<Account>>()
			.add(gAccount)
			.add(null)
			.add(control.createMock(G.class));
		Variant<Portfolios> vPorts = new Variant<Portfolios>(vAcc)
			.add(portfolios)
			.add(null)
			.add(control.createMock(Portfolios.class));
		int foundCnt = 0;
		GPortfolio found = null;
		do {
			GPortfolio actual = new GPortfolio(vAcc.get(), vPorts.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vPorts.next() );
		assertEquals(1, foundCnt);
		assertSame(gAccount, found.getAccountGetter());
		assertSame(portfolios, found.getPortfolios());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(this));
		assertFalse(getter.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/60015)
			.append(gAccount)
			.append(portfolios)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
