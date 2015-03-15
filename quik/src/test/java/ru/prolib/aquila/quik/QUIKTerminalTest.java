package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

public class QUIKTerminalTest {
	private IMocksControl control;
	private EventSystem es;
	private QUIKServiceLocator locator;
	private QUIKTerminal terminal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = new EventSystemImpl();
		locator = new QUIKServiceLocator(control.createMock(QUIKClient.class),
				control.createMock(Cache.class));
		terminal = new QUIKTerminal(es, locator);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(locator.getClient(), terminal.getClient());
		assertSame(locator.getDataCache(), terminal.getDataCache());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(terminal.equals(terminal));
		assertFalse(terminal.equals(null));
		assertFalse(terminal.equals(this));
	}
	
	@Test
	public void testRequestSecurity() throws Exception {
		// Пока ничего не делает.
		control.replay();
		
		terminal.requestSecurity(null);
		
		control.verify();
	}
	
	@Test
	public void testConstruct1_E() throws Exception {
		assertTrue(Check.NOTWIN, Check.isWin());
		terminal = new QUIKTerminal(es);
		assertNotNull(terminal.getEventSystem());
		assertSame(es, terminal.getEventSystem());
		assertNotNull(terminal.getClient());
		assertNotNull(terminal.getDataCache());
	}
	
	@Test
	public void testConstruct1_S() throws Exception {
		assertTrue(Check.NOTWIN, Check.isWin());
		terminal = new QUIKTerminal("foo");
		assertNotNull(terminal.getEventSystem());
		assertEquals("foo", terminal.getEventSystem().getEventQueue().getId());
		assertNotNull(terminal.getClient());
		assertNotNull(terminal.getDataCache());
	}

}
