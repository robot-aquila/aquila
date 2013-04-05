package ru.prolib.aquila.quik.subsys.portfolio;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-20<br>
 * $Id$
 */
public class QUIKAccountsTest {
	private static Account acc1, acc2, acc3, acc4;
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private QUIKAccounts accounts;
	
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
		accounts = new QUIKAccounts(firePanic);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, accounts.getFirePanicEvent());
	}
	
	@Test
	public void testGetAccount_NoAccount() throws Exception {
		firePanic.firePanicEvent(eq(1),
				eq("Accounts: NULL account for clientId & code: {}"),
				aryEq(new Object[] { "FOO@BAR" } ));
		control.replay();
		
		assertNull(accounts.getAccount("FOO", "BAR"));
		
		control.verify();
	}
	
	@Test
	public void testGetAccount_Ok() throws Exception {
		accounts.register(acc1);
		accounts.register(acc4);
		control.replay();
		
		assertSame(acc1, accounts.getAccount("001eqe", "001eqe"));
		assertSame(acc4, accounts.getAccount("R5555", "LX002"));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(accounts.equals(accounts));
		assertFalse(accounts.equals(null));
		assertFalse(accounts.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		accounts.register(acc1);
		accounts.register(acc2);
		accounts.register(acc3);
		Account actualAccSet[] = { acc1, acc2, acc3 };
		Account otherAccSet[] = { acc1, acc4 };
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(control.createMock(FirePanicEvent.class));
		Variant<Account[]> vAccs = new Variant<Account[]>(vFire)
			.add(actualAccSet)
			.add(otherAccSet);
		Variant<?> iterator = vAccs;
		int foundCnt = 0;
		QUIKAccounts x = null, found = null;
		do {
			x = new QUIKAccounts(vFire.get());
			Account accs[] = vAccs.get();
			for ( int i = 0; i < accs.length; i ++ ) {
				x.register(accs[i]);
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
