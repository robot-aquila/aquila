package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.probe.*;
import ru.prolib.aquila.probe.internal.FORTSSecurityCtrl.EveningClearing;
import ru.prolib.aquila.probe.internal.FORTSSecurityCtrl.ForTick;

public class FORTSSecurityCtrlTest {
	private static Symbol symbol;
	private static BMUtils ut = new BMUtils();
	
	static {
		symbol = new Symbol("RTS-12.14", "FORTS", "USD", SymbolType.FUTURE);
	}
	
	private IMocksControl control;
	private Scheduler scheduler;
	private EditableSecurity security;
	private PROBETerminal terminal;
	private SecurityProperties props;
	private FORTSSecurityCtrl ctrl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		scheduler = control.createMock(Scheduler.class);
		terminal = new PROBETerminalBuilder()
			.withScheduler(scheduler)
			.buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		props = new SecurityProperties();
		props.setDisplayName("RTS-future-12.14");
		props.setLotSize(1);
		props.setPricePrecision(0);
		props.setMinStepSize(10d);
		props.setInitialMarginCalcBase(0.15d);
		props.setStepPriceCalcBase(0.2d);
		ctrl = new FORTSSecurityCtrl(terminal, security, props);
		terminal.getEventSystem().getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		terminal.getEventSystem().getEventQueue().stop();
		terminal.getEventSystem().getEventQueue().join(1000);
	}
	
	@Test
	public void testSchemeVersions() throws Exception {
		assertEquals(2, Security.VERSION);
	}
	
	@Test
	public void testDoInitialTask() throws Exception {
		final Vector<Event> actual = new Vector<Event>();
		terminal.OnSecurityAvailable().addSyncListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		ctrl.doInitialTask(new Tick(DateTime.now(), 142912d));
		
		assertEquals("RTS-future-12.14", security.getDisplayName());
		assertEquals(new Integer(1), security.getLotSize());
		assertEquals(new Integer(0), security.getPrecision());
		assertEquals(10d, security.getMinStepSize(), 0.1d);
		assertNull(security.getAskPrice());
		assertNull(security.getAskSize());
		assertNull(security.getBidPrice());
		assertNull(security.getBidSize());
		assertEquals(142912d, security.getClosePrice(), 0.01d);
		assertEquals(142912d, security.getHighPrice(), 0.01d);
		assertEquals(142912d, security.getLowPrice(), 0.01d);
		assertEquals(142912d, security.getOpenPrice(), 0.01d);
		assertEquals(142912d, security.getInitialMargin(), 0.01d);
		assertEquals(142912d, security.getInitialPrice(), 0.01d);
		assertNull(security.getLastPrice());
		assertNull(security.getLastTrade());
		assertNull(security.getMaxPrice());
		assertNull(security.getMinPrice());
		assertEquals(1d, security.getMinStepPrice(), 0.1d);
		
		assertEquals(1, actual.size());
		assertEquals(new SecurityEvent(terminal.OnSecurityAvailable(), security), actual.get(0));
	}
	
	@Test
	public void testDoFinalTask() throws Exception {
		ctrl.doFinalTask(new Tick(DateTime.now(), 115240d));		
	}
	
	@Test
	public void testDoDailyTask_TickBeforeEveningClearing() throws Exception {
		expect(scheduler.schedule(
					eq(new FORTSSecurityCtrl.EveningClearing(ctrl)),
					eq(new DateTime(2014, 11, 18, 18, 55, 0, 0))))
				.andReturn(null);
		control.replay();
		
		ctrl.doDailyTask(null, // not used
				new Tick(new DateTime(2014, 11, 18, 15, 34, 29, 0), 0d));
		
		control.verify();
	}
	
	@Test
	public void testDoDailyTask_TickAtEveningClearing() throws Exception {
		expect(scheduler.schedule(
					eq(new FORTSSecurityCtrl.EveningClearing(ctrl)),
					eq(new DateTime(2014, 11, 19, 18, 55, 0, 0))))
				.andReturn(null);
		control.replay();
		
		ctrl.doDailyTask(null, // not used
				new Tick(new DateTime(2014, 11, 18, 18, 55, 0, 0), 0d));
		
		control.verify();
	}
	
	@Test
	public void testDoDailyTask_TickAfterEveningClearing() throws Exception {
		expect(scheduler.schedule(
					eq(new FORTSSecurityCtrl.EveningClearing(ctrl)),
					eq(new DateTime(2014, 11, 19, 18, 55, 0, 0))))
				.andReturn(null);
		control.replay();
		
		ctrl.doDailyTask(null, // not used
				new Tick(new DateTime(2014, 11, 18, 23, 19, 48, 354), 0d));
		
		control.verify();
	}

	
	@Test
	public void testEveningClearing() throws Exception {
		security.setLastPrice(112590d); // Используется как цена закрытия сессии
		
		ctrl.eveningClearing();
		
		assertEquals(112590d, security.getClosePrice(), 0.01d);
		assertNull(security.getHighPrice());
		assertNull(security.getLowPrice());
		assertNull(security.getOpenPrice());
		assertEquals(112590d, security.getInitialPrice(), 0.01d);
		assertEquals(112590d, security.getInitialMargin(), 0.01d);
		assertEquals(1d, security.getMinStepPrice(), 0.1d);
	}
	
	@Test
	public void testCreateTask() throws Exception {
		Tick tick = new Tick(DateTime.now(), 824d);
		assertEquals(new ForTick(ctrl, tick), ctrl.createTask(tick));
	}
	
	@Test
	public void testOnTick() throws Exception {
		ctrl.doInitialTask(new Tick(null, 120140d));
		DateTime time = DateTime.now();
		Trade expTrade = ut.tradeFromTick(new Tick(time, 119540d, 120d), security); 
		final List<Event> expected = new Vector<Event>(),
				actual = new Vector<Event>();
		expected.add(new SecurityEvent(security.OnChanged(), security));
		expected.add(new SecurityTradeEvent(security.OnTrade(), security, expTrade));
		final CountDownLatch finished = new CountDownLatch(2);
		EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
				finished.countDown();
			}
		};
		security.OnChanged().addListener(listener);
		security.OnTrade().addListener(listener);
		
		ctrl.onTick(new Tick(time, 119540d, 120d));
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected, actual);
		assertEquals(expTrade, security.getLastTrade());
		assertEquals(119540d, security.getLastPrice(), 0.1d);
		assertEquals(120140d, security.getHighPrice(), 0.1d);
		assertEquals(119540d, security.getLowPrice(), 0.1d);
		assertEquals(120140d, security.getOpenPrice(), 0.1d);
		assertEquals(120140d, security.getClosePrice(), 0.1d);
	}
	
	@Test
	public void testOnTick_IfOpenPriceUndefined() throws Exception {
		security.setMinStepSize(10d);
		security.setMinStepPrice(1d);
		security.setHighPrice(112000d);
		security.setLowPrice(111000d);
		
		ctrl.onTick(new Tick(DateTime.now(), 115240d, 10d));
		
		assertEquals(115240d, security.getOpenPrice(), 0.1d);
		assertEquals(115240d, security.getHighPrice(), 0.1d);
		assertEquals(115240d, security.getLowPrice(), 0.1d);
	}
	
	@Test
	public void testOnTick_UpdatesHigh() throws Exception {
		security.setMinStepSize(10d);
		security.setMinStepPrice(1d);
		ctrl.doInitialTask(new Tick(null, 120140d));
		
		ctrl.onTick(new Tick(DateTime.now(), 135240d, 10d));
		
		assertEquals(135240d, security.getHighPrice(), 1d);
		assertEquals(120140d, security.getLowPrice(), 1d);
	}
	
	@Test
	public void testOnTick_UpdatesLow() throws Exception {
		security.setMinStepSize(10d);
		security.setMinStepPrice(1d);
		ctrl.doInitialTask(new Tick(null, 120140d));
		
		ctrl.createTask(new Tick(DateTime.now(), 115240d, 10d)).run();
		
		assertEquals(120140d, security.getHighPrice(), 1d);
		assertEquals(115240d, security.getLowPrice(), 1d);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(ctrl.equals(ctrl));
		assertFalse(ctrl.equals(null));
		assertFalse(ctrl.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<PROBETerminal> vTerm = new Variant<PROBETerminal>()
				.add(terminal)
				.add(control.createMock(PROBETerminal.class));
		Variant<EditableSecurity> vSec = new Variant<EditableSecurity>(vTerm)
				.add(security)
				.add(control.createMock(EditableSecurity.class));
		Variant<SecurityProperties> vProps =
					new Variant<SecurityProperties>(vSec)
				.add(props)
				.add(control.createMock(SecurityProperties.class));
		Variant<?> iterator = vProps;
		int foundCnt = 0;
		FORTSSecurityCtrl x = null, found = null;
		do {
			x = new FORTSSecurityCtrl(vTerm.get(), vSec.get(), vProps.get());
			if ( ctrl.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(security, found.getSecurity());
		assertSame(props, found.getSecurityProperties());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = ctrl.getClass().getSimpleName() + "{" + symbol + "}";
		assertEquals(expected, ctrl.toString());
	}
	
	@Test
	public void testEveningClearing_run() throws Exception {
		FORTSSecurityCtrl ctrl = control.createMock(FORTSSecurityCtrl.class);
		ctrl.eveningClearing();
		control.replay();
		
		new EveningClearing(ctrl).run();
		
		control.verify();
	}
	
	@Test
	public void testEveningClearing_ToString() throws Exception {
		EveningClearing delegate = new EveningClearing(ctrl);
		String expected = ctrl.toString() + ".EveningClearing";
		assertEquals(expected, delegate.toString());
	}
	
	@Test
	public void testEventClearing_Equals() throws Exception {
		FORTSSecurityCtrl ctrl2 = control.createMock(FORTSSecurityCtrl.class);
		EveningClearing delegate1 = new EveningClearing(ctrl),
				delegate2 = new EveningClearing(ctrl),
				delegate3 = new EveningClearing(ctrl2);
		
		assertFalse(delegate1.equals(null));
		assertFalse(delegate1.equals(this));
		assertFalse(delegate1.equals(delegate3));
		assertEquals(delegate1, delegate1);
		assertEquals(delegate1, delegate2);
	}
	
	@Test
	public void testForTick_run() throws Exception {
		Tick tick = new Tick(new DateTime(2014, 12, 28, 2, 23, 8, 0), 114d);
		FORTSSecurityCtrl ctrl = control.createMock(FORTSSecurityCtrl.class);
		ctrl.onTick(eq(tick));
		control.replay();
		
		new ForTick(ctrl, tick).run();
		
		control.verify();
	}

	@Test
	public void testForTick_ToString() throws Exception {
		Tick tick = new Tick(new DateTime(2014, 12, 28, 2, 23, 8, 0), 114d);
		ForTick delegate = new ForTick(ctrl, tick);
		String expected = ctrl.toString() + ".ForTick{" + tick + "}";
		assertEquals(expected, delegate.toString());
	}
	
	@Test
	public void testForTick_Equals() throws Exception {
		Tick tick1 = new Tick(new DateTime(2014, 12, 28, 2, 23, 8, 0), 114d),
			 tick2 = new Tick(new DateTime(2015,  1, 14, 2, 23, 8, 0), 229d);
		FORTSSecurityCtrl ctrl2 = control.createMock(FORTSSecurityCtrl.class);
		Variant<FORTSSecurityCtrl> vCtrl = new Variant<FORTSSecurityCtrl>()
				.add(ctrl)
				.add(ctrl2);
		Variant<Tick> vTick = new Variant<Tick>(vCtrl)
				.add(tick1)
				.add(tick2);
		Variant<?> iterator = vTick;
		ForTick x, found = null, expected = new ForTick(ctrl, tick1);
		int foundCnt = 0;
		do {
			x = new ForTick(vCtrl.get(), vTick.get());
			if ( expected.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(expected, found);
		// special cases
		assertEquals(expected, expected);
		assertFalse(expected.equals(null));
		assertFalse(expected.equals(this));
	}

}
