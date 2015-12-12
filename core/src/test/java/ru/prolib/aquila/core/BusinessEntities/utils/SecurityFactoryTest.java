package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecurityFactoryTest {
	private static Symbol symbol;
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private SecurityFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "RUB", SymbolType.STK);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = new EventSystemImpl();
		terminal = control.createMock(EditableTerminal.class);
		factory = new SecurityFactory();
		expect(terminal.getEventSystem()).andStubReturn(es);
	}
	
	@Test
	public void testCreateInstance() throws Exception {
		control.replay();
		SecurityEventDispatcher d = new SecurityEventDispatcher(es, symbol);
		Security expected = new SecurityImpl(terminal, symbol, d);
		
		SecurityImpl actual = (SecurityImpl)
			factory.createInstance(terminal, symbol);
		assertEquals(expected, actual);
		assertNotNull(actual.getEventDispatcher());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new SecurityFactory()));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}
