package ru.prolib.aquila.ib.subsys.account;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.ib.subsys.account.IBIsPortfolioAvailable;

/**
 * 2012-12-30<br>
 * $Id: IBIsPortfolioAvailableTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBIsPortfolioAvailableTest {
	private static IMocksControl control;
	private static Portfolio portfolio;
	private static IBIsPortfolioAvailable validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		portfolio = control.createMock(Portfolio.class);
		validator = new IBIsPortfolioAvailable();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertTrue(validator.equals(new IBIsPortfolioAvailable()));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121231, 175427)
			.append(IBIsPortfolioAvailable.class)
			.toHashCode(), validator.hashCode());
	}
	
	@Test
	public void testValidate_SpecialCases() throws Exception {
		assertFalse(validator.validate(null));
		assertFalse(validator.validate(this));
	}
	
	@Test
	public void testValidate() throws Exception {
		Object fix[][] = {
				// cash, balance, expected
				{ 12.34d, 56.78d, true  },
				{ null,   56.78d, false },
				{ 12.34d, null,   false },
				{ null,   null,   false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(portfolio.getCash()).andStubReturn((Double) fix[i][0]);
			expect(portfolio.getBalance()).andStubReturn((Double) fix[i][1]);
			control.replay();
			assertEquals((Boolean) fix[i][2], validator.validate(portfolio));
			control.verify();
		}
	}

}
