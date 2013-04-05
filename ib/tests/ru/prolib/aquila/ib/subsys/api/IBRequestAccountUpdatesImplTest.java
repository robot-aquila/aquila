package ru.prolib.aquila.ib.subsys.api;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestAccountUpdatesImpl;

/**
 * 2012-11-28<br>
 * $Id: IBRequestAccountUpdatesImplTest.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestAccountUpdatesImplTest {
	private static EventSystem eSys;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType onUpdateAccount;
	private EventType onUpdatePortfolio;
	private EventType wOnUpdatePortfolio;
	private EventType wOnUpdateAccount;
	private IBClient client;
	private EditableTerminal terminal;
	private IBServiceLocator locator;
	private IBRequestAccountUpdatesImpl request;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		eSys = new EventSystemImpl();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		client = control.createMock(IBClient.class);
		terminal = control.createMock(EditableTerminal.class);
		wOnUpdatePortfolio = control.createMock(EventType.class);
		wOnUpdateAccount = control.createMock(EventType.class);

		dispatcher = eSys.createEventDispatcher();
		onUpdateAccount = eSys.createGenericType(dispatcher);
		onUpdatePortfolio = eSys.createGenericType(dispatcher);
		request = new IBRequestAccountUpdatesImpl(locator, dispatcher,
				onUpdateAccount, onUpdatePortfolio, "TEST");
		expect(client.OnUpdateAccount()).andStubReturn(wOnUpdateAccount);
		expect(client.OnUpdatePortfolio()).andStubReturn(wOnUpdatePortfolio);
		expect(locator.getApiClient()).andStubReturn(client);
		expect(locator.getTerminal()).andStubReturn(terminal);
		
		eSys.getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		eSys.getEventQueue().stop();
		eSys.getEventQueue().join(1000);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(dispatcher, request.getEventDispatcher());
		assertSame(onUpdateAccount, request.OnUpdateAccount());
		assertSame(onUpdatePortfolio, request.OnUpdatePortfolio());
		assertSame(locator, request.getServiceLocator());
		assertEquals("TEST", request.getAccount());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfNullAccount() throws Exception {
		new IBRequestAccountUpdatesImpl(locator, dispatcher, onUpdateAccount,
				onUpdatePortfolio, null);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(request.equals(request));
		assertFalse(request.equals(null));
		assertFalse(request.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>()
			.add(locator)
			.add(control.createMock(IBServiceLocator.class));
		Variant<EventDispatcher> vEd = new Variant<EventDispatcher>(vLoc)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vTua = new Variant<EventType>(vEd)
			.add(control.createMock(EventType.class))
			.add(onUpdateAccount);
		Variant<EventType> vTup = new Variant<EventType>(vTua)
			.add(onUpdatePortfolio)
			.add(control.createMock(EventType.class));
		Variant<String> vAcc = new Variant<String>(vTup)
			.add("TEST")
			.add("AC1");
		int foundCnt = 0;
		IBRequestAccountUpdatesImpl x = null, found = null;
		do {
			x = new IBRequestAccountUpdatesImpl(vLoc.get(), vEd.get(),
					vTua.get(), vTup.get(), vAcc.get());
			if ( request.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vAcc.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onUpdateAccount, found.OnUpdateAccount());
		assertSame(onUpdatePortfolio, found.OnUpdatePortfolio());
		assertEquals("TEST", found.getAccount());
	}
	
	@Test
	public void testStart_Ok() throws Exception {
		wOnUpdateAccount.addListener(request);
		wOnUpdatePortfolio.addListener(request);
		client.reqAccountUpdates(true, "TEST");
		control.replay();
		request.start();
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndConnected() throws Exception {
		wOnUpdateAccount.addListener(request);
		wOnUpdatePortfolio.addListener(request);
		client.reqAccountUpdates(true, "TEST");
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(true);
		terminal.firePanicEvent(1, "IBRequestAccountUpdatesImpl#start");
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndDisconnected() throws Exception {
		wOnUpdateAccount.addListener(request);
		wOnUpdatePortfolio.addListener(request);
		client.reqAccountUpdates(true, "TEST");
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(false);
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStop_Ok() throws Exception {
		client.reqAccountUpdates(false, "TEST");
		control.replay();
		
		request.stop();
		
		control.verify();
	}
	
	@Test
	public void testStart_IgnoreExceptions() throws Exception {
		client.reqAccountUpdates(false, "TEST");
		expectLastCall().andThrow(new IBException("Test exception"));
		control.replay();
		
		request.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnUpdateAccount_IfMatchedAcc() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final IBEventUpdateAccount init,expected;
		init = new IBEventUpdateAccount(wOnUpdateAccount,"k","v","USD","TEST");
		expected = new IBEventUpdateAccount(onUpdateAccount, init);
		onUpdateAccount.addListener(new EventListener() {
			@Override
			public void onEvent(Event actual) {
				assertEquals(expected, actual);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(init);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnUpdateAccount_IfMismatchedAcc() throws Exception {
		final CountDownLatch called = new CountDownLatch(1);
		final IBEventUpdateAccount init;
		init = new IBEventUpdateAccount(wOnUpdateAccount,"k","v","USD","BUZZ");
		onUpdateAccount.addListener(new EventListener() {
			@Override
			public void onEvent(Event actual) {
				called.countDown();
			}
		});
		control.replay();
		request.onEvent(init);
		control.verify();
		eSys.getEventQueue().stop();
		assertTrue(eSys.getEventQueue().join(100));
		assertEquals("Unexpected incoming event has been counted ",
				1, called.getCount());
	}
	
	@Test
	public void testOnEvent_OnUpdatePortfolio_IfMatchedAcc() throws Exception {
		Contract c = new Contract(); c.m_symbol = "SBER";
		final CountDownLatch finished = new CountDownLatch(1);
		final IBEventUpdatePortfolio init,expected;
		init = new IBEventUpdatePortfolio(wOnUpdatePortfolio, c,
				10, 1.2, 3.4, 5.6, 7.8, 9.1, "TEST");
		expected = new IBEventUpdatePortfolio(onUpdatePortfolio, init);
		onUpdatePortfolio.addListener(new EventListener() {
			@Override
			public void onEvent(Event actual) {
				assertEquals(expected, actual);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(init);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnUpdatePortfolio_IfMismatchedAcc()
			throws Exception
	{
		Contract c = new Contract(); c.m_symbol = "SBER";
		final CountDownLatch called = new CountDownLatch(1);
		final IBEventUpdatePortfolio init;
		init = new IBEventUpdatePortfolio(wOnUpdatePortfolio, c,
				10, 1.2, 3.4, 5.6, 7.8, 9.1, "ANOTHER");
		onUpdatePortfolio.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				called.countDown();
			}
		});
		control.replay();
		request.onEvent(init);
		control.verify();
		eSys.getEventQueue().stop();
		assertTrue(eSys.getEventQueue().join(100));
		assertEquals("Unexpected incoming event has been counted ",
				1, called.getCount());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 230947)
			.append(locator)
			.append(dispatcher)
			.append(onUpdateAccount)
			.append(onUpdatePortfolio)
			.append("TEST")
			.toHashCode();
		assertEquals(hashCode, request.hashCode());
	}

}
