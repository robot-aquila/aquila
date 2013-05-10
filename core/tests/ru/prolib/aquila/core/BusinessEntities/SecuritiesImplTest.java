package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;

public class SecuritiesImplTest {
	private static IMocksControl control,control2;
	private static EditableTerminal terminal;
	private static SecurityDescriptor descr1, descr2, descr3;
	private EventSystem es;
	private SecuritiesImpl set;
	private EventDispatcher dispatcher;
	private EventType onAvailable,onChanged,onTrade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER", "EQBR","RUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM2", "SPBF","USD", SecurityType.FUT);
		descr3 = new SecurityDescriptor("SBER", "RTSS","RUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		control2 = createStrictControl();
		es = new EventSystemImpl();
		terminal = control2.createMock(EditableTerminal.class);
		dispatcher = control.createMock(EventDispatcher.class);
		onAvailable = control2.createMock(EventType.class);
		onChanged = control2.createMock(EventType.class);
		onTrade = control2.createMock(EventType.class);
		set = new SecuritiesImpl(dispatcher, onAvailable, onChanged, onTrade);
		expect(terminal.getEventSystem()).andStubReturn(es);
		expect(dispatcher.asString()).andStubReturn("disp");
		expect(onAvailable.asString()).andStubReturn("OnAvailable");
		expect(onChanged.asString()).andStubReturn("OnChanged");
		expect(onTrade.asString()).andStubReturn("OnTrade");
		control2.replay();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(set.equals(set));
		assertFalse(set.equals(null));
		assertFalse(set.equals(this));
	}

	@Test
	public void testGetSecuritiesCount() throws Exception {
		assertEquals(0, set.getSecuritiesCount());
		set.createSecurity(terminal, descr1);
		assertEquals(1, set.getSecuritiesCount());
		set.createSecurity(terminal, descr2);
		assertEquals(2, set.getSecuritiesCount());
		set.createSecurity(terminal, descr3);
		assertEquals(3, set.getSecuritiesCount());
	}
	
	@Test
	public void testGetEditableSecurity() throws Exception {
		EditableSecurity s1 = set.createSecurity(terminal, descr1);
		assertSame(s1, set.getEditableSecurity(descr1));
		EditableSecurity s2 = set.createSecurity(terminal, descr2);
		assertSame(s2, set.getEditableSecurity(descr2));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetEditableSecurity_ThrowsIfNotExists() throws Exception {
		set.getEditableSecurity(descr1);
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		EditableSecurity s1 = set.createSecurity(terminal, descr1);
		EditableSecurity s2 = set.createSecurity(terminal, descr2);
		EditableSecurity s3 = set.createSecurity(terminal, descr3);
		List<Security> expected = new Vector<Security>();
		expected.add(s1);
		expected.add(s2);
		expected.add(s3);

		assertEquals(expected, set.getSecurities());
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		EditableSecurity s2 = set.createSecurity(terminal, descr2);
		EditableSecurity s3 = set.createSecurity(terminal, descr3);
		
		assertSame(s2, set.getSecurity(descr2));
		assertSame(s3, set.getSecurity(descr3));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity_ThrowsIfNotExists() throws Exception {
		set.getSecurity(descr1);
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		set.createSecurity(terminal, descr2);
		set.createSecurity(terminal, descr3);
		
		assertFalse(set.isSecurityExists(descr1));
		assertTrue(set.isSecurityExists(descr2));
		assertTrue(set.isSecurityExists(descr3));
	}
	
	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		Security s = set.createSecurity(terminal, descr1);
		dispatcher.dispatch(eq(new SecurityEvent(onAvailable, s)));
		control.replay();
		
		set.fireSecurityAvailableEvent(s);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_DispatchSecurityChangedEvent() throws Exception {
		Security s = set.createSecurity(terminal, descr1);
		dispatcher.dispatch(eq(new SecurityEvent(onChanged, s)));
		control.replay();
		
		set.onEvent(new SecurityEvent(s.OnChanged(), s));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_DispatchSecurityTradeEvent() throws Exception {
		Security s = set.createSecurity(terminal, descr3);
		Trade trade = new Trade(terminal);
		dispatcher.dispatch(eq(new SecurityTradeEvent(onTrade, s, trade)));
		control.replay();
		
		set.onEvent(new SecurityTradeEvent(s.OnTrade(), s, trade));
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurity() throws Exception {
		es = control.createMock(EventSystem.class);
		terminal = control.createMock(EditableTerminal.class);
		dispatcher = control.createMock(EventDispatcher.class);
		onChanged = control.createMock(EventType.class);
		onTrade = control.createMock(EventType.class);
		expect(terminal.getEventSystem()).andReturn(es);
		expect(es.createEventDispatcher(eq("Security[RIM2@SPBF(FUT/USD)]")))
			.andReturn(dispatcher);
		expect(es.createGenericType(same(dispatcher), eq("OnChanged")))
			.andReturn(onChanged);
		expect(es.createGenericType(same(dispatcher), eq("OnTrade")))
			.andReturn(onTrade);
		control.replay();
		
		EditableSecurity s = set.createSecurity(terminal, descr2);
		
		control.verify();
		assertNotNull(s);
		assertTrue(set.isSecurityExists(descr2));
		assertSame(s, set.getEditableSecurity(descr2));
		assertSame(s, set.getSecurity(descr2));
	}
	
	@Test (expected=SecurityAlreadyExistsException.class)
	public void testCreateSecurity_ThrowsIfExists() throws Exception {
		set.createSecurity(terminal, descr1);
		set.createSecurity(terminal, descr1);
	}

}
