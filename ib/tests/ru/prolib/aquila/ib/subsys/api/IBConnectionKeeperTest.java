package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Timer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * 2013-01-15<br>
 * $Id: IBConnectionKeeperTest.java 515 2013-02-11 05:52:28Z whirlwind $
 */
public class IBConnectionKeeperTest {
	private static IMocksControl control;
	private static IBServiceLocator locator;
	private static IBClientStarter starter;
	private static IBClient client;
	private static EventType onConnClosed, onConnOpened, onStarted, onStopped;
	private static EditableTerminal terminal;
	private static Timer timer;
	private static IBConnectionKeeper keeper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		starter = control.createMock(IBClientStarter.class);
		client = control.createMock(IBClient.class);
		onConnClosed = control.createMock(EventType.class);
		onConnOpened = control.createMock(EventType.class);
		onStarted = control.createMock(EventType.class);
		onStopped = control.createMock(EventType.class);
		terminal = control.createMock(EditableTerminal.class);
		timer = control.createMock(Timer.class);
		keeper = new IBConnectionKeeper(locator, starter);
		setStubExpectations();
	}
	
	private void setStubExpectations() {
		expect(locator.getApiClient()).andStubReturn(client);
		expect(locator.getTerminal()).andStubReturn(terminal);
		expect(client.OnConnectionClosed()).andStubReturn(onConnClosed);
		expect(client.OnConnectionOpened()).andStubReturn(onConnOpened);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		expect(locator.getTimer()).andStubReturn(timer);
	}
	
	@Test
	public void testStart() throws Exception {
		onConnClosed.addListener(same(keeper));
		onConnOpened.addListener(same(keeper));
		onStarted.addListener(same(keeper));
		onStopped.addListener(same(keeper));
		control.replay();
		
		keeper.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		control.replay();
		
		keeper.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnConnectionClosed() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		timer.schedule(eq(new IBConnectionKeeperTask(keeper)), eq(5000L));
		control.replay();
		
		keeper.onEvent(new EventImpl(onConnClosed));
		
		control.verify();
		
		// При повторном получении, событие отключения терминала не генерируется
		control.resetToStrict();
		setStubExpectations();
		timer.schedule(eq(new IBConnectionKeeperTask(keeper)), eq(5000L));
		control.replay();
		
		keeper.onEvent(new EventImpl(onConnClosed));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnConnectionOpened() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		
		keeper.onEvent(new EventImpl(onConnOpened));
		
		control.verify();
		
		// При повторном, событие подключения терминала не генерируется
		control.resetToStrict();
		setStubExpectations();
		control.replay();
		
		keeper.onEvent(new EventImpl(onConnOpened));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStarted() throws Exception {
		expect(terminal.started()).andReturn(true);
		expect(client.isConnected()).andReturn(false);
		starter.start();
		control.replay();
		
		keeper.onEvent(new EventImpl(onStarted));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStopped() throws Exception {
		client.eDisconnect();
		control.replay();
		
		keeper.onEvent(new EventImpl(onStopped));
		
		control.verify();
	}
	
	@Test
	public void testRestoreConnection() throws Exception {
		Variant<Boolean> vStarted = new Variant<Boolean>()
			.add(true)
			.add(false);
		Variant<Boolean> vConnected = new Variant<Boolean>(vStarted)
			.add(true)
			.add(false);
		Variant<?> iterator = vConnected;
		int foundCnt = 0;
		do {
			setUp();
			expect(terminal.started()).andStubReturn(vStarted.get());
			expect(client.isConnected()).andStubReturn(vConnected.get());
			if ( vStarted.get() && ! vConnected.get() ) {
				starter.start();
				foundCnt ++;
			}
			control.replay();
			
			keeper.restoreConnection();
			
			control.verify();
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
	}
	
}
