package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

public class QUIKTerminalImplTest {
	private IMocksControl control;
	private EventSystem es;
	private Starter starter;
	private EditableSecurities securities;
	private EditablePortfolios portfolios;
	private EditableOrders orders;
	private OrderProcessor orderProcessor;
	private EventDispatcher dispatcher;
	private EventType onConn, onDisc, onStarted, onStopped, onPanic,
		onReqSecurityError;
	private TerminalController controller;
	private Scheduler scheduler;
	private Cache cache;
	private QUIKClient client;
	private QUIKTerminalImpl terminal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		controller = control.createMock(TerminalController.class);
		starter = control.createMock(Starter.class);
		securities = control.createMock(EditableSecurities.class);
		portfolios = control.createMock(EditablePortfolios.class);
		orders = control.createMock(EditableOrders.class);
		orderProcessor = control.createMock(OrderProcessor.class);
		scheduler = control.createMock(Scheduler.class);
		cache = control.createMock(Cache.class);
		client = control.createMock(QUIKClient.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Terminal");
		onConn = dispatcher.createType("OnConnected");
		onDisc = dispatcher.createType("OnDisconnected");
		onStarted = dispatcher.createType("OnStarted");
		onStopped = dispatcher.createType("OnStopped");
		onPanic = dispatcher.createType("OnPanic");
		onReqSecurityError = dispatcher.createType("OnRequestSecurityError");
		terminal = new QUIKTerminalImpl(es, scheduler, starter, securities,
				portfolios, orders, controller, dispatcher, 
				onConn, onDisc, onStarted, onStopped, onPanic,
				onReqSecurityError, cache, client);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(terminal.equals(terminal));
		assertFalse(terminal.equals(null));
		assertFalse(terminal.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(control.createMock(EventSystem.class));
		Variant<Scheduler> vSched = new Variant<Scheduler>(vEs)
			.add(scheduler)
			.add(control.createMock(Scheduler.class));
		Variant<Starter> vSta = new Variant<Starter>(vSched)
			.add(starter)
			.add(control.createMock(Starter.class));
		Variant<EditableSecurities> vScs = new Variant<EditableSecurities>(vSta)
			.add(securities)
			.add(control.createMock(EditableSecurities.class));
		Variant<EditablePortfolios> vPts = new Variant<EditablePortfolios>(vScs)
			.add(portfolios)
			.add(control.createMock(EditablePortfolios.class));
		Variant<EditableOrders> vOrds = new Variant<EditableOrders>(vPts)
			.add(orders)
			.add(control.createMock(EditableOrders.class));
		Variant<TerminalController> vCtrl =
				new Variant<TerminalController>(vOrds)
			.add(controller)
			.add(control.createMock(TerminalController.class));
		Variant<String> vDispId = new Variant<String>(vCtrl)
			.add("Terminal")
			.add("TerminalX");
		Variant<String> vConnId = new Variant<String>(vDispId)
			.add("OnConnected")
			.add("OnConnectedX");
		Variant<String> vDiscId = new Variant<String>(vConnId)
			.add("OnDisconnected")
			.add("OnDisconnectedX");
		Variant<String> vStartId = new Variant<String>(vDiscId)
			.add("OnStarted")
			.add("OnStartedX");
		Variant<String> vStopId = new Variant<String>(vStartId)
			.add("OnStopped")
			.add("OnStoppedX");
		Variant<String> vPanicId = new Variant<String>(vStopId)
			.add("OnPanic")
			.add("OnPanicX");
		Variant<String> vReqSecId = new Variant<String>(vPanicId)
			.add("OnRequestSecurityError")
			.add("OnRequestSecurityErrorX");
		Variant<Cache> vCache = new Variant<Cache>(vReqSecId)
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<QUIKClient> vClnt = new Variant<QUIKClient>(vCache)
			.add(client)
			.add(control.createMock(QUIKClient.class));
		Variant<TerminalState> vStat = new Variant<TerminalState>(vClnt)
			.add(TerminalState.STOPPED)
			.add(TerminalState.STARTING);
		Variant<OrderProcessor> vOrdProc = new Variant<OrderProcessor>(vStat)
			.add(orderProcessor)
			.add(control.createMock(OrderProcessor.class));
		Variant<?> iterator = vOrdProc;
		terminal.setTerminalState(TerminalState.STOPPED);
		terminal.setOrderProcessorInstance(orderProcessor);
		int foundCnt = 0;
		QUIKTerminalImpl x, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new QUIKTerminalImpl(vEs.get(), vSched.get(), vSta.get(),
					vScs.get(), vPts.get(), vOrds.get(), 
					vCtrl.get(), d, d.createType(vConnId.get()),
					d.createType(vDiscId.get()), d.createType(vStartId.get()),
					d.createType(vStopId.get()), d.createType(vPanicId.get()),
					d.createType(vReqSecId.get()), vCache.get(),
					vClnt.get());
			x.setTerminalState(vStat.get());
			x.setOrderProcessorInstance(vOrdProc.get());
			if ( terminal.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(es, found.getEventSystem());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(starter, found.getStarter());
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onConn, found.OnConnected());
		assertEquals(onDisc, found.OnDisconnected());
		assertEquals(onStarted, found.OnStarted());
		assertEquals(onStopped, found.OnStopped());
		assertEquals(onPanic, found.OnPanic());
		assertSame(TerminalState.STOPPED, found.getTerminalState());
		assertSame(orderProcessor, found.getOrderProcessorInstance());
		assertSame(scheduler, found.getScheduler());
		assertEquals(onReqSecurityError, found.OnRequestSecurityError());
		assertSame(cache, found.getDataCache());
		assertSame(client, found.getClient());
	}
	
	@Test
	public void testConstruct_Short() throws Exception {
		terminal = new QUIKTerminalImpl(es, starter, securities, portfolios,
				orders, dispatcher, onConn, onDisc, onStarted,
				onStopped, onPanic, onReqSecurityError, cache, client);
		Terminal expected = new QUIKTerminalImpl(es, new SchedulerLocal(),
				starter, securities, portfolios, orders, 
				new TerminalController(), dispatcher, onConn, onDisc,
				onStarted, onStopped, onPanic,
				onReqSecurityError, cache, client);
		assertEquals(expected, terminal);
	}
	@Test
	public void testRequestSecurity() throws Exception {
		// Пока ничего не делает.
		control.replay();
		
		terminal.requestSecurity(null);
		
		control.verify();
	}

}
