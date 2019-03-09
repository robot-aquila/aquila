package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl.SecurityController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParamsBuilder;
import ru.prolib.aquila.core.data.DataProviderStub;

/**
 * 2012-05-30<br>
 * $Id: SecurityImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class SecurityImplTest extends ObservableStateContainerImplTest {
	private static Symbol symbol1 = new Symbol("S:GAZP@EQBR:RUB");
	private SchedulerStub schedulerStub;
	private SecurityController controller;
	private EditableTerminal terminal;
	private SecurityImpl security;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ObservableStateContainerImplTest.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
		controller = new SecurityController();
		super.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected String getID() {
		return security.getContainerID();
	}
	
	private void prepareTerminal() {
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withTerminalID("foobar")
				.withEventQueue(queue)
				.withScheduler(schedulerStub)
				.buildTerminal();
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		security = new SecurityImpl(new SecurityParamsBuilder(queue)
				.withSymbol(symbol1)
				.withTerminal(terminal)
				.buildParams());
		return security;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(OSCController controller) {
		prepareTerminal();
		security = new SecurityImpl(new SecurityParamsBuilder(queue)
				.withSymbol(symbol1)
				.withTerminal(terminal)
				.withController(controller)
				.buildParams());
		return security;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(EventDispatcher eventDispatcher,
			OSCController controller)
	{
		prepareTerminal();
		security = new SecurityImpl(new SecurityParamsBuilder()
				.withTerminal(terminal)
				.withSymbol(symbol1)
				.withEventDispatcher(eventDispatcher)
				.withController(controller)
				.buildParams());
		return security;
	}
	
	private L1Update toL1Update(Tick tick) {
		return new L1UpdateImpl(symbol1, tick);
	}
	
	@Test
	public void testCtor_DefaultController() throws Exception {
		produceContainer(controller);
		assertSame(controller, security.getController());
		assertNotNull(security.getTerminal());
		assertNotNull(security.getEventDispatcher());
		assertSame(terminal, security.getTerminal());
		assertSame(queue, ((EventDispatcherImpl)security.getEventDispatcher()).getEventQueue());
		assertEquals(symbol1, security.getSymbol());
		String prefix = String.format("%s.S:GAZP@EQBR:RUB.SECURITY", "foobar");
		assertEquals(prefix, security.getContainerID());
		assertEquals(prefix + ".SESSION_UPDATE", security.onSessionUpdate().getId());
		assertEquals(prefix + ".BEST_ASK", security.onBestAsk().getId());
		assertEquals(prefix + ".BEST_BID", security.onBestBid().getId());
		assertEquals(prefix + ".LAST_TRADE", security.onLastTrade().getId());
		assertEquals(prefix + ".MARKET_DEPTH_UPDATE", security.onMarketDepthUpdate().getId());
		assertFalse(security.isAvailable());
	}

	@Test
	public void testGetScale() throws Exception {
		assertNull(security.getScale());
		security.update(SecurityField.TICK_SIZE, CDecimalBD.of("0.005"));
		assertEquals(new Integer(3), security.getScale());
	}
	
	@Test
	public void testGetLotSize() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getLotSize();
			}	
		};
		testGetter(SecurityField.LOT_SIZE, CDecimalBD.of(10L), CDecimalBD.of(1L));
	}
	
	@Test
	public void testGetUpperPriceLimit() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getUpperPriceLimit();
			}
		};
		testGetter(SecurityField.UPPER_PRICE_LIMIT,
				CDecimalBD.of("137.15"), CDecimalBD.of("158.12"));
	}
	
	@Test
	public void testGetLowerPriceLimit() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getLowerPriceLimit();
			}
		};
		testGetter(SecurityField.LOWER_PRICE_LIMIT,
				CDecimalBD.of("119.02"), CDecimalBD.of("118.16"));
	}
	
	@Test
	public void testGetTickValue() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getTickValue();
			}
		};
		testGetter(SecurityField.TICK_VALUE,
				CDecimalBD.ofUSD2("440.09"), CDecimalBD.ofRUB2("482.15"));
	}
	
	@Test
	public void testGetTickSize() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getTickSize();
			}
		};
		testGetter(SecurityField.TICK_SIZE,
				CDecimalBD.of("10.0"), CDecimalBD.of("0.05"));
	}
	
	@Test
	public void testGetDisplayName() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return security.getDisplayName();
			}
		};
		testGetter(SecurityField.DISPLAY_NAME, "foo", "bar");
	}	
	
	@Test
	public void testGetOpenPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getOpenPrice();
			}
		};
		testGetter(SecurityField.OPEN_PRICE,
				CDecimalBD.of("321.19"), CDecimalBD.of("280.04"));
	}
	
	@Test
	public void testGetClosePrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getClosePrice();
			}
		};
		testGetter(SecurityField.CLOSE_PRICE,
				CDecimalBD.of("10.03"), CDecimalBD.of("12.34"));
	}
	
	@Test
	public void testGetLowPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getLowPrice();
			}
		};
		testGetter(SecurityField.LOW_PRICE,
				CDecimalBD.of("8.02"), CDecimalBD.of("15.87"));
	}

	@Test
	public void testGetHighPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getHighPrice();
			}
		};
		testGetter(SecurityField.HIGH_PRICE,
				CDecimalBD.of("4586.13"), CDecimalBD.of("7002.17"));
	}

	@Test
	public void testGetSettlementPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getSettlementPrice();
			}
		};
		testGetter(SecurityField.SETTLEMENT_PRICE,
				CDecimalBD.of("215.86"), CDecimalBD.of("114.12"));
	}

	@Test
	public void testGetInitialMargin() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return security.getInitialMargin();
			}
		};
		testGetter(SecurityField.INITIAL_MARGIN,
				CDecimalBD.ofUSD2("118.99"), CDecimalBD.ofRUB2("120.01"));
	}
	
	@Test
	public void testGetExpirationTime() throws Exception {
		getter = new Getter<Instant>() {
			@Override
			public Instant get() {
				return security.getExpirationTime();
			}
		};
		testGetter(SecurityField.EXPIRATION_TIME,
				Instant.parse("2018-03-25T00:00:00Z"), Instant.parse("2018-03-26T07:01:00Z"));
	}
	
	@Test
	public void testClose() {
		EventType type = new EventTypeImpl();
		security.onAvailable().addListener(listenerStub);
		security.onAvailable().addAlternateType(type);
		security.onSessionUpdate().addListener(listenerStub);
		security.onSessionUpdate().addAlternateType(type);
		security.onUpdate().addListener(listenerStub);
		security.onUpdate().addAlternateType(type);
		security.onBestAsk().addListener(listenerStub);
		security.onBestAsk().addAlternateType(type);
		security.onBestBid().addListener(listenerStub);
		security.onBestBid().addAlternateType(type);
		security.onLastTrade().addListener(listenerStub);
		security.onLastTrade().addAlternateType(type);
		security.onMarketDepthUpdate().addListener(listenerStub);
		security.onMarketDepthUpdate().addAlternateType(type);
		
		security.close();
		
		assertFalse(security.onAvailable().hasListeners());
		assertFalse(security.onAvailable().hasAlternates());
		assertFalse(security.onSessionUpdate().hasListeners());
		assertFalse(security.onSessionUpdate().hasAlternates());
		assertFalse(security.onUpdate().hasListeners());
		assertFalse(security.onUpdate().hasAlternates());
		assertFalse(security.onBestAsk().hasListeners());
		assertFalse(security.onBestAsk().hasAlternates());
		assertFalse(security.onBestBid().hasListeners());
		assertFalse(security.onBestBid().hasAlternates());
		assertFalse(security.onLastTrade().hasListeners());
		assertFalse(security.onLastTrade().hasAlternates());
		assertFalse(security.onMarketDepthUpdate().hasListeners());
		assertFalse(security.onMarketDepthUpdate().hasAlternates());
		assertNull(security.getTerminal());
		assertFalse(security.isAvailable());
		assertTrue(security.isClosed());
	}
	
	@Test
	public void testSecurityController_HasMinimalData() throws Exception {
		SecurityController controller = new SecurityController();
		
		assertFalse(controller.hasMinimalData(security, null));
		
		data.put(SecurityField.DISPLAY_NAME, "GAZP");
		data.put(SecurityField.LOT_SIZE, CDecimalBD.of(100L));
		data.put(SecurityField.TICK_SIZE, CDecimalBD.of("5.00"));
		data.put(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("2.37"));
		data.put(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("200.01"));
		security.update(data);
		
		assertTrue(controller.hasMinimalData(security, null));
	}
	
	@Test
	public void testSecurityController_ProcessUpdate_SessionUpdate() {
		Instant time = T("2017-08-04T19:51:00Z");
		data.put(SecurityField.LOT_SIZE, CDecimalBD.of(1L));
		data.put(SecurityField.TICK_SIZE, CDecimalBD.of("0.05"));
		data.put(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("0.01"));
		data.put(SecurityField.INITIAL_MARGIN, CDecimalBD.of("2034.17"));
		data.put(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("80.93"));
		data.put(SecurityField.OPEN_PRICE, CDecimalBD.of("79.19"));
		data.put(SecurityField.HIGH_PRICE, CDecimalBD.of("83.64"));
		data.put(SecurityField.LOW_PRICE, CDecimalBD.of("79.19"));
		data.put(SecurityField.CLOSE_PRICE, CDecimalBD.of("79.18"));
		security.update(data);
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addListener(listener);
		
		controller.processUpdate(security, time);
		
		assertEquals(1, listener.getEventCount());
		assertContainerEvent((SecurityEvent) listener.getEvent(0), security.onSessionUpdate(), security, time,
				SecurityField.LOT_SIZE,
				SecurityField.TICK_SIZE,
				SecurityField.TICK_VALUE,
				SecurityField.INITIAL_MARGIN,
				SecurityField.SETTLEMENT_PRICE,
				SecurityField.OPEN_PRICE,
				SecurityField.HIGH_PRICE,
				SecurityField.LOW_PRICE,
				SecurityField.CLOSE_PRICE);
	}

	@Test
	public void testSecurityController_ProcessAvailable() {
		Instant time = T("2017-08-04T19:55:00Z");
		data.put(SecurityField.LOT_SIZE, CDecimalBD.of(1L));
		data.put(SecurityField.TICK_SIZE, CDecimalBD.of("0.05"));
		data.put(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("0.01"));
		data.put(SecurityField.INITIAL_MARGIN, CDecimalBD.of("2034.17"));
		data.put(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of("80.93"));
		data.put(SecurityField.OPEN_PRICE, CDecimalBD.of("79.19"));
		data.put(SecurityField.HIGH_PRICE, CDecimalBD.of("83.64"));
		data.put(SecurityField.LOW_PRICE, CDecimalBD.of("79.19"));
		data.put(SecurityField.CLOSE_PRICE, CDecimalBD.of("79.18"));
		security.update(data);
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addListener(listener);
		
		controller.processAvailable(security, time);
		
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	public void testUpdate_Tick_BestAsk() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T21:05:00Z");
		security.onBestAsk().addListener(listenerStub);
		assertNull(security.getBestAsk());
		
		security.consume(toL1Update(Tick.ofAsk(T("1996-12-04T00:15:00Z"),
				CDecimalBD.of("80.34"),
				CDecimalBD.of(15L))));
		
		Tick expected = Tick.ofAsk(T("1996-12-04T00:15:00Z"),
				CDecimalBD.of("80.34"),
				CDecimalBD.of(15L));
		assertEquals(expected, security.getBestAsk());
		assertEquals(1, listenerStub.getEventCount());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onBestAsk(), security, T("2017-08-04T21:05:00Z"));
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getTick());
	}
	
	@Test
	public void testUpdate_Tick_ResetBestAsk() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T20:03:00Z");
		security.consume(toL1Update(Tick.ofAsk(T("2003-01-01T00:00:00Z"),
				CDecimalBD.of("92.13"),
				CDecimalBD.of(100L))));
		security.onBestAsk().addListener(listenerStub);
		
		security.consume(toL1Update(Tick.NULL_ASK));
		
		assertNull(security.getBestAsk());
		assertEquals(1, listenerStub.getEventCount());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onBestAsk(), security, T("2017-08-04T20:03:00Z"));
		assertSame(security, e.getSecurity());
		assertNull(e.getTick());
	}
	
	@Test
	public void testUpdate_Tick_BestBid() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T21:10:00Z");
		security.onBestBid().addListener(listenerStub);
		assertNull(security.getBestBid());
		
		security.consume(toL1Update(Tick.ofBid(T("2010-09-11T03:15:25Z"),
				CDecimalBD.of("12.48"),
				CDecimalBD.of(500L))));
		
		Tick expected = Tick.ofBid(T("2010-09-11T03:15:25Z"),
				CDecimalBD.of("12.48"),
				CDecimalBD.of(500L));
		assertEquals(expected, security.getBestBid());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onBestBid(), security, T("2017-08-04T21:10:00Z"));
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getTick());
	}
	
	@Test
	public void testUpdate_Tick_ResetBestBid() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T20:03:00Z");
		security.consume(toL1Update(Tick.ofBid(T("1992-07-24T15:45:00Z"),
				CDecimalBD.of("52.94"),
				CDecimalBD.of(1L))));
		security.onBestBid().addListener(listenerStub);
		
		security.consume(toL1Update(Tick.NULL_BID));
		
		assertNull(security.getBestBid());
		assertEquals(1, listenerStub.getEventCount());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onBestBid(), security, T("2017-08-04T20:03:00Z"));
		assertSame(security, e.getSecurity());
		assertNull(e.getTick());
	}
	
	@Test
	public void testUpdate_Tick_LastTrade() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T21:10:00Z");
		security.onLastTrade().addListener(listenerStub);
		assertNull(security.getLastTrade());
		
		security.consume(toL1Update(Tick.ofTrade(T("1978-02-01T05:12:15Z"),
				CDecimalBD.of("72.15"),
				CDecimalBD.of(805L))));
		
		Tick expected = Tick.ofTrade(T("1978-02-01T05:12:15Z"),
				CDecimalBD.of("72.15"),
				CDecimalBD.of(805L));
		assertEquals(expected, security.getLastTrade());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onLastTrade(), security, T("2017-08-04T21:10:00Z"));
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getTick());
	}
	
	@Test
	public void testGetLast_Price() {
		
		assertNull(security.getLastPrice());
		
		security.consume(toL1Update(
				Tick.ofTrade(T("1978-02-01T05:12:15Z"),
				CDecimalBD.of("272.15"),
				CDecimalBD.of(105L)))
			);
		
		assertEquals(of("272.15"), security.getLastPrice());
	}
	
	@Test
	public void testGetMarketDepth_DefaultValue() {
		MarketDepth md = security.getMarketDepth();
		
		assertNotNull(md);
		assertEquals(symbol1, md.getSymbol());
		assertEquals(0, md.getTimestamp());
		assertFalse(md.hasBestAsk());
		assertFalse(md.hasBestBid());
	}
	
	@Test
	public void testUpdate_MDUpdate_RefreshAsk() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-03-04T01:27:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_ASK, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		// REFRESH_ASK will reset ask-side but bid-side quotes will be kept
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("100.02"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("102.45"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("100.02"), CDecimalBD.of(800L))); // this quote must be kept
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)));
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_RefreshBid() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-03-04T01:27:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_BID, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("100.02"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("102.45"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("102.45"), CDecimalBD.of(100L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)));
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Refresh() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-02-04T17:24:15Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("100.02"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("102.45"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)));
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Update_Replace() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.buildUpdate());

		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.30"), CDecimalBD.of(120L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of( "99.98"), CDecimalBD.of(500L)), MDTransactionType.ADD);
		security.consume(update);
		
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(999L)), MDTransactionType.REPLACE);
		update.addRecord(Tick.ofBid(time2, CDecimalBD.of( "99.98"), CDecimalBD.of(199L)), MDTransactionType.REPLACE);
		security.onMarketDepthUpdate().addListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(999L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)));
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)));
		expectedBids.add(Tick.ofBid(time2, CDecimalBD.of( "99.98"), CDecimalBD.of(199L)));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}

	@Test
	public void testUpdate_MDUpdate_Update_Delete() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.0001"))
			.buildUpdate());

		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.30"), CDecimalBD.of(120L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofBid(time1, CDecimalBD.of( "99.98"), CDecimalBD.of(500L)), MDTransactionType.ADD);
		security.consume(update);
		
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(0L)), MDTransactionType.DELETE);
		update.addRecord(Tick.ofBid(time2, CDecimalBD.of( "99.98"), CDecimalBD.of(0L)), MDTransactionType.DELETE);
		security.onMarketDepthUpdate().addListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)));
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Update_PriceRounding() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.buildUpdate());

		Instant time1 = Instant.EPOCH;
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.30"), CDecimalBD.of(120L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)), MDTransactionType.ADD);
		update.addRecord(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(200L)), MDTransactionType.ADD);
		security.consume(update);

		Instant time2 = Instant.EPOCH.plusSeconds(1000);
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(500L)), MDTransactionType.REPLACE);
		update.addRecord(Tick.ofAsk(time2, CDecimalBD.of("102.40"), CDecimalBD.of(250L)), MDTransactionType.REPLACE);
		update.addRecord(Tick.ofAsk(time2, CDecimalBD.of("102.41"), CDecimalBD.of(220L)), MDTransactionType.REPLACE);
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(500L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.40"), CDecimalBD.of(250L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.41"), CDecimalBD.of(220L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(200L)));
		assertEquals(expectedAsks, security.getMarketDepth().getAsks());
	}
	
	@Test
	public void testSecurityController_GetCurrentTime_IfClosed() {
		schedulerStub.setFixedTime("2017-08-04T20:00:00Z");
		security.close();
		
		assertNull(controller.getCurrentTime(security));
	}
	
	@Test
	public void testSecurityController_GetCurrentTime_IfNotClosed() {
		schedulerStub.setFixedTime("2017-08-04T20:00:00Z");
		
		assertEquals(T("2017-08-04T20:00:00Z"), controller.getCurrentTime(security));
	}
	
	@Test
	public void testUpdate_OnAvailable() throws Exception {
		Instant time = T("2017-08-04T20:01:00Z");
		container = produceContainer(controllerMock);
		container.onAvailable().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(security)).andReturn(time);
		controllerMock.processUpdate(security, time);
		expect(controllerMock.hasMinimalData(security, time)).andReturn(true);
		controllerMock.processAvailable(security, time);
		getMocksControl().replay();

		data.put(12345, 415); // any value
		security.update(data);
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT((SecurityEvent) listenerStub.getEvent(0),
				security.onAvailable(), security, time);
	}
	
	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		Instant time1 = T("2017-08-04T21:15:00Z"), time2 = T("2017-08-04T21:20:00Z");
		container = produceContainer(controllerMock);
		container.onUpdate().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(security)).andReturn(time1);
		controllerMock.processUpdate(security, time1);
		expect(controllerMock.hasMinimalData(security, time1)).andReturn(true);
		controllerMock.processAvailable(security, time1);
		expect(controllerMock.getCurrentTime(security)).andReturn(time2);
		controllerMock.processUpdate(security, time2);
		getMocksControl().replay();
		
		security.update(12345, 415);
		security.update(12345, 450);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertContainerEvent(listenerStub.getEvent(0), security.onUpdate(), security, time1, 12345);
		assertContainerEvent(listenerStub.getEvent(1), security.onUpdate(), security, time2, 12345);
	}
	
	@Test
	public void testRound() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.buildUpdate());
		
		assertEquals(of("156900"), security.round(of("156900.0000000")));
		assertEquals(of("156900"), security.round(of("156901")));
		assertEquals(of("156900"), security.round(of("156903.6281963")));
		assertEquals(of("156900"), security.round(of("156904.6281963")));
		assertEquals(of("156910"), security.round(of("156905.1123963")));
		assertEquals(of("156910"), security.round(of("156908")));
		assertEquals(of("156910"), security.round(of("156910")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("1"))
				.buildUpdate());
		
		assertEquals(of("156900"), security.round(of("156900.0000000")));
		assertEquals(of("156901"), security.round(of("156901")));
		assertEquals(of("156904"), security.round(of("156903.6281963")));
		assertEquals(of("156905"), security.round(of("156904.6281963")));
		assertEquals(of("156905"), security.round(of("156905.1123963")));
		assertEquals(of("156908"), security.round(of("156908")));
		assertEquals(of("156910"), security.round(of("156910")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.05"))
				.buildUpdate());
		
		assertEquals(of("156900.00"), security.round(of("156900.0000000")));
		assertEquals(of("156901.00"), security.round(of("156901")));
		assertEquals(of("156903.65"), security.round(of("156903.6281963")));
		assertEquals(of("156904.65"), security.round(of("156904.6281963")));
		assertEquals(of("156905.10"), security.round(of("156905.1123963")));
		assertEquals(of("156908.00"), security.round(of("156908")));
		assertEquals(of("156910.00"), security.round(of("156910")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.buildUpdate());
		
		assertEquals(of("156900.00"), security.round(of("156900.0000000")));
		assertEquals(of("156901.00"), security.round(of("156901")));
		assertEquals(of("156903.63"), security.round(of("156903.6281963")));
		assertEquals(of("156904.63"), security.round(of("156904.6281963")));
		assertEquals(of("156905.11"), security.round(of("156905.1123963")));
		assertEquals(of("156908.00"), security.round(of("156908")));
		assertEquals(of("156910.00"), security.round(of("156910")));
	}
	
	@Test
	public void testPriceToValue2() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		assertEquals(ofRUB5("760173.08290"), security.priceToValue(of("112090"), of("5")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.67818"))
				.buildUpdate());
		
		assertEquals(ofRUB5("547942.99098"), security.priceToValue(of("1154.23"), of("7")));
	}
	
	@Test (expected=ArithmeticException.class)
	public void testPriceToValue2_ThrowsIfOddPrice() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		security.priceToValue(of("112096"), of("5"));
	}
	
	@Test
	public void testPriceToValue1() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		assertEquals(ofRUB5("152034.61658"), security.priceToValue(of("112090")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.67818"))
				.buildUpdate());
		
		assertEquals(ofRUB5("78277.57014"), security.priceToValue(of("1154.23")));
	}
	
	@Test (expected=ArithmeticException.class)
	public void testPriceToValue1_ThrowsIfOddPrice() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		security.priceToValue(of("112096"));
	}
	
	@Test
	public void testPriceToValueWR2() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		assertEquals(ofRUB5("760173.08290"), security.priceToValueWR(of("112090"), of("5")));
		assertEquals(ofRUB5("760173.08290"), security.priceToValueWR(of("112093"), of("5")));
		assertEquals(ofRUB5("760173.08290"), security.priceToValueWR(of("112090.48991"), of("5")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.67818"))
				.buildUpdate());
		
		assertEquals(ofRUB5("547942.99098"), security.priceToValueWR(of("1154.23"), of("7")));
		assertEquals(ofRUB5("547942.99098"), security.priceToValueWR(of("1154.231762"), of("7")));
	}
	
	@Test
	public void testPriceToValueWR1() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("10"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.56362"))
				.buildUpdate());
		
		assertEquals(ofRUB5("152034.61658"), security.priceToValueWR(of("112090.123")));
		
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.67818"))
				.buildUpdate());
		
		assertEquals(ofRUB5("78277.57014"), security.priceToValueWR(of("1154.232294")));
	}

}
