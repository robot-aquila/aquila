package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.PortfolioSetVariationMargin;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

/**
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioSetVariationMarginTest {
	private EditablePortfolio portfolio;
	private PortfolioSetVariationMargin setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new PortfolioSetVariationMargin();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(20),		null,   false },
				{ new Double(10.25d),	10.25d, true  },
				{ null,					null,   false },
				{ new Boolean(false),	null,   false },
				{ this,					null,   false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			portfolio = control.createMock(EditablePortfolio.class);
			if ( (Boolean) fixture[i][2] ) {
				portfolio.setVariationMargin((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(portfolio, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PortfolioSetVariationMargin()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
