package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-01-23<br>
 * $Id: QUIKSecurityDescriptorsImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class SecurityDescriptorRegistryTest {
	private static SecurityDescriptor descr1,descr2,descr3;
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private SecurityDescriptorRegistry registry;
	
	/**
	 * Вспомогательный класс для тестирования сравнения реестров.
	 */
	private static class TestFirePanicEvent implements FirePanicEvent {
		@Override
		public void firePanicEvent(int code, String msgId) { }
		@Override
		public void firePanicEvent(int code, String msgId, Object[] args) { }
		/**
		 * Сравнение с другим экземпляров всегда будет давать true.
		 * Но это не должно влиять на сравнение реестра, которое рассматривает
		 * конкретные экземпляры генераторов, а не их эквивалентность.
		 */
		@Override
		public boolean equals(Object other) {
			return other != null && other.getClass()==TestFirePanicEvent.class;
		}
	}
	
	/**
	 * Вспомогательный класс для представления записи кэша.
	 */
	private static class FR {
		private final SecurityDescriptor descr;
		private final String name;
		
		private FR(SecurityDescriptor descr, String name) {
			super();
			this.descr = descr;
			this.name = name;
		}
		
	}


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		descr1 = new SecurityDescriptor("LKOH","RTSST", "RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("LKOH","EQBR",  "RUB",SecurityType.STK);
		descr3 = new SecurityDescriptor("RIM2","SPBFUT","USD",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(FirePanicEvent.class);
		registry = new SecurityDescriptorRegistry(firePanic);
		registry.registerSecurityDescriptor(descr1, "LKOH");
		registry.registerSecurityDescriptor(descr2, "ЛУКОЙЛ");
		registry.registerSecurityDescriptor(descr3, "RIM2");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, registry.getFirePanicEvent());
	}
	
	@Test
	public void testGetSecurityDescriptorByName() throws Exception {
		assertSame(descr1, registry.getSecurityDescriptorByName("LKOH"));
		assertSame(descr2, registry.getSecurityDescriptorByName("ЛУКОЙЛ"));
		assertSame(descr3, registry.getSecurityDescriptorByName("RIM2"));
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered1() throws Exception {
		assertTrue(registry.isSecurityDescriptorRegistered("LKOH"));
		assertTrue(registry.isSecurityDescriptorRegistered("ЛУКОЙЛ"));
		assertTrue(registry.isSecurityDescriptorRegistered("RIM2"));
		assertFalse(registry.isSecurityDescriptorRegistered("Газпром АО"));
	}
	
	@Test
	public void testGetSecurityDescriptorByCodeAndClass() throws Exception {
		assertSame(descr1,
				registry.getSecurityDescriptorByCodeAndClass("LKOH", "RTSST"));
		assertSame(descr2,
				registry.getSecurityDescriptorByCodeAndClass("LKOH", "EQBR"));
		assertSame(descr3,
				registry.getSecurityDescriptorByCodeAndClass("RIM2", "SPBFUT"));
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered2() throws Exception {
		assertTrue(registry.isSecurityDescriptorRegistered("LKOH", "RTSST"));
		assertTrue(registry.isSecurityDescriptorRegistered("LKOH", "EQBR"));
		assertTrue(registry.isSecurityDescriptorRegistered("RIM2", "SPBFUT"));
		assertFalse(registry.isSecurityDescriptorRegistered("LKOH", "UNKNOWN"));
		assertFalse(registry.isSecurityDescriptorRegistered("GAZP", "EQBR"));
	}
	
	@Test
	public void testGetSecurityDescriptorByCodeAndClass_IfNotExists()
		throws Exception
	{
		firePanic.firePanicEvent(eq(1),
				eq("NULL security descriptor by code & class: {}"),
				aryEq(new Object[] { "LKOH@SMART" }));
		control.replay();

		assertNull(registry.getSecurityDescriptorByCodeAndClass("LKOH", "SMART"));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByName_IfNotExists()
		throws Exception
	{
		firePanic.firePanicEvent(eq(1),
				eq("NULL security descriptor by name: {}"),
				aryEq(new Object[] { "Газпром-ао" }));
		control.replay();
		
		assertNull(registry.getSecurityDescriptorByName("Газпром-ао"));

		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(registry.equals(registry));
		assertFalse(registry.equals(null));
		assertFalse(registry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		firePanic = new TestFirePanicEvent();
		registry = new SecurityDescriptorRegistry(firePanic);
		registry.registerSecurityDescriptor(descr1, "LKOH");
		registry.registerSecurityDescriptor(descr2, "ЛУКОЙЛ");
		registry.registerSecurityDescriptor(descr3, "RIM2");
		
		List<FR> rows1 = new Vector<FR>();
		rows1.add(new FR(descr1, "LKOH"));
		rows1.add(new FR(descr2, "ЛУКОЙЛ"));
		rows1.add(new FR(descr3, "RIM2"));
		List<FR> rows2 = new Vector<FR>();
		rows2.add(new FR(descr3, "RIM2"));
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(new TestFirePanicEvent());
		Variant<List<FR>> vRows = new Variant<List<FR>>(vFire)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		SecurityDescriptorRegistry x = null, found = null;
		do {
			x = new SecurityDescriptorRegistry(vFire.get());
			for ( FR fix : vRows.get() ) {
				x.registerSecurityDescriptor(fix.descr, fix.name);
			}
			if ( registry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(descr1,
				found.getSecurityDescriptorByCodeAndClass("LKOH", "RTSST"));
		assertSame(descr2,
				found.getSecurityDescriptorByCodeAndClass("LKOH", "EQBR"));
		assertSame(descr3,
				found.getSecurityDescriptorByCodeAndClass("RIM2", "SPBFUT"));
	}

}
