package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class AccountTradeSelectorTest {
	private static Account account;
	private IMocksControl control;
	private Order order;
	private AccountTradeSelector selector;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("TEST");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		order = control.createMock(Order.class);
		selector = new AccountTradeSelector(account);
	}
	
	@Test
	public void testMustBeAdded() throws Exception {
		expect(order.getAccount()).andReturn(new Account("BEST"));
		expect(order.getAccount()).andReturn(new Account("TEST"));
		control.replay();
		
		assertFalse(selector.mustBeAdded(null, order));
		assertTrue(selector.mustBeAdded(null, order));
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		AccountTradeSelector s1 = new AccountTradeSelector(new Account("TEST")),
			s2 = new AccountTradeSelector(new Account("BEST"));
		assertTrue(selector.equals(selector));
		assertTrue(selector.equals(s1));
		assertFalse(selector.equals(s2));
		assertFalse(selector.equals(null));
		assertFalse(selector.equals(this));
	}

}
