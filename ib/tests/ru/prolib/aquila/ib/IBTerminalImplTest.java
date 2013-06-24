package ru.prolib.aquila.ib;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.IBRequestSecurityHandler;
import ru.prolib.aquila.ib.assembler.cache.Cache;

public class IBTerminalImplTest {
	private IMocksControl control;
	private EventSystem es;
	private Starter starter;
	private EditableSecurities securities;
	private EditablePortfolios portfolios;
	private EditableOrders orders;
	private EditableOrders stopOrders;
	private OrderProcessor orderProcessor;
	private EventDispatcher dispatcher;
	private EventType onConn,onDisc,onStarted,onStopped,onPanic,onSecReqErr;
	private TerminalController controller;
	private Timer timer;
	private Cache cache;
	private IBClient client;
	private IBTerminalImpl terminal;
	
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
		stopOrders = control.createMock(EditableOrders.class);
		orderProcessor = control.createMock(OrderProcessor.class);
		timer = control.createMock(Timer.class);
		cache = control.createMock(Cache.class);
		client = control.createMock(IBClient.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Terminal");
		onConn = dispatcher.createType("OnConnected");
		onDisc = dispatcher.createType("OnDisconnected");
		onStarted = dispatcher.createType("OnStarted");
		onStopped = dispatcher.createType("OnStopped");
		onPanic = dispatcher.createType("OnPanic");
		onSecReqErr = dispatcher.createType("OnSecurityRequestError");
		terminal = new IBTerminalImpl(es, timer, starter, securities,
				portfolios, orders, stopOrders,  controller, dispatcher, 
				onConn, onDisc, onStarted, onStopped, onPanic,
				onSecReqErr, cache, client);
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
		Variant<Timer> vTmr = new Variant<Timer>(vEs)
			.add(timer)
			.add(control.createMock(Timer.class));
		Variant<Starter> vSta = new Variant<Starter>(vTmr)
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
		Variant<EditableOrders> vStOrds = new Variant<EditableOrders>(vOrds)
			.add(stopOrders)
			.add(control.createMock(EditableOrders.class));
		Variant<TerminalController> vCtrl =
				new Variant<TerminalController>(vStOrds)
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
		Variant<String> vSecReqErrId = new Variant<String>(vPanicId)
			.add("OnSecurityRequestError")
			.add("OnSecurityRequestErrorX");
		Variant<Cache> vCache = new Variant<Cache>(vSecReqErrId)
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<IBClient> vClnt = new Variant<IBClient>(vCache)
			.add(client)
			.add(control.createMock(IBClient.class));
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
		IBTerminalImpl x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new IBTerminalImpl(vEs.get(), vTmr.get(), vSta.get(),
					vScs.get(), vPts.get(), vOrds.get(), vStOrds.get(),
					vCtrl.get(), d, d.createType(vConnId.get()),
					d.createType(vDiscId.get()), d.createType(vStartId.get()),
					d.createType(vStopId.get()), d.createType(vPanicId.get()),
					d.createType(vSecReqErrId.get()), vCache.get(),
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
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertSame(starter, found.getStarter());
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onConn, found.OnConnected());
		assertEquals(onDisc, found.OnDisconnected());
		assertEquals(onStarted, found.OnStarted());
		assertEquals(onStopped, found.OnStopped());
		assertEquals(onPanic, found.OnPanic());
		assertSame(TerminalState.STOPPED, found.getTerminalState());
		assertSame(orderProcessor, found.getOrderProcessorInstance());
		assertSame(timer, found.getTimer());
		assertEquals(onSecReqErr, found.OnSecurityRequestError());
		assertSame(cache, found.getCache());
		assertSame(client, found.getClient());
	}
	
	@Test
	public void testConstruct15() throws Exception {
		terminal = new IBTerminalImpl(es, starter, securities, portfolios,
				orders, stopOrders,	dispatcher, onConn, onDisc, onStarted,
				onStopped, onPanic, onSecReqErr, cache, client);
		Terminal expected = new IBTerminalImpl(es, new TimerLocal(), starter,
				securities, portfolios, orders, stopOrders,
				new TerminalController(), dispatcher, onConn, onDisc,
				onStarted, onStopped, onPanic, onSecReqErr, cache, client);
		assertEquals(expected, terminal);
	}
	
	@Test
	public void testRequestSecurity() throws Exception {
		SecurityDescriptor descr =
			new SecurityDescriptor("SPXS", "SMART", "USD", SecurityType.STK);
		expect(client.nextReqId()).andReturn(344);
		client.setContractHandler(eq(344),
				eq(new IBRequestSecurityHandler(terminal, 344, descr)));
		Contract expected = new Contract();
		expected.m_symbol = "SPXS";
		expected.m_exchange = "SMART";
		expected.m_currency = "USD";
		expected.m_secType = "STK";
		client.reqContractDetails(eq(344), eq(expected));
		control.replay();
		
		terminal.requestSecurity(descr);
		
		control.verify();
	}
	
	@Test
	public void testFireSecurityRequestError() throws Exception {
		control.replay();
		
		terminal.fireSecurityRequestError(new SecurityDescriptor("SPXS",
				"SMART", "USD", SecurityType.STK), 80, "test error");
		
		control.verify();
	}

}
