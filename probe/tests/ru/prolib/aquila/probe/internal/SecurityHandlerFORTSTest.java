package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityTradeEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

public class SecurityHandlerFORTSTest {
	private static SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("RTS-12.14", "FORTS", "USD", SecurityType.FUT);
	}
	
	private IMocksControl control;
	private Scheduler scheduler;
	private EditableSecurity security;
	private PROBETerminal terminal;
	private SecurityProperties props;
	private SecurityHandlerFORTS handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		scheduler = control.createMock(Scheduler.class);
		terminal = new PROBETerminal("foo");
		security = terminal.getEditableSecurity(descr);
		props = new SecurityProperties();
		props.setDisplayName("RTS-future-12.14");
		props.setLotSize(1);
		props.setPricePrecision(0);
		props.setMinStepSize(10d);
		props.setInitialMarginCalculationBase(0.15d);
		props.setStepPriceCalculationBase(0.2d);
		handler = new SecurityHandlerFORTS(terminal, security, props);
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
		handler.doInitialTask(new Tick(DateTime.now(), 142912d));
		
		assertEquals("RTS-future-12.14", security.getDisplayName());
		assertEquals(1, security.getLotSize());
		assertEquals(0, security.getPrecision());
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
	}
	
	@Test
	public void testDoFinalTask() throws Exception {
		handler.doFinalTask(new Tick(DateTime.now(), 115240d));		
	}
	
	@Test
	public void testDoDailyTask() throws Exception {
		final List<Runnable> tmp = new Vector<Runnable>();
		DateTime expected = new DateTime(2014, 11, 18, 18, 55, 0, 0);
		expect(scheduler.schedule(anyObject(Runnable.class), eq(expected)))
			.andDelegateTo(new SchedulerLocal() {
				@Override public TaskHandler schedule(Runnable r, DateTime t) {
					tmp.add(r);
					return null;
				}
		});
		control.replay();
		terminal.setScheduler(scheduler);
		security.setLastPrice(112590d); // Используется как цена закрытия сессии
		
		handler.doDailyTask(null,
				new Tick(new DateTime(2014, 11, 18, 15, 34, 29, 0), 138940d));
		
		control.verify();
		tmp.get(0).run();
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
		DateTime time = DateTime.now();
		final List<Event> expected = new Vector<Event>(),
				actual = new Vector<Event>();
		expected.add(new SecurityEvent(security.OnChanged(), security));
		expected.add(new SecurityTradeEvent(security.OnTrade(), security,
			new BMUtils().tradeFromTick(new Tick(time, 119540d, 120d), security)));
		final CountDownLatch finished = new CountDownLatch(2);
		EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
				finished.countDown();
			}
		};
		security.OnChanged().addListener(listener);
		security.OnTrade().addListener(listener);
		handler.doInitialTask(new Tick(null, 120140d));
		
		handler.createTask(new Tick(time, 119540d, 120d)).run();
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() {
		fail("TODO: incomplete");
	}

}
