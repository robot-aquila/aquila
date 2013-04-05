package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Timer;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QServiceStarter;

/**
 * 2013-02-11<br>
 * $Id$
 */
public class QUIKConnectionKeeperTest {
	private IMocksControl control;
	private T2QServiceStarter starter;
	private QUIKServiceLocator locator;
	private Timer timer;
	private EditableTerminal terminal;
	private QUIKConnectionKeeper keeper;
	private EventType onStarted, onStopped, onDisconnected;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		starter = control.createMock(T2QServiceStarter.class);
		terminal = control.createMock(EditableTerminal.class);
		timer = control.createMock(Timer.class);
		locator = new QUIKServiceLocator(terminal);
		locator.setTimer(timer);
		onStarted = control.createMock(EventType.class);
		onStopped = control.createMock(EventType.class);
		onDisconnected = control.createMock(EventType.class);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconnected);
		keeper = new QUIKConnectionKeeper(locator, starter);
	}
	
	/**
	 * Метод для тестирования восстановления соединения.
	 * <p>
	 * @param action действие
	 */
	private void checkRestoreConnection(Runnable action) throws Exception {
		Variant<Boolean> vStarted = new Variant<Boolean>()
			.add(true)
			.add(false);
		Variant<Boolean> vConnected = new Variant<Boolean>(vStarted)
			.add(true)
			.add(false);
		Variant<?> iterator = vConnected;
		do {
			setUp();
			expect(terminal.started()).andStubReturn(vStarted.get());
			expect(terminal.connected()).andStubReturn(vConnected.get());
			if ( vStarted.get() && ! vConnected.get() ) {
				starter.start();
			}
			control.replay();
			
			action.run();
			
			control.verify();
		} while ( iterator.next() );
		
		// Test exception on start
		setUp();
		expect(terminal.started()).andStubReturn(true);
		expect(terminal.connected()).andStubReturn(false);
		starter.start();
		expectLastCall().andThrow(new StarterException("Test exception"));
		timer.schedule(new QUIKConnectionKeeperTask(keeper), 5000);
		control.replay();
		
		action.run();
		
		control.verify();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, keeper.getServiceLocator());
		assertSame(starter, keeper.getStarter());
	}
	
	@Test
	public void testStart() throws Exception {
		onStarted.addListener(same(keeper));
		onStopped.addListener(same(keeper));
		onDisconnected.addListener(same(keeper));
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
	public void testOnEvent_OnStarted() throws Exception {
		checkRestoreConnection(new Runnable() {
			@Override public void run() {
				keeper.onEvent(new EventImpl(onStarted));
			}
		});
	}
	
	@Test
	public void testOnEvent_OnStopped_Ok() throws Exception {
		starter.stop();
		control.replay();
		
		keeper.onEvent(new EventImpl(onStopped));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnStopped_StarterException() throws Exception {
		starter.stop();
		expectLastCall().andThrow(new StarterException("Test exception"));
		control.replay();
		
		keeper.onEvent(new EventImpl(onStopped));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnDisconnected() throws Exception {
		checkRestoreConnection(new Runnable() {
			@Override public void run() {
				keeper.onEvent(new EventImpl(onDisconnected));
			}
		});
	}

	@Test
	public void testRestoreConnection() throws Exception {
		checkRestoreConnection(new Runnable() {
			@Override public void run() {
				keeper.restoreConnection();
			}
		});
	}

}
