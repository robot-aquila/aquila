package ru.prolib.aquila.quik.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-01-23<br>
 * $Id: QUIKSecurityDescriptorsImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class QUIKSecurityDescriptorsImplTest {
	private static SecurityDescriptor descr1,descr2,descr3;
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private EditableTerminal terminal;
	private QUIKSecurityDescriptorsImpl descrs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("LKOH","RTSST","RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("LKOH","EQBR","RUB",SecurityType.STK);
		descr3 = new SecurityDescriptor("RIM2","SPBFUT","USD",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		terminal = control.createMock(EditableTerminal.class);
		descrs = new QUIKSecurityDescriptorsImpl(locator);
		descrs.register(descr1, "LKOH");
		descrs.register(descr2, "ЛУКОЙЛ");
		descrs.register(descr3, "RIM2");
		expect(locator.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, descrs.getServiceLocator());
	}
	
	@Test
	public void testMain() throws Exception {
		assertSame(descr1, descrs.getByCodeAndClass("LKOH", "RTSST"));
		assertSame(descr1, descrs.getByName("LKOH"));
		assertSame(descr2, descrs.getByCodeAndClass("LKOH", "EQBR"));
		assertSame(descr2, descrs.getByName("ЛУКОЙЛ"));
		assertSame(descr3, descrs.getByCodeAndClass("RIM2", "SPBFUT"));
		assertSame(descr3, descrs.getByName("RIM2"));
	}
	
	@Test
	public void testGetByCodeAndClass_IfNotExists() throws Exception {
		terminal.firePanicEvent(eq(1),
				eq("NULL security descriptor by code & class: {}"),
				aryEq(new Object[] { "LKOH@SMART" }));
		control.replay();

		assertNull(descrs.getByCodeAndClass("LKOH", "SMART"));
		
		control.verify();
	}
	
	@Test
	public void testGetByName_ThrowsIfNotExists() throws Exception {
		terminal.firePanicEvent(eq(1),
				eq("NULL security descriptor by name: {}"),
				aryEq(new Object[] { "Газпром-ао" }));
		control.replay();
		
		assertNull(descrs.getByName("Газпром-ао"));

		control.verify();
	}

}
