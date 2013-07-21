package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecurityFactoryTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private SecurityFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
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
		String id = "Security[SBER@EQBR(STK/SUR)]";
		EventDispatcher d = es.createEventDispatcher(id);
		Security expected = new SecurityImpl(terminal, descr, d,
				d.createType("OnChanged"), d.createType("OnTrade"));
		
		assertEquals(expected, factory.createInstance(terminal, descr));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new SecurityFactory()));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}
