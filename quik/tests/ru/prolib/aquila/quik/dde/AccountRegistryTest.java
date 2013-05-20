package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.AccountRegistry;

/**
 * 2013-02-20<br>
 * $Id$
 */
public class AccountRegistryTest {
	private static Account acc1, acc2, acc3, acc4;
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private AccountRegistry accounts;
	
	/**
	 * Вспомогательный класс для тестирования сравнения реестров.
	 */
	static class TestFirePanicEvent implements FirePanicEvent {
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		acc1 = new Account("SPBFUT", "001eqe");
		acc2 = new Account("SPBFUT", "001jmk");
		acc3 = new Account("FIRM01", "R2345", "LX001");
		acc4 = new Account("FIRM01", "R5555", "LX002");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(FirePanicEvent.class);
		accounts = new AccountRegistry(firePanic);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, accounts.getFirePanicEvent());
	}
	
	@Test
	public void testGetAccount2_NoAccount() throws Exception {
		firePanic.firePanicEvent(eq(1),
				eq("Accounts: NULL account for clientId & code: {}"),
				aryEq(new Object[] { "FOO@BAR" } ));
		control.replay();
		
		assertNull(accounts.getAccount("FOO", "BAR"));
		
		control.verify();
	}
	
	@Test
	public void testGetAccount1_NoAccount() throws Exception {
		accounts.registerAccount(acc4);
		firePanic.firePanicEvent(eq(1),
				eq("Accounts: NULL account for clientId & code: {}"),
				aryEq(new Object[] { "LX002@LX002" } ));
		control.replay();
		
		assertNull(accounts.getAccount("LX002"));
		
		control.verify();
	}
	
	@Test
	public void testGetAccount() throws Exception {
		accounts.registerAccount(acc1);
		accounts.registerAccount(acc4);
		control.replay();
		
		assertSame(acc1, accounts.getAccount("001eqe", "001eqe"));
		assertSame(acc1, accounts.getAccount("001eqe"));
		assertSame(acc4, accounts.getAccount("R5555", "LX002"));
		
		control.verify();
	}
	
	@Test
	public void testIsAccountRegistered2() throws Exception {
		accounts.registerAccount(acc1);
		accounts.registerAccount(acc4);
		
		assertTrue(accounts.isAccountRegistered("R5555", "LX002"));
		assertFalse(accounts.isAccountRegistered("R2345", "LX001"));
		assertTrue(accounts.isAccountRegistered("001eqe", "001eqe"));
		assertFalse(accounts.isAccountRegistered("001jmk", "001jmk"));
	}
	
	@Test
	public void testIsAccountRegistered1() throws Exception {
		accounts.registerAccount(acc1);
		accounts.registerAccount(acc4);
		
		assertFalse(accounts.isAccountRegistered("LX002"));
		assertTrue(accounts.isAccountRegistered("001eqe"));
		assertFalse(accounts.isAccountRegistered("001jmk"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(accounts.equals(accounts));
		assertFalse(accounts.equals(null));
		assertFalse(accounts.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		firePanic = new TestFirePanicEvent();
		accounts = new AccountRegistry(firePanic);
		accounts.registerAccount(acc1);
		accounts.registerAccount(acc2);
		accounts.registerAccount(acc3);
		
		Account actualAccSet[] = { acc1, acc2, acc3 };
		Account otherAccSet[] = { acc1, acc4 };
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(new TestFirePanicEvent());
		Variant<Account[]> vAccs = new Variant<Account[]>(vFire)
			.add(actualAccSet)
			.add(otherAccSet);
		Variant<?> iterator = vAccs;
		int foundCnt = 0;
		AccountRegistry x = null, found = null;
		do {
			x = new AccountRegistry(vFire.get());
			Account accs[] = vAccs.get();
			for ( int i = 0; i < accs.length; i ++ ) {
				x.registerAccount(accs[i]);
			}
			if ( accounts.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(acc1, found.getAccount("001eqe", "001eqe"));
		assertSame(acc2, found.getAccount("001jmk", "001jmk"));
		assertSame(acc3, found.getAccount("R2345", "LX001"));
	}

}
