package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.getter.*;
import ru.prolib.aquila.core.data.getter.GDate2E;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.quik.subsys.getter.*;

/**
 * 2013-02-16<br>
 * $Id$
 */
public class ElementAdaptersTest {
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private EditableTerminal terminal;
	private ElementAdapters adapters;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		terminal = control.createMock(EditableTerminal.class);
		adapters = new ElementAdapters(locator, "PFX: ");
		expect(locator.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, adapters.getServiceLocator());
		assertEquals("PFX: ", adapters.getMessagePrefix());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderDir() throws Exception {
		GOrderDir expected, actual;
		expected = new GOrderDir(terminal,
				new RowElement("zulu4", String.class),
				"BUY", "SELL", "PFX: ");
		control.replay();
		
		actual = (GOrderDir) adapters.createOrderDir("zulu4", "BUY", "SELL");
		
		assertEquals(expected, actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateLong1() throws Exception {
		G<Long> expected = new GDouble2Long(terminal,
				new RowElement("bar", Double.class),
				true, "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createLong("bar"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateLong2() throws Exception {
		G<Long> expected1 = new GDouble2Long(terminal,
				new RowElement("bar", Double.class),
				true, "PFX: ");
		G<Long> expected2 = new GDouble2Long(terminal,
				new RowElement("foo", Double.class),
				false, "PFX: ");
		control.replay();
		
		assertEquals(expected1, adapters.createLong("bar", true));
		assertEquals(expected2, adapters.createLong("foo", false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateInteger1() throws Exception {
		G<Integer> expected = new GDouble2Int(terminal,
				new RowElement("foo", Double.class),
				true, "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createInteger("foo"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateInteger2() throws Exception {
		G<Integer> expected1 = new GDouble2Int(terminal,
				new RowElement("bar", Double.class),
				false, "PFX: ");
		G<Integer> expected2 = new GDouble2Int(terminal,
				new RowElement("buz", Double.class),
				true, "PFX: ");
		control.replay();
		
		assertEquals(expected1, adapters.createInteger("bar", false));
		assertEquals(expected2, adapters.createInteger("buz", true));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecDescr6() throws Exception {
		Map<String, SecurityType> types = new HashMap<String, SecurityType>();
		types.put("Облиг.", SecurityType.BOND);
		G<SecurityDescriptor> expected = new GSecurityDescr(
			new RowElement("code", String.class),
			new RowElement("class", String.class),
			new QUIKGetCurrency(new RowElement("currency",String.class), "SUR"),
			new GStringMap<SecurityType>(terminal,
				new RowElement("type", String.class),
				types, SecurityType.STK, true, "PFX: "));
		control.replay();
		
		assertEquals(expected, adapters.createSecDescr("code", "class",
				"currency", "SUR", "type", types));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateDouble() throws Exception {
		G<Double> expected = new GNotNull<Double>(terminal,
				new RowElement("foo", Double.class),
				"PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createDouble("foo"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateElement() throws Exception {
		G<Double> expected = new RowElement("foo", Integer.class);
		control.replay();
		
		assertEquals(expected, adapters.createElement("foo", Integer.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecDescr1() throws Exception {
		G<SecurityDescriptor> expected =
			new QUIKGetSecurityDescriptor1(locator,
					new RowElement("kappa", String.class));
		control.replay();
		
		assertEquals(expected, adapters.createSecDescr("kappa"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecDescr2() throws Exception {
		G<SecurityDescriptor> expected =
			new QUIKGetSecurityDescriptor2(locator,
					new RowElement("code", String.class),
					new RowElement("class", String.class));
		control.replay();
		
		assertEquals(expected, adapters.createSecDescr("code", "class"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateDate4() throws Exception {
		G<Date> expected = new GDate2E(terminal, true,
				new RowElement("date", String.class),
				new RowElement("time", String.class),
				"yyyy-MM-dd", "HH:mm:ss", "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createDate("date", "time",
				"yyyy-MM-dd", "HH:mm:ss", true));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateDate4_DefaultFormats() throws Exception {
		G<Date> expected = new GDate2E(terminal, false,
				new RowElement("date", String.class),
				new RowElement("time", String.class),
				((SimpleDateFormat) DateFormat.getDateInstance()).toPattern(),
				((SimpleDateFormat) DateFormat.getTimeInstance()).toPattern(),
				"PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createDate("date", "time",
				null, null, false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateAccount2() throws Exception {
		G<Account> expected = new GAccount(
			new GNotNull<String>(terminal,
				new RowElement("code", String.class), "PFX: "),
			new GNotNull<String>(terminal,
				new RowElement("subCode", String.class), "PFX: "));
		control.replay();
		
		assertEquals(expected, adapters.createAccount("code", "subCode"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateAccountAndCheckExists2() throws Exception {
		G<Account> expected = new GAccountExists(terminal,
			new GAccount(
				new GNotNull<String>(terminal,
					new RowElement("code", String.class), "PFX: "),
				new GNotNull<String>(terminal,
					new RowElement("subCode", String.class), "PFX: ")),
			"PFX: ");
		control.replay();
		
		assertEquals(expected,
				adapters.createAccountAndCheckExists("code", "subCode"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateAccount3() throws Exception {
		G<Account> expected = new GAccount(
			new GNotNull<String>(terminal,
				new RowElement("code", String.class), "PFX: "),
			new GNotNull<String>(terminal,
				new RowElement("subCode", String.class), "PFX: "),
			new GNotNull<String>(terminal,
				new RowElement("subCode2", String.class), "PFX: "));
		control.replay();
		
		assertEquals(expected,
				adapters.createAccount("code", "subCode", "subCode2"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateAccountAndCheckExists3() throws Exception {
		G<Account> expected = new GAccountExists(terminal,
			new GAccount(
				new GNotNull<String>(terminal,
					new RowElement("code", String.class), "PFX: "),
				new GNotNull<String>(terminal,
					new RowElement("subCode", String.class), "PFX: "),
				new GNotNull<String>(terminal,
					new RowElement("subCode2", String.class), "PFX: ")),
			"PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createAccountAndCheckExists("code",
				"subCode", "subCode2"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderAccount() throws Exception {
		G<Account> expected = new QUIKGetOrderAccount(locator,
				new RowElement("subCode", String.class),
				new RowElement("subCode2", String.class));
		control.replay();
		
		assertEquals(expected,
				adapters.createOrderAccount("subCode", "subCode2"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateString() throws Exception {
		G<String> expected = new GNotNull<String>(terminal,
				new RowElement("foobar", String.class), "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createString("foobar"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStringMap() throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		G<Integer> expected = new GStringMap<Integer>(terminal,
				new RowElement("foo", String.class), map, 543, true, "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createStringMap("foo", map, true, 543));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStringMap2G() throws Exception {
		Map<String, G<Integer>> map = new HashMap<String, G<Integer>>();
		map.put("foo", control.createMock(G.class));
		map.put("bar", control.createMock(G.class));
		G<Integer> expected = new GStringMap2G<Integer>(
				new GNotNull<String>(terminal,
					new RowElement("field", String.class),
					"PFX: "),
				map, 890);
		control.replay();
		
		assertEquals(expected, adapters.createStringMap2G("field", map, 890));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderType() throws Exception {
		Map<String, OrderType> map = new HashMap<String, OrderType>();
		G<OrderType> expected = new GStringMap<OrderType>(terminal,
			new GNotNull<String>(terminal,
				new QUIKGetOrderTypeCode(new RowElement("zulu", String.class)),
				"PFX: "),
			map, OrderType.OTHER, true, "PFX: ");
		control.replay();
		
		assertEquals(expected, adapters.createOrderType("zulu", map));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePrice() throws Exception {
		Map<String, PriceUnit> map = new HashMap<String, PriceUnit>();
		map.put("%", PriceUnit.PERCENT);
		G<Price> expected = new GPrice(
				new RowElement("price", Double.class),
				new GStringMap<PriceUnit>(terminal,
						new RowElement("unit", String.class),
						map, null, false, "PFX: "));
		control.replay();
		
		assertEquals(expected, adapters.createPrice("price", "unit", map));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(adapters.equals(adapters));
		assertFalse(adapters.equals(null));
		assertFalse(adapters.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<String> vPfx = new Variant<String>(vLoc)
			.add("PFX: ")
			.add("foobar");
		control.replay();
		Variant<?> iterator = vPfx;
		int foundCnt = 0;
		ElementAdapters x = null, found = null;
		do {
			x = new ElementAdapters(vLoc.get(), vPfx.get());
			if ( adapters.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertEquals("PFX: ", found.getMessagePrefix());
	}

}
