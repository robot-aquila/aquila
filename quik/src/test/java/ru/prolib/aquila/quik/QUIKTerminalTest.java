package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.BasicTerminalParams;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

public class QUIKTerminalTest {
	private IMocksControl control;
	private BasicTerminalParams params;
	private OrderProcessor orderProcessor;
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
		params = new BasicTerminalBuilder().withOrderProcessor(orderProcessor)
				.buildParams();
		locator = new QUIKServiceLocator(control.createMock(QUIKClient.class),
				control.createMock(Cache.class));
		terminal = new QUIKTerminal(params, locator);
	}
	
	@Test
	public void testCtor2() throws Exception {
		assertSame(params.getController(), terminal.getTerminalController());
		assertSame(params.getEventDispatcher(), terminal.getTerminalEventDispatcher());
		assertSame(orderProcessor, terminal.getOrderProcessor());
		assertSame(params.getOrderStorage(), terminal.getOrderStorage());
		assertSame(params.getPortfolioStorage(), terminal.getPortfolioStorage());
		assertSame(params.getScheduler(), terminal.getScheduler());
		assertSame(params.getSecurityStorage(), terminal.getSecurityStorage());
		assertSame(params.getStarter(), terminal.getStarter());
		assertSame(params.getEventSystem(), terminal.getEventSystem());
		assertSame(locator, terminal.getServiceLocator());
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

}
