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
import ru.prolib.aquila.ib.assembler.IBRequestContractHandler;
import ru.prolib.aquila.ib.assembler.IBRequestSecurityHandler;
import ru.prolib.aquila.ib.assembler.cache.Cache;

public class IBTerminalImplTest {
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
		orderProcessor = control.createMock(OrderProcessor.class);
		scheduler = control.createMock(Scheduler.class);
		cache = control.createMock(Cache.class);
		client = control.createMock(IBClient.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Terminal");
		onConn = dispatcher.createType("OnConnected");
		onDisc = dispatcher.createType("OnDisconnected");
		onStarted = dispatcher.createType("OnStarted");
		onStopped = dispatcher.createType("OnStopped");
		onPanic = dispatcher.createType("OnPanic");
		onReqSecurityError = dispatcher.createType("OnRequestSecurityError");
		terminal = new IBTerminalImpl(es, scheduler, starter, securities,
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
			x = new IBTerminalImpl(vEs.get(), vSched.get(), vSta.get(),
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
		assertSame(cache, found.getCache());
		assertSame(client, found.getClient());
	}
	
	@Test
	public void testConstruct_Short() throws Exception {
		terminal = new IBTerminalImpl(es, starter, securities, portfolios,
				orders, dispatcher, onConn, onDisc, onStarted,
				onStopped, onPanic, onReqSecurityError, cache, client);
		Terminal expected = new IBTerminalImpl(es, new SchedulerLocal(),
				starter, securities, portfolios, orders, 
				new TerminalController(), dispatcher, onConn, onDisc,
				onStarted, onStopped, onPanic,
				onReqSecurityError, cache, client);
		assertEquals(expected, terminal);
	}
	
	@Test
	public void testRequestSecurity() throws Exception {
		terminal.getOrderNumerator().set(824);
		SecurityDescriptor descr =
			new SecurityDescriptor("SPXS", "SMART", "USD", SecurityType.STK);
		client.setContractHandler(eq(825),
				eq(new IBRequestSecurityHandler(terminal, 825, descr)));
		Contract expected = new Contract();
		expected.m_symbol = "SPXS";
		expected.m_exchange = "SMART";
		expected.m_currency = "USD";
		expected.m_secType = "STK";
		client.reqContractDetails(eq(825), eq(expected));
		control.replay();
		
		terminal.requestSecurity(descr);
		
		control.verify();
	}
	
	@Test
	public void testRequestContract() throws Exception {
		terminal.getOrderNumerator().set(811);
		client.setContractHandler(eq(812),
				eq(new IBRequestContractHandler(terminal, 812, 559)));
		Contract expected = new Contract();
		expected.m_conId = 559;
		client.reqContractDetails(eq(812), eq(expected));
		control.replay();
		
		terminal.requestContract(559);
		
		control.verify();
	}

}
