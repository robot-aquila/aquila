package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class SecuritiesImplTest {
	private static EventSystem es;
	private static SecurityDescriptor descr1, descr2, descr3;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security1, security2, security3;
	private SecuritiesImpl securities;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onAvailable,onChanged,onTrade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER", "EQBR","RUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM2", "SPBF","USD", SecurityType.FUT);
		descr3 = new SecurityDescriptor("SBER", "RTSS","RUR", SecurityType.STK);
		es = new EventSystemImpl();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		dispatcherMock = control.createMock(EventDispatcher.class);
		security1 = control.createMock(EditableSecurity.class);
		security2 = control.createMock(EditableSecurity.class);
		security3 = control.createMock(EditableSecurity.class);
		dispatcher = es.createEventDispatcher("Securities");
		onAvailable = dispatcher.createType("OnAvailable");
		onChanged = dispatcher.createType("OnChanged");
		onTrade = dispatcher.createType("OnTrade");
		securities = new SecuritiesImpl(dispatcher, onAvailable, onChanged,
				onTrade);
		expect(terminal.getEventSystem()).andStubReturn(es);
		expect(security1.getDescriptor()).andStubReturn(descr1);
		expect(security2.getDescriptor()).andStubReturn(descr2);
		expect(security3.getDescriptor()).andStubReturn(descr3);
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		securities.setSecurity(descr1, security1);
		securities.setSecurity(descr2, security2);
		securities.setSecurity(descr3, security3);
		
		List<Security> expected = new Vector<Security>();
		expected.add(security1);
		expected.add(security2);
		expected.add(security3);

		assertEquals(expected, securities.getSecurities());
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		securities.setSecurity(descr3, security3);
		securities.setSecurity(descr2, security2);
		
		assertSame(security2, securities.getSecurity(descr2));
		assertSame(security3, securities.getSecurity(descr3));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity_ThrowsIfNotExists() throws Exception {
		securities.getSecurity(descr1);
	}

	@Test
	public void testIsSecurityExists() throws Exception {
		securities.setSecurity(descr2, security2);
		securities.setSecurity(descr3, security3);
		
		assertFalse(securities.isSecurityExists(descr1));
		assertTrue(securities.isSecurityExists(descr2));
		assertTrue(securities.isSecurityExists(descr3));
	}

	@Test
	public void testGetEditableSecurity() throws Exception {
		securities.setSecurity(descr1, security1);
		securities.setSecurity(descr2, security2);

		assertSame(security1, securities.getEditableSecurity(descr1));
		assertSame(security2, securities.getEditableSecurity(descr2));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetEditableSecurity_ThrowsIfNotExists() throws Exception {
		securities.getEditableSecurity(descr1);
	}

	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		securities = new SecuritiesImpl(dispatcherMock, onAvailable, onChanged,
				onTrade);
		dispatcherMock.dispatch(eq(new SecurityEvent(onAvailable, security1)));
		control.replay();
		
		securities.fireSecurityAvailableEvent(security1);
		
		control.verify();
	}

	@Test
	public void testOnEvent_DispatchSecurityChangedEvent() throws Exception {
		securities = new SecuritiesImpl(dispatcherMock, onAvailable, onChanged,
				onTrade);
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(security1.OnChanged()).andStubReturn(type1);
		expect(security1.OnTrade()).andStubReturn(type2);
		dispatcherMock.dispatch(eq(new SecurityEvent(onChanged, security1)));
		control.replay();
		
		securities.onEvent(new SecurityEvent(type1, security1));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_DispatchSecurityTradeEvent() throws Exception {
		securities = new SecuritiesImpl(dispatcherMock, onAvailable, onChanged,
				onTrade);
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(security1.OnTrade()).andStubReturn(type1);
		expect(security1.OnChanged()).andStubReturn(type2);
		Trade t = new Trade(terminal);
		dispatcherMock.dispatch(new SecurityTradeEvent(onTrade, security1, t));
		control.replay();
		
		securities.onEvent(new SecurityTradeEvent(type1, security1, t));
		
		control.verify();
	}

	@Test
	public void testGetSecuritiesCount() throws Exception {
		assertEquals(0, securities.getSecuritiesCount());
		securities.setSecurity(descr1, security1);
		assertEquals(1, securities.getSecuritiesCount());
		securities.setSecurity(descr2, security2);
		assertEquals(2, securities.getSecuritiesCount());
		securities.setSecurity(descr3, security3);
		assertEquals(3, securities.getSecuritiesCount());
	}
	
	@Test (expected=SecurityAlreadyExistsException.class)
	public void testCreateSecurity_ThrowsIfExists() throws Exception {
		securities.setSecurity(descr1, security1);
		securities.createSecurity(terminal, descr1);
	}
	
	@Test
	public void testCreateSecurity() throws Exception {
		String id = "Security[RIM2@SPBF(FUT/USD)]";
		EventDispatcher d = es.createEventDispatcher(id);
		Security expected = new SecurityImpl(terminal, descr2, d,
				d.createType("OnChanged"), d.createType("OnTrade"));
		expected.OnChanged().addListener(securities);
		expected.OnTrade().addListener(securities);
		control.replay();
		
		EditableSecurity actual = securities.createSecurity(terminal, descr2);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
		assertTrue(securities.isSecurityExists(descr2));
		assertSame(actual, securities.getEditableSecurity(descr2));
		assertSame(actual, securities.getSecurity(descr2));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(securities.equals(securities));
		assertFalse(securities.equals(null));
		assertFalse(securities.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<EditableSecurity> list1 = new Vector<EditableSecurity>();
		list1.add(security1);
		list1.add(security2);
		List<EditableSecurity> list2 = new Vector<EditableSecurity>();
		list2.add(security3);
		list2.add(security2);
		list2.add(security1);
		
		Variant<String> vDispId = new Variant<String>()
			.add("Securities")
			.add("AnotherId");
		Variant<String> vAvlId = new Variant<String>(vDispId)
			.add("OnAvailable")
			.add("OnUnknown");
		Variant<String> vChngId = new Variant<String>(vAvlId)
			.add("OnChanged")
			.add("OnBubbled");
		Variant<String> vTrdId = new Variant<String>(vChngId)
			.add("OnTrade")
			.add("OnBiggle");
		Variant<List<EditableSecurity>> vList =
				new Variant<List<EditableSecurity>>(vTrdId)
			.add(list1)
			.add(list2);
		Variant<?> iterator = vList;
		int foundCnt = 0;
		SecuritiesImpl x = null, found = null;
		control.replay();
		for ( EditableSecurity security : list1 ) {
			securities.setSecurity(security.getDescriptor(), security);
		}
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new SecuritiesImpl(d, d.createType(vAvlId.get()),
					d.createType(vChngId.get()), d.createType(vTrdId.get()));
			for ( EditableSecurity security : vList.get() ) {
				x.setSecurity(security.getDescriptor(), security);
			}
			if ( securities.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onAvailable, found.OnSecurityAvailable());
		assertEquals(onChanged, found.OnSecurityChanged());
		assertEquals(onTrade, found.OnSecurityTrade());
		assertEquals(list1, found.getSecurities());
	}

}
