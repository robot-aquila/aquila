package ru.prolib.aquila.core.BusinessEntities.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-18<br>
 * $Id$
 */
public class GAccountExistsTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private G<Account> gAccount;
	private GAccountExists getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		gAccount = control.createMock(G.class);
		getter = new GAccountExists(terminal, gAccount, "PFX: ");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, getter.getTerminal());
		assertSame(gAccount, getter.getAccountGetter());
		assertEquals("PFX: ", getter.getMessagePrefix());
	}
	
	@Test
	public void testGet_IfExists() throws Exception {
		Account account = new Account("LX001");
		expect(gAccount.get(same(this))).andReturn(account);
		expect(terminal.isPortfolioAvailable(same(account))).andReturn(true);
		control.replay();
		
		assertSame(account, getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_IfNotExists() throws Exception {
		Account account = new Account("LX001");
		expect(gAccount.get(same(this))).andReturn(account);
		expect(terminal.isPortfolioAvailable(same(account))).andReturn(false);
		terminal.firePanicEvent(eq(1),
				eq("PFX: Portfolio not exists: {}"),
				aryEq(new Object[] { account }));
		control.replay();
		
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_IfNull() throws Exception {
		expect(gAccount.get(same(this))).andReturn(null);
		control.replay();
		
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<G<Account>> vGtr = new Variant<G<Account>>(vTerm)
			.add(gAccount)
			.add(control.createMock(G.class));
		Variant<String> vPfx = new Variant<String>(vGtr)
			.add("test")
			.add("PFX: ");
		Variant<?> iterator = vPfx;
		int foundCnt = 0;
		GAccountExists x = null, found = null;
		do {
			x = new GAccountExists(vTerm.get(), vGtr.get(), vPfx.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(gAccount, found.getAccountGetter());
		assertEquals("PFX: ", found.getMessagePrefix());
	}

	@Test
	public void testToString() throws Exception {
		String expected = "GAccountExists[value="
			+ gAccount + ", msgPfx='PFX: ']";
		assertEquals(expected, getter.toString());
	}
}
