package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.PortfolioSetBalance;

/**
 * 2012-12-28<br>
 * $Id: SetPortfolioBalanceTest.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class PortfolioSetBalanceTest {
	private EditablePortfolio portfolio;
	private PortfolioSetBalance setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new PortfolioSetBalance();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		null,   false },
				{ new Double(21.15d),	21.15d, true  },
				{ null,					null,   false },
				{ new Boolean(false),	null,   false },
				{ this,					null,   false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			portfolio = control.createMock(EditablePortfolio.class);
			if ( (Boolean) fixture[i][2] ) {
				portfolio.setBalance((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(portfolio, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PortfolioSetBalance()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
