package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityFactory;
import ru.prolib.aquila.core.utils.Variant;

public class SecuritiesImplTest {
	private static SecurityDescriptor descr1, descr2, descr3;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security1, security2, security3;
	private SecuritiesImpl securities;
	private EventDispatcher dispatcher;
	private EventType onAvailable,onChanged,onTrade;
	private SecurityFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER", "EQBR","RUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM2", "SPBF","USD", SecurityType.FUT);
		descr3 = new SecurityDescriptor("SBER", "RTSS","RUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		dispatcher = control.createMock(EventDispatcher.class);
		security1 = control.createMock(EditableSecurity.class);
		security2 = control.createMock(EditableSecurity.class);
		security3 = control.createMock(EditableSecurity.class);
		factory = control.createMock(SecurityFactory.class);
		onAvailable = control.createMock(EventType.class);
		onChanged = control.createMock(EventType.class);
		onTrade = control.createMock(EventType.class);
		securities = new SecuritiesImpl(dispatcher, onAvailable, onChanged,
				onTrade, factory);
		
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

		assertSame(security1, securities.getEditableSecurity(terminal, descr1));
		assertSame(security2, securities.getEditableSecurity(terminal, descr2));
	}
	
	@Test 
	public void testGetEditableSecurity_CreateIfNotExists() throws Exception {
		EventType onChng = control.createMock(EventType.class),
			onTrd = control.createMock(EventType.class);
		expect(security1.OnChanged()).andStubReturn(onChng);
		expect(security1.OnTrade()).andStubReturn(onTrd);
		
		expect(factory.createInstance(terminal, descr1)).andReturn(security1);
		onChng.addListener(securities);
		onTrd.addListener(securities);
		control.replay();
		
		EditableSecurity actual =
			securities.getEditableSecurity(terminal, descr1);
		
		control.verify();
		assertSame(security1, actual);
		assertSame(security1, securities.getSecurity(descr1));
	}

	@Test
	public void testFireEvents_Available() throws Exception {
		expect(security1.isAvailable()).andReturn(false);
		security1.setAvailable(true);
		dispatcher.dispatch(eq(new SecurityEvent(onAvailable, security1)));
		security1.resetChanges();
		control.replay();
		
		securities.fireEvents(security1);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Changed() throws Exception {
		expect(security1.isAvailable()).andReturn(true);
		security1.fireChangedEvent();
		security1.resetChanges();
		control.replay();
		
		securities.fireEvents(security1);
		
		control.verify();
	}

	@Test
	public void testOnEvent_DispatchSecurityChangedEvent() throws Exception {
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(security1.OnChanged()).andStubReturn(type1);
		expect(security1.OnTrade()).andStubReturn(type2);
		dispatcher.dispatch(eq(new SecurityEvent(onChanged, security1)));
		control.replay();
		
		securities.onEvent(new SecurityEvent(type1, security1));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_DispatchSecurityTradeEvent() throws Exception {
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(security1.OnTrade()).andStubReturn(type1);
		expect(security1.OnChanged()).andStubReturn(type2);
		Trade t = new Trade(terminal);
		dispatcher.dispatch(new SecurityTradeEvent(onTrade, security1, t));
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
		
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vAvl = new Variant<EventType>(vDisp)
			.add(onAvailable)
			.add(control.createMock(EventType.class));
		Variant<EventType> vChng = new Variant<EventType>(vAvl)
			.add(onChanged)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTrd = new Variant<EventType>(vChng)
			.add(onTrade)
			.add(control.createMock(EventType.class));
		Variant<List<EditableSecurity>> vList =
				new Variant<List<EditableSecurity>>(vTrd)
			.add(list1)
			.add(list2);
		Variant<SecurityFactory> vFact = new Variant<SecurityFactory>(vList)
			.add(factory)
			.add(control.createMock(SecurityFactory.class));
		Variant<?> iterator = vFact;
		int foundCnt = 0;
		SecuritiesImpl x = null, found = null;
		control.replay();
		for ( EditableSecurity security : list1 ) {
			securities.setSecurity(security.getDescriptor(), security);
		}
		do {
			x = new SecuritiesImpl(vDisp.get(), vAvl.get(),
					vChng.get(), vTrd.get(), vFact.get());
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
		assertSame(factory, found.getFactory());
	}
	
	@Test
	public void testConstruct_DefaultFactory() throws Exception {
		SecuritiesImpl expected = new SecuritiesImpl(dispatcher, onAvailable,
				onChanged, onTrade, new SecurityFactory());
		assertEquals(expected, new SecuritiesImpl(dispatcher, onAvailable,
				onChanged, onTrade));
	}

}
