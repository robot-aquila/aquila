package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-02-20<br>
 * $Id$
 */
public class ValidatePortfolioRowTest {
	private IMocksControl control;
	private RowSet rs;
	private QUIKServiceLocator locator;
	private Cache ddeCache;
	private ValidatePortfolioRow validator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rs = control.createMock(RowSet.class);
		locator = control.createMock(QUIKServiceLocator.class);
		ddeCache = control.createMock(Cache.class);
		validator = new ValidatePortfolioRow(locator);
		expect(locator.getDdeCache()).andStubReturn(ddeCache);
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
		ddeCache.registerAccount(same(account));
		control.replay();
		
		assertTrue(validator.validate(rs));
		
		control.verify();
	}
	
	@Test
	public void testValidate_ThrowsIfRowGetThrows() throws Exception {
		RowException expected = new RowException("test");
		expect(rs.get(eq("PORT_ACC"))).andThrow(expected);
		control.replay();
		
		try {
			validator.validate(rs);
			fail("Expected: " + ValidatorException.class.getSimpleName());
		} catch ( ValidatorException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
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
