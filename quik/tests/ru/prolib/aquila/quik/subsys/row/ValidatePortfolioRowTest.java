package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.quik.subsys.portfolio.QUIKAccounts;

/**
 * 2013-02-20<br>
 * $Id$
 */
public class ValidatePortfolioRowTest {
	private IMocksControl control;
	private RowSet rs;
	private QUIKServiceLocator locator;
	private QUIKAccounts accounts;
	private ValidatePortfolioRow validator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rs = control.createMock(RowSet.class);
		locator = control.createMock(QUIKServiceLocator.class);
		accounts = control.createMock(QUIKAccounts.class);
		validator = new ValidatePortfolioRow(locator);
		expect(locator.getAccounts()).andStubReturn(accounts);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, validator.getServiceLocator());
	}
	
	@Test
	public void testValidate_IfAccountIsNull() throws Exception {
		expect(rs.get(eq("PORT_ACC"))).andReturn(null);
		control.replay();
		
		assertTrue(validator.validate(rs));
		
		control.verify();
	}
	
	@Test
	public void testValidate_IfAccountIsNotNull() throws Exception {
		Account account = new Account("foo", "bar");
		expect(rs.get(eq("PORT_ACC"))).andReturn(account);
		accounts.register(same(account));
		control.replay();
		
		assertTrue(validator.validate(rs));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<?> iterator = vLoc;
		int foundCnt = 0;
		ValidatePortfolioRow x = null, found = null;
		do {
			x = new ValidatePortfolioRow(vLoc.get());
			if ( validator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
	}

}
