package ru.prolib.aquila.ui.plugin;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.SecuritiesTableCols;
import ru.prolib.aquila.ui.ServiceLocator;
import ru.prolib.aquila.ui.MessageRegistry;

/**
 * $Id$
 */
public class UISecuritiesPluginTest {

	private static IMocksControl control;
	private Terminal terminal;
	private MessageRegistry texts;
	private ServiceLocator locator;
	
	private UISecuritiesPlugin plugin;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		terminal = control.createMock(Terminal.class);
		texts = control.createMock(MessageRegistry.class);
		locator = new ServiceLocator(texts, control.createMock(Runnable.class));
		
		plugin = new UISecuritiesPlugin();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructor() {
		IsInstanceOf.instanceOf(SecuritiesTableCols.class).matches(plugin.getTableCols());
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.plugin.UISecuritiesPlugin#initialize(ru.prolib.aquila.ui.ServiceLocator, ru.prolib.aquila.core.BusinessEntities.Terminal)}.
	 */
	@Test
	public void testInitialize() {
		plugin.initialize(locator, terminal, null);
		assertEquals(terminal, plugin.getTerminal());
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.plugin.UISecuritiesPlugin#createUI(ru.prolib.aquila.ui.AquilaUI)}.
	 */
	@Test
	@Ignore
	public void testCreateUI() {
		fail("Not yet implemented"); // TODO
	}

}
