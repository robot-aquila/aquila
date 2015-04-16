package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

public class QUIKTerminalTest {
	private IMocksControl control;
	private EditableTerminal underlyingTerminal; 
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
		underlyingTerminal = control.createMock(EditableTerminal.class);
		locator = new QUIKServiceLocator(control.createMock(QUIKClient.class),
				control.createMock(Cache.class));
		terminal = new QUIKTerminal(underlyingTerminal, locator);
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
	public void testConstruct2() throws Exception {
		assertSame(underlyingTerminal, terminal.getTerminal());
		assertSame(locator, terminal.getServiceLocator());
	}

}
