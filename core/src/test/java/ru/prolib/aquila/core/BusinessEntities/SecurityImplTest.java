package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

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
		security.update(SecurityField.TICK_SIZE, new FDecimal("0.005"));
		assertEquals(new Integer(3), security.getScale());
	}
	
	@Test
	public void testGetLotSize() throws Exception {
		getter = new Getter<Integer>() {
			@Override public Integer get() {
				return security.getLotSize();
			}	
		};
		testGetter(SecurityField.LOT_SIZE, 10, 1);
	}
	
	@Test
	public void testGetUpperPriceLimit() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return security.getUpperPriceLimit();
			}
		};
		testGetter(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("137.15"), new FDecimal("158.12"));
	}
	
	@Test
	public void testGetLowerPriceLimit() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return security.getLowerPriceLimit();
			}
		};
		testGetter(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("119.02"), new FDecimal("118.16"));
	}
	
	@Test
	public void testGetTickValue() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return security.getTickValue();
			}
		};
		testGetter(SecurityField.TICK_VALUE, new FMoney("440.09", "USD"), new FMoney("482.15", "RUB"));
	}
	
	@Test
	public void testGetTickSize() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return security.getTickSize();
			}
		};
		testGetter(SecurityField.TICK_SIZE, new FDecimal("10.0"), new FDecimal("0.05"));
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
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getOpenPrice();
			}
		};
		testGetter(SecurityField.OPEN_PRICE, 321.19d, 280.04d);
	}
	
	@Test
	public void testGetClosePrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getClosePrice();
			}
		};
		testGetter(SecurityField.CLOSE_PRICE, 10.03d, 12.34d);
	}
	
	@Test
	public void testGetLowPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getLowPrice();
			}
		};
		testGetter(SecurityField.LOW_PRICE, 8.02d, 15.87d);
	}

	@Test
	public void testGetHighPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getHighPrice();
			}
		};
		testGetter(SecurityField.HIGH_PRICE, 4586.13d, 7002.17d);
	}

	@Test
	public void testGetSettlementPrice() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return security.getSettlementPrice();
			}
		};
		testGetter(SecurityField.SETTLEMENT_PRICE, new FDecimal("215.86"), new FDecimal("114.12"));
	}

	@Test
	public void testGetInitialMargin() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return security.getInitialMargin();
			}
		};
		testGetter(SecurityField.INITIAL_MARGIN,
				new FMoney("118.99", "USD"), new FMoney("120.01", "RUB"));
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
		data.put(SecurityField.LOT_SIZE, 100);
		data.put(SecurityField.TICK_SIZE, new FDecimal("5.00"));
		data.put(SecurityField.TICK_VALUE, new FMoney("2.37", "RUB"));
		data.put(SecurityField.SETTLEMENT_PRICE, 200.01d);
		security.update(data);
		
		assertTrue(controller.hasMinimalData(security, null));
	}
	
	@Test
	public void testSecurityController_ProcessUpdate_SessionUpdate() {
		Instant time = T("2017-08-04T19:51:00Z");
		data.put(SecurityField.LOT_SIZE, 1);
		data.put(SecurityField.TICK_SIZE, new FDecimal("0.05"));
		data.put(SecurityField.TICK_VALUE, new FMoney("0.01", "RUB"));
		data.put(SecurityField.INITIAL_MARGIN, 2034.17d);
		data.put(SecurityField.SETTLEMENT_PRICE, 80.93d);
		data.put(SecurityField.OPEN_PRICE, 79.19d);
		data.put(SecurityField.HIGH_PRICE, 83.64d);
		data.put(SecurityField.LOW_PRICE, 79.19d);
		data.put(SecurityField.CLOSE_PRICE, 79.18d);
		security.update(data);
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addSyncListener(listener);
		
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
		data.put(SecurityField.LOT_SIZE, 1);
		data.put(SecurityField.TICK_SIZE, new FDecimal("0.05"));
		data.put(SecurityField.TICK_VALUE, new FMoney("0.01", "RUB"));
		data.put(SecurityField.INITIAL_MARGIN, 2034.17d);
		data.put(SecurityField.SETTLEMENT_PRICE, 80.93d);
		data.put(SecurityField.OPEN_PRICE, 79.19d);
		data.put(SecurityField.HIGH_PRICE, 83.64d);
		data.put(SecurityField.LOW_PRICE, 79.19d);
		data.put(SecurityField.CLOSE_PRICE, 79.18d);
		security.update(data);
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addSyncListener(listener);
		
		controller.processAvailable(security, time);
		
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	public void testUpdate_Tick_BestAsk() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T21:05:00Z");
		security.onBestAsk().addSyncListener(listenerStub);
		assertNull(security.getBestAsk());
		
		security.consume(toL1Update(Tick.ofAsk(T("1996-12-04T00:15:00Z"), 80.34d, 15L)));
		
		Tick expected = Tick.ofAsk(T("1996-12-04T00:15:00Z"), 80.34d, 15L);
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
		security.consume(toL1Update(Tick.ofAsk(T("2003-01-01T00:00:00Z"), 92.13d, 100L)));
		security.onBestAsk().addSyncListener(listenerStub);
		
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
		security.onBestBid().addSyncListener(listenerStub);
		assertNull(security.getBestBid());
		
		security.consume(toL1Update(Tick.ofBid(T("2010-09-11T03:15:25Z"), 12.48d, 500L)));
		
		Tick expected = Tick.ofBid(T("2010-09-11T03:15:25Z"), 12.48d, 500L);
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
		security.consume(toL1Update(Tick.ofBid(T("1992-07-24T15:45:00Z"), 52.94d, 1L)));
		security.onBestBid().addSyncListener(listenerStub);
		
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
		security.onLastTrade().addSyncListener(listenerStub);
		assertNull(security.getLastTrade());
		
		security.consume(toL1Update(Tick.ofTrade(T("1978-02-01T05:12:15Z"), 72.15d, 805L)));
		
		Tick expected = Tick.ofTrade(T("1978-02-01T05:12:15Z"), 72.15d, 805L);
		assertEquals(expected, security.getLastTrade());
		SecurityTickEvent e = (SecurityTickEvent) listenerStub.getEvent(0);
		assertContainerEventWUT(e, security.onLastTrade(), security, T("2017-08-04T21:10:00Z"));
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getTick());
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
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-03-04T01:27:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_ASK, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		// REFRESH_ASK will reset ask-side but bid-side quotes will be kept
		update.addRecord(Tick.of(TickType.BID, time, 100.02d, 800), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 102.45d, 100), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time, 12.35d, 20), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.28d, 30), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.33d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.30d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.34d, 15), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addSyncListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.33d, 10));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.34d, 15));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.35d, 20));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time, 100.02d, 800)); // this quote must be kept
		expectedBids.add(Tick.of(TickType.BID, time, 12.30d, 10));
		expectedBids.add(Tick.of(TickType.BID, time, 12.28d, 30));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time, 2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_RefreshBid() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-03-04T01:27:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_BID, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.BID, time, 100.02d, 800), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 102.45d, 100), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time, 12.35d, 20), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.28d, 30), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.33d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.30d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.34d, 15), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addSyncListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.33d, 10));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.34d, 15));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.35d, 20));
		expectedAsks.add(Tick.of(TickType.ASK, time, 102.45d, 100));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time, 12.30d, 10));
		expectedBids.add(Tick.of(TickType.BID, time, 12.28d, 30));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time, 2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Refresh() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.01"))
			.buildUpdate());

		Instant time = Instant.parse("2016-02-04T17:24:15Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.BID, time, 100.02d, 800), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 102.45d, 100), MDTransactionType.ADD);
		security.consume(update);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time, 12.35d, 20), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.28d, 30), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.33d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time, 12.30d, 10), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time, 12.34d, 15), MDTransactionType.ADD);
		security.onMarketDepthUpdate().addSyncListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.33d, 10));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.34d, 15));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.35d, 20));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time, 12.30d, 10));
		expectedBids.add(Tick.of(TickType.BID, time, 12.28d, 30));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time, 2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Update_Replace() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.01"))
			.buildUpdate());

		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.45d, 100), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.40d, 150), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.30d, 120), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.00d, 220), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 101.00d, 450), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1, 100.02d, 800), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1, 100.01d, 100), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1,  99.98d, 500), MDTransactionType.ADD);
		security.consume(update);
		
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time2, 102.30d, 999), MDTransactionType.REPLACE);
		update.addRecord(Tick.of(TickType.BID, time2,  99.98d, 199), MDTransactionType.REPLACE);
		security.onMarketDepthUpdate().addSyncListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time1, 101.00d, 450));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.00d, 220));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.30d, 999));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.45d, 100));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time1, 100.02d, 800));
		expectedBids.add(Tick.of(TickType.BID, time1, 100.01d, 100));
		expectedBids.add(Tick.of(TickType.BID, time2,  99.98d, 199));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time2, 2);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}

	@Test
	public void testUpdate_MDUpdate_Update_Delete() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.0001"))
			.buildUpdate());

		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.45d, 100), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.40d, 150), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.30d, 120), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.00d, 220), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 101.00d, 450), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1, 100.02d, 800), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1, 100.01d, 100), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.BID, time1,  99.98d, 500), MDTransactionType.ADD);
		security.consume(update);
		
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time2, 102.30d, 0), MDTransactionType.DELETE);
		update.addRecord(Tick.of(TickType.BID, time2,  99.98d, 0), MDTransactionType.DELETE);
		security.onMarketDepthUpdate().addSyncListener(listenerStub);
		
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time1, 101.00d, 450));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.00d, 220));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.45d, 100));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time1, 100.02d, 800));
		expectedBids.add(Tick.of(TickType.BID, time1, 100.01d, 100));
		MarketDepth expected = new MarketDepth(symbol1, expectedAsks, expectedBids, time2, 4);
		assertEquals(expected, security.getMarketDepth());
		assertEquals(1, listenerStub.getEventCount());
		SecurityMarketDepthEvent e = (SecurityMarketDepthEvent) listenerStub.getEvent(0);
		assertSame(security, e.getSecurity());
		assertEquals(expected, e.getMarketDepth());
	}
	
	@Test
	public void testUpdate_MDUpdate_Update_PriceRouding() {
		security.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.TICK_SIZE, new FDecimal("0.01"))
			.buildUpdate());

		Instant time1 = Instant.EPOCH;
		MDUpdateHeader header = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, symbol1);
		MDUpdateImpl update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.30d, 120), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.40d, 150), MDTransactionType.ADD);
		update.addRecord(Tick.of(TickType.ASK, time1, 102.45d, 100), MDTransactionType.ADD);
		security.consume(update);

		Instant time2 = Instant.EPOCH.plusSeconds(1000);
		header = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time2, symbol1);
		update = new MDUpdateImpl(header);
		update.addRecord(Tick.of(TickType.ASK, time2, 102.29651d, 500), MDTransactionType.REPLACE);
		update.addRecord(Tick.of(TickType.ASK, time2, 102.40561d, 250), MDTransactionType.REPLACE);
		update.addRecord(Tick.of(TickType.ASK, time2, 102.45021d, 200), MDTransactionType.REPLACE);
		security.consume(update);
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.30d, 500));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.41d, 250));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.45d, 200));
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
		container.onAvailable().addSyncListener(listenerStub);
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
		container.onUpdate().addSyncListener(listenerStub);
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

}
