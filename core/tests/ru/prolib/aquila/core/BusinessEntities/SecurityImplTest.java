package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-05-30<br>
 * $Id: SecurityImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class SecurityImplTest {
	private SecurityDescriptor descr;
	private EventSystem eventSystem;
	private EventQueueImpl eventQueue;
	private IMocksControl control;
	private Terminal terminal;
	private SecurityImpl security;
	private EventType etChanged;
	private EventType etNewTrade;
	private EventDispatcher dispatcher;
	private G<?> getter;
	private S<SecurityImpl> setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		eventSystem = new EventSystemImpl();
		eventQueue = (EventQueueImpl) eventSystem.getEventQueue();
		dispatcher = eventSystem.createEventDispatcher();
		etChanged = eventSystem.createGenericType(dispatcher);
		etNewTrade = eventSystem.createGenericType(dispatcher);
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK); 
		security = new SecurityImpl(terminal, descr, dispatcher,
									etChanged, etNewTrade);
		setter = null;
		getter = null;
	}
	
	@After
	public void tearDown() throws Exception {
		eventQueue.stop();
	}
	
	/**
	 * Протестировать геттер/сеттер атрибута с проверкой признака изменения.
	 * <p>
	 * @param firstValue первое значение атрибута
	 * @param secondValue второе значение атрибута
	 */
	private void testGetterSetter(Object firstValue, Object secondValue) {
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
	public void testConstruct() throws Exception {
		Variant<Terminal> vTerm = new Variant<Terminal>()
			.add(terminal)
			.add(null);
		Variant<SecurityDescriptor> vDesc =
				new Variant<SecurityDescriptor>(vTerm)
			.add(descr)
			.add(new SecurityDescriptor("FOO", "BAR", null, null));
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vDesc)
			.add(dispatcher)
			.add(null);
		Variant<EventType> vEtChanged = new Variant<EventType>(vDisp)
			.add(etChanged)
			.add(null);
		Variant<EventType> vEtNewTrd = new Variant<EventType>(vEtChanged)
			.add(etNewTrade)
			.add(null);
		Variant<?> iterator = vEtNewTrd;
		int exceptionCnt = 0;
		SecurityImpl found = null;
		do {
			try {
				found = new SecurityImpl(vTerm.get(), vDesc.get(),
						vDisp.get(), vEtChanged.get(), vEtNewTrd.get());
			} catch ( Exception e ) {
				exceptionCnt ++;				
			}
		} while ( iterator.next() );
		assertEquals(exceptionCnt, iterator.count() - 1);
		assertSame(terminal, found.getTerminal());
		assertSame(descr, found.getDescriptor());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(etChanged, found.OnChanged());
		assertSame(etNewTrade, found.OnTrade());
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
		final CountDownLatch finished = new CountDownLatch(1);
		final SecurityTradeEvent expected =
			new SecurityTradeEvent(security.OnTrade(), security, trade);
		eventQueue.start();
		security.OnTrade().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		assertNull(security.getLastTrade());
		security.fireTradeEvent(trade);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		assertSame(trade, security.getLastTrade());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final SecurityEvent expected =
			new SecurityEvent(security.OnChanged(), security);
		eventQueue.start();
		security.OnChanged().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		security.fireChangedEvent();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
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
			public Double get(Object source) {
				return ((Security) source).getOpenPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) {
				object.setOpenPrice((Double) value);
			}
		};
		testGetterSetter(123.45d, 678.901d);
	}
	
	@Test
	public void testSetClosePrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) {
				return ((Security) source).getClosePrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) {
				object.setClosePrice((Double) value);
			}
		};
		testGetterSetter(87.56d, 72.11d);
	}
	
	@Test
	public void testSetLowPrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) {
				return ((Security) source).getLowPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) {
				object.setLowPrice((Double) value);
			}
		};
		testGetterSetter(91.23d, 17.24d);
	}

	@Test
	public void testSetHighPrice() throws Exception {
		getter = new G<Double>() {
			@Override
			public Double get(Object source) {
				return ((Security) source).getHighPrice();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) {
				object.setHighPrice((Double) value);
			}
		};
		testGetterSetter(191.23d, 217.24d);
	}

	@Test
	public void testSetStatus() throws Exception {
		getter = new G<SecurityStatus>() {
			@Override
			public SecurityStatus get(Object source) {
				return ((Security) source).getStatus();
			}
		};
		setter = new S<SecurityImpl>() {
			@Override
			public void set(SecurityImpl object, Object value) {
				object.setStatus((SecurityStatus) value);
			}
		};
		testGetterSetter(SecurityStatus.TRADING, SecurityStatus.STOPPED);
	}

}
