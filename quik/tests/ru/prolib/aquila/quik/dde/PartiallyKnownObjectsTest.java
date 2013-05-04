package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class PartiallyKnownObjectsTest {
	private static Account acc1, acc2;
	private static SecurityDescriptor descr1, descr2;
	private IMocksControl control;
	private AccountRegistry accounts;
	private SecurityDescriptorRegistry descriptors;
	private PartiallyKnownObjects facade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		acc1 = new Account("BCS", "eqe0001");
		acc2 = new Account("FINAM", "3466", "LX01-F0");
		descr1 = new SecurityDescriptor("RIM3","SPBFUT","USD",SecurityType.FUT);
		descr2 = new SecurityDescriptor("GAZP","EQBR", "RUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		accounts = control.createMock(AccountRegistry.class);
		descriptors = control.createMock(SecurityDescriptorRegistry.class);
		facade = new PartiallyKnownObjects(accounts, descriptors);
	}
	
	@Test
	public void testGetAccount1() throws Exception {
		expect(accounts.getAccount(eq("eqe0001"))).andReturn(acc1);
		control.replay();
		
		assertSame(acc1, facade.getAccount("eqe0001"));
		
		control.verify();;
	}
	
	@Test
	public void testGetAccount2() throws Exception {
		expect(accounts.getAccount(eq("3466"), eq("LX01-F0"))).andReturn(acc2);
		control.replay();
		
		assertSame(acc2, facade.getAccount("3466", "LX01-F0"));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByCodeAndClass() throws Exception {
		expect(descriptors
				.getSecurityDescriptorByCodeAndClass(eq("GAZP"), eq("EQBR")))
			.andReturn(descr2);
		control.replay();
		
		assertSame(descr2,
				facade.getSecurityDescriptorByCodeAndClass("GAZP", "EQBR"));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByName() throws Exception {
		expect(descriptors.getSecurityDescriptorByName(eq("РТС")))
			.andReturn(descr1);
		control.replay();
		
		assertSame(descr1, facade.getSecurityDescriptorByName("РТС"));
		
		control.verify();
	}
	
	@Test
	public void testIsAccountRegistered1() throws Exception {
		expect(accounts.isAccountRegistered(eq("foo"))).andReturn(true);
		expect(accounts.isAccountRegistered(eq("bar"))).andReturn(false);
		control.replay();
		
		assertTrue(facade.isAccountRegistered("foo"));
		assertFalse(facade.isAccountRegistered("bar"));
		
		control.verify();
	}
	
	@Test
	public void testIsAccountRegistered2() throws Exception {
		expect(accounts.isAccountRegistered(eq("A"), eq("B"))).andReturn(true);
		expect(accounts.isAccountRegistered(eq("B"), eq("C"))).andReturn(false);
		control.replay();
		
		assertTrue(facade.isAccountRegistered("A", "B"));
		assertFalse(facade.isAccountRegistered("B", "C"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered1() throws Exception {
		expect(descriptors.isSecurityDescriptorRegistered(eq("Лукойл")))
			.andReturn(true);
		expect(descriptors.isSecurityDescriptorRegistered(eq("Сбер")))
			.andReturn(false);
		control.replay();
		
		assertTrue(facade.isSecurityDescriptorRegistered("Лукойл"));
		assertFalse(facade.isSecurityDescriptorRegistered("Сбер"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered2() throws Exception {
		expect(descriptors
				.isSecurityDescriptorRegistered(eq("RIM3"), eq("SPBFUT")))
			.andReturn(true);
		expect(descriptors
				.isSecurityDescriptorRegistered(eq("SBER"), eq("EQBR")))
			.andReturn(false);
		control.replay();
		
		assertTrue(facade.isSecurityDescriptorRegistered("RIM3", "SPBFUT"));
		assertFalse(facade.isSecurityDescriptorRegistered("SBER", "EQBR"));
		
		control.verify();
	}
	
	@Test
	public void testRegisterAccount() throws Exception {
		accounts.registerAccount(same(acc1));
		control.replay();
		
		facade.registerAccount(acc1);
		
		control.verify();
	}

	@Test
	public void testRegisterSecurityDescriptor() throws Exception {
		descriptors.registerSecurityDescriptor(same(descr1), eq("JUBBA"));
		control.replay();
		
		facade.registerSecurityDescriptor(descr1, "JUBBA");
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(facade.equals(facade));
		assertFalse(facade.equals(null));
		assertFalse(facade.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<AccountRegistry> vAccs = new Variant<AccountRegistry>()
			.add(accounts)
			.add(control.createMock(AccountRegistry.class));
		Variant<SecurityDescriptorRegistry> vDescrs =
				new Variant<SecurityDescriptorRegistry>(vAccs)
			.add(descriptors)
			.add(control.createMock(SecurityDescriptorRegistry.class));
		Variant<?> iterator = vDescrs;
		int foundCnt = 0;
		PartiallyKnownObjects x= null, found = null;
		do {
			x = new PartiallyKnownObjects(vAccs.get(), vDescrs.get());
			if ( facade.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(accounts, found.getAccountRegistry());
		assertSame(descriptors, found.getSecurityDescriptorRegistry());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		FirePanicEvent firePanic = control.createMock(FirePanicEvent.class);
		PartiallyKnownObjects expected = new PartiallyKnownObjects(
				new AccountRegistry(firePanic),
				new SecurityDescriptorRegistry(firePanic));
		facade = new PartiallyKnownObjects(firePanic);
		assertEquals(expected, facade);
	}

}
