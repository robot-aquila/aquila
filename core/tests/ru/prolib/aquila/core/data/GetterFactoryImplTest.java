package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.GAccount;
import ru.prolib.aquila.core.data.getter.GPrice;
import ru.prolib.aquila.core.data.getter.GSecurityDescr;
import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * 2012-10-19<br>
 * $Id: GetterFactoryImplTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GetterFactoryImplTest {
	private static IMocksControl control;
	private static GetterFactoryImpl factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		factory = new GetterFactoryImpl();
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testCondInteger2G() throws Exception {
		G<Integer> expected = new GCond<Integer>(new ValidatorEq("foobar"),
				new GConst<Integer>(1), new GConst<Integer>(2));
		assertEquals(expected, factory.condInteger(new ValidatorEq("foobar"),
				new GConst<Integer>(1), new GConst<Integer>(2)));
	}
	
	@Test
	public void testCondInteger2V() throws Exception {
		G<Integer> expected = new GCond<Integer>(new ValidatorEq(""),
				new GConst<Integer>(10), new GConst<Integer>(20));
		assertEquals(expected,factory.condInteger(new ValidatorEq(""), 10, 20));
	}
	
	@Test
	public void testConstObject() throws Exception {
		G<Object> expected = new GConst<Object>(this);
		assertEquals(expected, factory.constObject(this));
	}
	
	@Test
	public void testConstString() throws Exception {
		G<String> expected = new GConst<String>("charlie");
		assertEquals(expected, factory.constString("charlie"));
	}
	
	@Test
	public void testPortfolio() throws Exception {
		Portfolios portfolios = control.createMock(Portfolios.class);
		G<Portfolio> expected, actual;
		expected = new GPortfolio(new GConst<Account>(new Account("zulu")),
				portfolios);
		actual = factory.portfolio(portfolios,
				new GConst<Account>(new Account("zulu")));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRowAccount1() throws Exception {
		G<Account> expected = new GAccount(new GRowObj<String>("foo",
				new GString()));
		G<Account> actual = factory.rowAccount("foo");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRowAccount2() throws Exception {
		G<Account> expected = new GAccount(
				new GRowObj<String>("foo", new GString()),
				new GRowObj<String>("bar", new GString()));
		G<Account> actual = factory.rowAccount("foo", "bar");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRowCondInteger() throws Exception {
		@SuppressWarnings("rawtypes")
		G<Integer> expected = new GCond<Integer>(
				new GValidator(new GRowObj("value"), new ValidatorEq("--")),
				new GConst<Integer>(100), new GConst<Integer>(500));
		assertEquals(expected, factory.rowCondInteger("value", "--", 100, 500));
	}
	
	@Test
	public void testRowDate4() throws Exception {
		G<Date> expected = new GDate2E(
				new GRowObj<String>("date", new GString()),
				new GRowObj<String>("time", new GString()),
				"y", "H");
		assertEquals(expected, factory.rowDate("date", "time", "y", "H"));
	}
	
	@Test
	public void testRowDate1() throws Exception {
		G<Date> expected = new GRowObj<Date>("time", new GDate());
		assertEquals(expected, factory.rowDate("time"));
	}
	
	@Test
	public void testRowDouble() throws Exception {
		G<Double> expected = new GRowObj<Double>("foo", new GDouble());
		assertEquals(expected, factory.rowDouble("foo"));
	}

	@Test
	public void testRowInteger() throws Exception {
		G<Integer> expected = new GRowObj<Integer>("bar", new GInteger());
		assertEquals(expected, factory.rowInteger("bar"));
	}
	
	@Test
	public void testRowLong() throws Exception {
		G<Long> expected = new GRowObj<Long>("buzz", new GLong());
		assertEquals(expected, factory.rowLong("buzz"));
	}
	
	@Test
	public void testRowObject() throws Exception {
		G<Object> expected = new GRowObj<Object>("zulu24");
		assertEquals(expected, factory.rowObject("zulu24"));
	}
	
	@Test
	public void testRowOrderDir() throws Exception {
		@SuppressWarnings("rawtypes")
		G<Direction> expected = new GCond<Direction>(
				new GValidator(new GRowObj("dir"), new ValidatorEq("BUY")),
				new GConst<Direction>(Direction.BUY),
				new GConst<Direction>(Direction.SELL));
		assertEquals(expected, factory.rowOrderDir("dir", "BUY"));
	}
	
	@Test
	public void testRowPortfolio2() throws Exception {
		Portfolios portfolios = control.createMock(Portfolios.class);
		G<Portfolio> expected = new GPortfolio(new GAccount(
				new GRowObj<String>("code", new GString())),
				portfolios);
		assertEquals(expected, factory.rowPortfolio(portfolios, "code"));
	}
	
	@Test
	public void testRowPortfolio3() throws Exception {
		Portfolios portfolios = control.createMock(Portfolios.class);
		G<Portfolio> expected = new GPortfolio(new GAccount(
				new GRowObj<String>("code", new GString()),
				new GRowObj<String>("sub", new GString())),
				portfolios);
		assertEquals(expected, factory.rowPortfolio(portfolios, "code", "sub"));
	}
	
	@Test
	public void testRowPrice() throws Exception {
		Map<String, PriceUnit> map = new HashMap<String, PriceUnit>();
		map.put("Д", PriceUnit.MONEY);
		map.put("%", PriceUnit.PERCENT);
		G<Price> expected = new GPrice(
				new GRowObj<Double>("price", new GDouble()),
				new GMapTR<PriceUnit>(new GRowObj<Object>("unit"), map));
		assertEquals(expected, factory.rowPrice("price", "unit", map));
	}
	
	@Test
	public void testRowSecurity() throws Exception {
		Securities securities = control.createMock(Securities.class);
		G<Security> expected = new GSecurity(new GSecurityDescr(
					new GRowObj<String>("code", new GString()),
					new GRowObj<String>("class", new GString()),
					new GConst<String>("EUR"),
					new GConst<SecurityType>(SecurityType.STK)),
				securities);
		assertEquals(expected, factory.rowSecurity(securities, "code", "class",
				"EUR", SecurityType.STK));
	}
	
	@Test
	public void testRowSecurityDescriptor() throws Exception {
		G<SecurityDescriptor> expected = new GSecurityDescr(
				new GRowObj<String>("code", new GString()),
				new GRowObj<String>("class", new GString()),
				new GConst<String>("USD"),
				new GConst<SecurityType>(SecurityType.UNK));
		assertEquals(expected, factory.rowSecurityDescr("code", "class",
				"USD", SecurityType.UNK));
	}
	
	@Test
	public void testRowString() throws Exception {
		G<String> expected = new GRowObj<String>("url", new GString());
		assertEquals(expected, factory.rowString("url"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSecurity() throws Exception {
		G<SecurityDescriptor> gDescr = control.createMock(G.class);
		Securities securities = control.createMock(Securities.class);
		G<Security> expected = new GSecurity(gDescr, securities);
		assertEquals(expected, factory.security(securities, gDescr));
	}

}
