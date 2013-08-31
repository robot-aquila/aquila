package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PortfolioFactoryTest {
	private static Account account;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EventSystem es;
	private PortfolioFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("TEST", "1", "2");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		es = new EventSystemImpl();
		factory = new PortfolioFactory();
		expect(terminal.getEventSystem()).andReturn(es);
	}
	
	@Test
	public void testCreateInstance() throws Exception {
		control.replay();
		
		PortfolioImpl expected = new PortfolioImpl(terminal, account,
					new PortfolioEventDispatcher(es, account));
		expected.setPositionsInstance(new PositionsImpl(expected,
				new PositionsEventDispatcher(es, account)));
		
		assertEquals(expected, factory.createInstance(terminal, account));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new PortfolioFactory()));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}
