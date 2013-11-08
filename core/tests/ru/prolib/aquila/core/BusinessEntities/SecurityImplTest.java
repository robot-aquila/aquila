package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityEventDispatcher;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-05-30<br>
 * $Id: SecurityImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class SecurityImplTest {
	private static SecurityDescriptor descr1, descr2;
	private IMocksControl control;
	private Terminal terminal;
	private SecurityEventDispatcher dispatcher;
	private SecurityImpl security;
	private G<?> getter;
	private S<SecurityImpl> setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("GAZP", "EQBR", "RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM3", "SPFT", "USD",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		dispatcher = control.createMock(SecurityEventDispatcher.class);
		 
		security = new SecurityImpl(terminal, descr1, dispatcher);
		setter = null;
		getter = null;
	}
	
	/**
	 * Протестировать геттер/сеттер атрибута с проверкой признака изменения.
	 * <p>
	 * @param firstValue первое значение атрибута
	 * @param secondValue второе значение атрибута
	 */
	private void testGetterSetter(Object firstValue, Object secondValue)
			throws Exception
	{
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null, 	  null,			false },
				{ firstValue, firstValue,	false },
				{ null,		  secondValue,	true  },
				{ firstValue, secondValue,  true  },
				{ firstValue, null,			true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			setter.set(security, fixture[i][0]);
			security.resetChanges();
			setter.set(security, fixture[i][1]);
			assertEquals(msg, (Boolean)fixture[i][2], security.hasChanged());
			assertEquals(msg, fixture[i][1], getter.get(security));
		}
	}
	
	@Test
	public void testDefaultValues() throws Exception {
		assertEquals(0, security.getPrecision());
		assertNull(security.getMinPrice());
		assertNull(security.getMaxPrice());
		assertEquals(0, security.getLotSize());
		assertNull(security.getMinStepPrice());
		assertEquals(0.0d, security.getMinStepSize(), 0.01d);
		assertNull(security.getLastTrade());
		assertNull(security.getLastPrice());
		assertFalse(security.isAvailable());
		assertNull(security.getDisplayName());
		assertNull(security.getAskPrice());
		assertNull(security.getAskSize());
		assertNull(security.getBidPrice());
		assertNull(security.getBidSize());
		assertNull(security.getOpenPrice());
		assertNull(security.getClosePrice());
		assertNull(security.getHighPrice());
		assertNull(security.getLowPrice());
	}
	
	@Test
	public void testAccessorsAndMutators() throws Exception {
		security.setPrecision(3);
		security.setMinPrice(90.00d);
		security.setMaxPrice(130.00d);
		security.setLotSize(1);
		security.setMinStepPrice(0.1d);
		security.setMinStepSize(1.00d);
		security.setLastPrice(20.44d);
		security.setDisplayName("zulu4");
		security.setAskPrice(12.34d);
		security.setAskSize(1000l);
		security.setBidPrice(34.56d);
		security.setBidSize(2000l);
		security.setOpenPrice(13.45d);
		security.setClosePrice(98.15d);
		security.setLowPrice(24.56d);
		security.setHighPrice(18.44d);
		security.setStatus(SecurityStatus.TRADING);
		
		assertEquals(3, security.getPrecision());
		assertEquals(130.00d, security.getMaxPrice(), 0.001d);
		assertEquals(90.00d, security.getMinPrice(), 0.001d);
		assertEquals(1, security.getLotSize());
		assertEquals(0.1d, security.getMinStepPrice(), 0.001d);
		assertEquals(1.00d, security.getMinStepSize(), 0.001d);
		assertEquals(20.44d, security.getLastPrice(), 0.001d);
		assertEquals("zulu4", security.getDisplayName());
		assertEquals(12.34d, security.getAskPrice(), 0.01d);
		assertEquals(1000l, (long) security.getAskSize());
		assertEquals(34.56d, security.getBidPrice(), 0.01d);
		assertEquals(2000l, (long) security.getBidSize());
		assertEquals(13.45d, security.getOpenPrice(), 0.01d);
		assertEquals(98.15d, security.getClosePrice(), 0.01d);
		assertEquals(24.56d, security.getLowPrice(), 0.01d);
		assertEquals(18.44d, security.getHighPrice(), 0.01d);
		assertSame(SecurityStatus.TRADING, security.getStatus());
		
		security.setAvailable(true);
		assertTrue(security.isAvailable());
		security.setAvailable(false);
		assertFalse(security.isAvailable());
		security.setAvailable(true);
		assertTrue(security.isAvailable());
	}
	
	@Test
	public void testShrinkPrice_DecimalsGreaterThanZero() throws Exception {
		security.setMinStepSize(0.05d);
		security.setPrecision(2);
		assertEquals("123.00", security.shrinkPrice(123.00153123123d));
		assertEquals("222.15", security.shrinkPrice(222.1345d));
		security.setPrecision(4);
		assertEquals("222.1500", security.shrinkPrice(222.1345d));
		security.setMinStepSize(0.0005d);
		assertEquals("0.1000", security.shrinkPrice(0.10001d));
		assertEquals("222.1345", security.shrinkPrice(222.1345d));
	}
	
	@Test
	public void testShrinkPrice_DecimalsEqZero() throws Exception {
		security.setMinStepSize(5.0d);
		security.setPrecision(0);
		assertEquals("14555", security.shrinkPrice(14553.15));
		assertEquals("14550", security.shrinkPrice(14551.95));
		security.setMinStepSize(10.0d);
		assertEquals("14550", security.shrinkPrice(14553.15));
		assertEquals("14560", security.shrinkPrice(14555.55));
	}

	@Test
	public void testFireTradeEvent() throws Exception {
		Trade trade = new Trade(terminal);
		dispatcher.fireTrade(same(security), same(trade));
		control.replay();
		
		assertNull(security.getLastTrade());
		security.fireTradeEvent(trade);
		assertSame(trade, security.getLastTrade());
		
		control.verify();
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		dispatcher.fireChanged(same(security));
		control.replay();
		
		security.fireChangedEvent();
		
		control.verify();
	}
	
	@Test
	public void testSetPrecision_SetsChanged() throws Exception {
		assertFalse(security.hasChanged());
		security.setPrecision(5);
		assertTrue(security.hasChanged());
		security.resetChanges();
		assertFalse(security.hasChanged());
		security.setPrecision(5);
		assertFalse(security.hasChanged());
	}
	
	@Test
	public void testSetLotSize_SetsChanged() throws Exception {
		assertFalse(security.hasChanged());
		security.setLotSize(10);
		assertTrue(security.hasChanged());
		security.resetChanges();
		assertFalse(security.hasChanged());
		security.setLotSize(10);
		assertFalse(security.hasChanged());
	}
	
	@Test
	public void testSetMaxPrice_SetsChanged() throws Exception {
		Object fixture[][] = {
			// initial value, new value, changed?
			{ null,			null,		false },
			{ null,			115.1234d,	true  },
			{ 115.1234d,	115.1234d,	false },
			{ 115.1234d,	null,		true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setMaxPrice((Double) fixture[i][0]);
			security.resetChanges();
			security.setMaxPrice((Double) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals((Double) fixture[i][1], security.getMaxPrice());
		}
	}
	
	@Test
	public void testSetMinPrice_SetsChanged() throws Exception {
		Object fixture[][] = {
			// initial value, new value, changed?
			{ null, 	null,		false },
			{ null,		95.1234d,	true  },
			{ 95.1234d, 95.1234d,	false },
			{ 95.1234d, null,		true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setMinPrice((Double) fixture[i][0]);
			security.resetChanges();
			security.setMinPrice((Double) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals((Double) fixture[i][1], security.getMinPrice());
		}
	}
	
	@Test
	public void testSetLastPrice_SetsChanged() throws Exception {
		Object fix[][] = {
				// initial value, new value, changed?
				{ null, 	null,		false },
				{ null,		5.1234d,	true  },
				{ 5.1234d,  5.1234d,	false },
				{ 5.1234d,  null,		true  },
			};
			for ( int i = 0; i < fix.length; i ++ ) {
				String msg = "At #" + i;
				security.setLastPrice((Double) fix[i][0]);
				security.resetChanges();
				security.setLastPrice((Double) fix[i][1]);
				assertEquals(msg, (Boolean) fix[i][2], security.hasChanged());
				assertEquals((Double) fix[i][1], security.getLastPrice());
			}
	}
	
	@Test
	public void testSetMinStepPrice_SetsChanged() throws Exception {
		Object fixture[][] = {
			// initial value, new value, changed?
			{ null,		null,		false },
			{ null,		12.345d,	true  },
			{ 12.345d,	null,		true  },
			{ 12.345d,	12.345d,	false },
			{ 1.2345d,	1.2223d,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setMinStepPrice((Double) fixture[i][0]);
			security.resetChanges();
			security.setMinStepPrice((Double) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals((Double) fixture[i][1], security.getMinStepPrice());
		}
	}
	
	@Test
	public void testSetMinStepSize_SetsChanged() throws Exception {
		assertFalse(security.hasChanged());
		security.setMinStepSize(0.0005d);
		assertTrue(security.hasChanged());
		assertEquals(0.0005d, security.getMinStepSize(), 0.00001d);
		security.resetChanges();
		assertFalse(security.hasChanged());
		security.setMinStepSize(0.0005d);
		assertFalse(security.hasChanged());
	}
	
	@Test
	public void testSetDisplayName_SetsChanged() throws Exception {
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,	false },
				{ null,		"foo",	true  },
				{ "foo",	null,	true  },
				{ "foo",	"foo",	false },
				{ "foo",	"bar",	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setDisplayName((String) fixture[i][0]);
			security.resetChanges();
			security.setDisplayName((String) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals(msg, (String)fixture[i][1], security.getDisplayName());
		}
	}
	
	@Test
	public void testSetAskPrice_SetsChanged() throws Exception {
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,		false },
				{ null,		812.345d,	true  },
				{ 812.345d,	null,		true  },
				{ 812.345d,	812.345d,	false },
				{ 81.2345d,	81.2223d,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setAskPrice((Double) fixture[i][0]);
			security.resetChanges();
			security.setAskPrice((Double) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals(msg, (Double) fixture[i][1], security.getAskPrice());
		}
	}
	
	@Test
	public void testSetAskSize_SetsChanged() throws Exception {
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,		false },
				{ null,		812345l,	true  },
				{ 812345l,	null,		true  },
				{ 812345l,	812345l,	false },
				{ 812345l,	812223l,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setAskSize((Long) fixture[i][0]);
			security.resetChanges();
			security.setAskSize((Long) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals(msg, (Long) fixture[i][1], security.getAskSize());
		}
	}
	
	@Test
	public void testSetBidPrice_SetsChanged() throws Exception {
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,		false },
				{ null,		212.345d,	true  },
				{ 212.345d,	null,		true  },
				{ 212.345d,	212.345d,	false },
				{ 21.2345d,	21.2223d,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setBidPrice((Double) fixture[i][0]);
			security.resetChanges();
			security.setBidPrice((Double) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals(msg, (Double) fixture[i][1], security.getBidPrice());
		}

	}
	
	@Test
	public void testSetBidSize_SetsChanged() throws Exception {
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,	false },
				{ null,		2345l,	true  },
				{ 2345l,	null,	true  },
				{ 2345l,	2345l,	false },
				{ 2345l,	2223l,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			security.setBidSize((Long) fixture[i][0]);
			security.resetChanges();
			security.setBidSize((Long) fixture[i][1]);
			assertEquals(msg, (Boolean) fixture[i][2], security.hasChanged());
			assertEquals(msg, (Long) fixture[i][1], security.getBidSize());
		}
	}
	
	@Test
	public void testSetOpenPrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Security) source).getOpenPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) throws ValueException {
				object.setOpenPrice((Double) value);
			}
		};
		testGetterSetter(123.45d, 678.901d);
	}
	
	@Test
	public void testSetClosePrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Security) source).getClosePrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) throws ValueException {
				object.setClosePrice((Double) value);
			}
		};
		testGetterSetter(87.56d, 72.11d);
	}
	
	@Test
	public void testSetLowPrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Security) source).getLowPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) throws ValueException {
				object.setLowPrice((Double) value);
			}
		};
		testGetterSetter(91.23d, 17.24d);
	}

	@Test
	public void testSetHighPrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) throws ValueException {
				return ((Security) source).getHighPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) throws ValueException {
				object.setHighPrice((Double) value);
			}
		};
		testGetterSetter(191.23d, 217.24d);
	}

	@Test
	public void testSetStatus() throws Exception {
		getter = new G<SecurityStatus>() {
			@Override
			public SecurityStatus get(Object source) throws ValueException {
				return ((Security) source).getStatus();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) throws ValueException {
				object.setStatus((SecurityStatus) value);
			}
		};
		testGetterSetter(SecurityStatus.TRADING, SecurityStatus.STOPPED);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(security.equals(security));
		assertFalse(security.equals(null));
		assertFalse(security.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Trade trade = new Trade(terminal);
		security.setAskPrice(200.00d);
		security.setAskSize(80L);
		security.setAvailable(true);
		security.setBidPrice(180.00d);
		security.setBidSize(100L);
		security.setClosePrice(800.00d);
		security.setDisplayName("Yuppy");
		security.setHighPrice(650.00d);
		security.setLastPrice(124.00d);
		security.setLotSize(10);
		security.setLowPrice(754.00d);
		security.setMaxPrice(100.05d);
		security.setMinPrice(512.00d);
		security.setMinStepPrice(1.0d);
		security.setMinStepSize(0.01d);
		security.setOpenPrice(852.00d);
		security.setPrecision(2);
		security.setStatus(SecurityStatus.TRADING);
		security.fireTradeEvent(trade);

		Variant<Terminal> vTerm = new Variant<Terminal>()
			.add(terminal)
			.add(control.createMock(Terminal.class));
		Variant<SecurityDescriptor> vDescr =
				new Variant<SecurityDescriptor>(vTerm)
			.add(descr1)
			.add(descr2);
		Variant<Double> vAsk = new Variant<Double>(vDescr)
			.add(200.00d)
			.add(115.00d);
		Variant<Long> vAskSz = new Variant<Long>(vAsk)
			.add(80L)
			.add(12L);
		Variant<Boolean> vAvl = new Variant<Boolean>(vAskSz)
			.add(true)
			.add(false);
		Variant<Double> vBid = new Variant<Double>(vAvl)
			.add(180.00d)
			.add(815.00d);
		Variant<Long> vBidSz = new Variant<Long>(vBid)
			.add(100L)
			.add(256L);
		Variant<Double> vClose = new Variant<Double>(vBidSz)
			.add(800.00d)
			.add(1800.00d);
		Variant<String> vDispNm = new Variant<String>(vClose)
			.add("Yuppy")
			.add("Juppy");
		Variant<Double> vHigh = new Variant<Double>(vDispNm)
			.add(650.00d)
			.add(734.00d);
		Variant<Double> vLast = new Variant<Double>(vHigh)
			.add(124.00d)
			.add(321.00d);
		Variant<Integer> vLot = new Variant<Integer>(vLast)
			.add(10)
			.add(100);
		Variant<Double> vLow = new Variant<Double>(vLot)
			.add(754.00d)
			.add(828.00d);
		Variant<Double> vMax = new Variant<Double>(vLow)
			.add(100.05d)
			.add(215.00d);
		Variant<Double> vMin = new Variant<Double>(vMax)
			.add(512.00d)
			.add(1024.00d);
		Variant<Double> vStpPr = new Variant<Double>(vMin)
			.add(1.0d)
			.add(2.0d);
		Variant<Double> vStpSz = new Variant<Double>(vStpPr)
			.add(0.01d)
			.add(0.02d);
		Variant<Double> vOpen = new Variant<Double>(vStpSz)
			.add(852.00d)
			.add(634.00d);
		Variant<Integer> vPrec = new Variant<Integer>(vOpen)
			.add(2)
			.add(5);
		Variant<SecurityStatus> vStat = new Variant<SecurityStatus>(vPrec)
			.add(SecurityStatus.TRADING)
			.add(SecurityStatus.STOPPED);
		Variant<Trade> vLastTrd = new Variant<Trade>(vStat)
			.add(trade)
			.add(null);
		Variant<?> iterator = vLastTrd;
		int foundCnt = 0;
		SecurityImpl x = null, found = null;
		do {
			x = new SecurityImpl(vTerm.get(), vDescr.get(), dispatcher);
			x.setAskPrice(vAsk.get());
			x.setAskSize(vAskSz.get());
			x.setAvailable(vAvl.get());
			x.setBidPrice(vBid.get());
			x.setBidSize(vBidSz.get());
			x.setClosePrice(vClose.get());
			x.setDisplayName(vDispNm.get());
			x.setHighPrice(vHigh.get());
			x.setLastPrice(vLast.get());
			x.setLotSize(vLot.get());
			x.setLowPrice(vLow.get());
			x.setMaxPrice(vMax.get());
			x.setMinPrice(vMin.get());
			x.setMinStepPrice(vStpPr.get());
			x.setMinStepSize(vStpSz.get());
			x.setOpenPrice(vOpen.get());
			x.setPrecision(vPrec.get());
			x.setStatus(vStat.get());
			if ( vLastTrd.get() != null ) {
				x.fireTradeEvent(vLastTrd.get());
			}
			if ( security.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertEquals(descr1, found.getDescriptor());
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(200.00d, found.getAskPrice(), 0.01d);
		assertEquals(new Long(80L), found.getAskSize());
		assertTrue(found.isAvailable());
		assertEquals(180.00d, found.getBidPrice(), 0.01d);
		assertEquals(new Long(100L), found.getBidSize());
		assertEquals(800.00d, found.getClosePrice(), 0.01d);
		assertEquals("Yuppy", found.getDisplayName());
		assertEquals(650.00d, found.getHighPrice(), 0.01d);
		assertEquals(124.00d, found.getLastPrice(), 0.01d);
		assertEquals(10, found.getLotSize());
		assertEquals(754.00d, found.getLowPrice(), 0.01d);
		assertEquals(100.05d, found.getMaxPrice(), 0.01d);
		assertEquals(512.00d, found.getMinPrice(), 0.01d);
		assertEquals(1.0d, found.getMinStepPrice(), 0.01d);
		assertEquals(0.01d, found.getMinStepSize(), 0.01d);
		assertEquals(852.00d, found.getOpenPrice(), 0.01d);
		assertEquals(2, found.getPrecision());
		assertEquals(SecurityStatus.TRADING, found.getStatus());
		assertSame(trade, found.getLastTrade());
	}
	
	@Test
	public void testGetMostAccuratePrice() throws Exception {
		Double fix[][] = {
			//last, bid,  ask,  open, close, high, low, max, min, expected
			{ 10.1, 11.2, 12.4,  9.8,  8.9, 12.5,  9.6, 13.0,  9.0, 10.1  },
			{ null, 11.2, 12.4,  9.8,  8.9, 12.5,  9.6, 13.0,  9.0, 11.8  },
			{ null, null, 12.4,  9.8,  8.9, 12.5,  9.6, 13.0,  9.0,  9.8  },
			{ null, 11.2, null,  9.8,  8.9, 12.5,  9.6, 13.0,  9.0,  9.8  },
			{ null, null, null,  9.8,  8.9, 12.5,  9.6, 13.0,  9.0,  9.8  },
			{ null, null, null, null,  8.9, 12.5,  9.6, 13.0,  9.0,  8.9  },
			{ null, null, null, null, null, 12.5,  9.6, 13.0,  9.0, 11.05 },
			{ null, null, null, null, null, null,  9.6, 13.0,  9.0, 11.0  },
			{ null, null, null, null, null, 12.5, null, 13.0,  9.0, 11.0  },
			{ null, null, null, null, null, null, null, 13.0,  9.0, 11.0  },
			{ null, null, null, null, null, null, null, null,  9.0, null  },
			{ null, null, null, null, null, null, null, 13.0, null, null  },
			{ null, null, null, null, null, null, null, null, null, null  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			security.setLastPrice(fix[i][0]);
			security.setBidPrice(fix[i][1]);
			security.setAskPrice(fix[i][2]);
			security.setOpenPrice(fix[i][3]);
			security.setClosePrice(fix[i][4]);
			security.setHighPrice(fix[i][5]);
			security.setLowPrice(fix[i][6]);
			security.setMaxPrice(fix[i][7]);
			security.setMinPrice(fix[i][8]);
			if ( fix[i][9] == null ) {
				assertNull(msg, security.getMostAccuratePrice());
			} else {
				assertEquals(msg, fix[i][9], security.getMostAccuratePrice(),
						0.01d);
			}
		}
	}

	@Test
	public void testGetMostAccurateVolume() throws Exception {
		// Для первой строки:
		// 6.38818 / 10 = X / 130430 -> X = 130430 * 6.38818 / 10
		//	-> X = price * tick price / tick 
		Double fix[][] = {
			// tick, tick price, price, qty, expected
			{ 10.0, 6.38818, 130430.0, 1.0, 83321.03174 },
			{ 10.0, 6.38818, 130430.0, 2.0, 166642.06348 },
			{ 10.0, null,    130430.0, 1.0, 130430.0 },
			{ 10.0, null,    130430.0, 2.0, 260860.0 },
			{  1.0, 1.0,     32044.0,  5.0, 160220.0 },
			{ 0.01, 3.19409, 100.3,    1.0, 32036.7227 },
			{ 0.01, null,    100.3,    5.0, 501.5 },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			security.setMinStepSize(fix[i][0]);
			security.setMinStepPrice(fix[i][1]);
			assertEquals(msg, fix[i][4], security
					.getMostAccurateVolume(fix[i][2], fix[i][3].longValue()),
					0.000001d);
		}
	}
	
	@Test
	public void testOnChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, security.OnChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnTrade()).andReturn(type);
		control.replay();
		
		assertSame(type, security.OnTrade());
		
		control.verify();
	}
	
	/**
	 * Запись фикстуры для тестирования сравнения цен.
	 */
	static class FR {
		final int precision;
		final Double price1, price2;
		final boolean expected;
		FR(int precision, Double price1, Double price2, boolean expected) {
			this.precision = precision;
			this.price1 = price1;
			this.price2 = price2;
			this.expected = expected;
		}
	}
	
	@Test
	public void testIsPricesEquals() throws Exception {
		FR fix[] = {
			new FR( 0, 142030.0000d, 142030.0000d, true),
			new FR( 0, 142030.0000d, 142030.0001d, true),
			new FR( 0, 142030.0000d, 142030.0010d, true),
			new FR( 0, 142030.0000d, 142030.0100d, true),
			new FR( 0, 142030.0000d, 142030.0999d, true),
			new FR( 0, 142030.0000d, 142030.1000d, false),
			new FR( 0, 142030.0000d, 142030.1001d, false),
			new FR( 0, null, 		 142030.0000d, false),
			new FR( 0, 142030.0000d, null,		   false),
			new FR( 0, null,		 null,		   false),
			new FR( 0, 142030.0000d, 142030.1534d, false),
			new FR( 0, 142030.0000d, 142030.5102d, false),
			new FR( 0, 142030.0000d, 142031.0000d, false),
			new FR( 4, 1.3554d, 	1.3554d,	   true),
			new FR( 4, 1.3554d,     1.355409d,	   true),
			new FR( 4, 1.3554d,     1.3555d,	   false),
			new FR( 4, -1.3554d,    1.3554d,	   false),
			new FR( 4, 1.3554d,    -1.3554d,	   false),
			
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			security.setPrecision(fr.precision);
			assertEquals(msg, fr.expected,
					security.isPricesEquals(fr.price1, fr.price2));
		}
	}

}
