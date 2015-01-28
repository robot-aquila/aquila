package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

public class SecuritiesTest {
	private static SecurityDescriptor descr1, descr2, descr3;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security1, security2, security3;
	private Securities securities;
	private SecuritiesEventDispatcher dispatcher;
	private SecurityFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER", "EQBR","RUB", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM2", "SPBF","USD", SecurityType.FUT);
		descr3 = new SecurityDescriptor("SBER", "RTSS","RUB", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		dispatcher = control.createMock(SecuritiesEventDispatcher.class);
		security1 = control.createMock(EditableSecurity.class);
		security2 = control.createMock(EditableSecurity.class);
		security3 = control.createMock(EditableSecurity.class);
		factory = control.createMock(SecurityFactory.class);
		securities = new Securities(dispatcher, factory);
		
		expect(security1.getDescriptor()).andStubReturn(descr1);
		expect(security2.getDescriptor()).andStubReturn(descr2);
		expect(security3.getDescriptor()).andStubReturn(descr3);
	}
	
	@Test
	public void testEventTypes() throws Exception {
		SecuritiesEventDispatcher d =
			new SecuritiesEventDispatcher(new EventSystemImpl());
		securities = new Securities(d);
		
		assertSame(d.OnAvailable(), securities.OnSecurityAvailable());
		assertSame(d.OnChanged(), securities.OnSecurityChanged());
		assertSame(d.OnTrade(), securities.OnSecurityTrade());
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
		expect(factory.createInstance(terminal, descr1)).andReturn(security1);
		dispatcher.startRelayFor(same(security1));
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
		dispatcher.fireAvailable(same(security1));
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
	public void testConstruct_DefaultFactory() throws Exception {
		Securities actual = new Securities(dispatcher);
		assertEquals(SecurityFactory.class, actual.getFactory().getClass());
	}

}