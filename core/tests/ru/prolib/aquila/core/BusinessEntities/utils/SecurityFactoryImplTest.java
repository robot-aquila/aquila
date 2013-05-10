package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * 2012-07-05<br>
 * $Id: SecurityFactoryImplTest.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class SecurityFactoryImplTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private SecurityFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("SPY", "SMART", "USD", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		terminal = control.createMock(EditableTerminal.class);
		factory = new SecurityFactoryImpl();
		expect(terminal.getEventSystem()).andStubReturn(es);
	}
	
	@Test
	public void testCreateSecurity() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType etNewTrade = control.createMock(EventType.class);
		EventType etChanged = control.createMock(EventType.class);
		expect(es.createEventDispatcher("Security[" + descr + "]"))
			.andReturn(dispatcher);
		expect(es.createGenericType(dispatcher, "OnChanged"))
			.andReturn(etChanged);
		expect(es.createGenericType(dispatcher, "OnTrade"))
			.andReturn(etNewTrade);
		control.replay();
		

		SecurityImpl sec = (SecurityImpl) factory.createSecurity(terminal, descr);
		assertNotNull(sec);
		
		control.verify();
		assertEquals("SPY", sec.getCode());
		assertEquals("SMART", sec.getClassCode());
		assertEquals(descr, sec.getDescriptor());
		assertSame(terminal, sec.getTerminal());
		assertSame(dispatcher, sec.getEventDispatcher());
		assertSame(etChanged, sec.OnChanged());
		assertSame(etNewTrade, sec.OnTrade());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}
