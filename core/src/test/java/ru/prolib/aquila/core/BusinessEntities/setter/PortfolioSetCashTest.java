package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.setter.PortfolioSetCash;

/**
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioSetCashTest {
	private EditablePortfolio portfolio;
	private PortfolioSetCash setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new PortfolioSetCash();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		null,   false },
				{ new Double(20.15d),	20.15d, true  },
				{ null,					null,   false },
				{ new Boolean(false),	null,   false },
				{ this,					null,   false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			portfolio = control.createMock(EditablePortfolio.class);
			if ( (Boolean) fixture[i][2] ) {
				portfolio.setCash((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(portfolio, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PortfolioSetCash()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
