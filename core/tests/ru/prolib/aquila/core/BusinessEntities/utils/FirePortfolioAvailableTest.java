package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.utils.FirePortfolioAvailable;

/**
 * 2012-11-29<br>
 * $Id: FireEventPortfolioAvailableTest.java 326 2012-11-29 17:34:31Z whirlwind $
 */
public class FirePortfolioAvailableTest {
	private static IMocksControl control;
	private static EditablePortfolios portfolios;
	private static EditablePortfolio portfolio;
	private static FirePortfolioAvailable fepa;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		portfolios = control.createMock(EditablePortfolios.class);
		portfolio = control.createMock(EditablePortfolio.class);
		fepa = new FirePortfolioAvailable(portfolios);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testFireEvent() throws Exception {
		portfolios.firePortfolioAvailableEvent(same(portfolio));
		control.replay();
		fepa.fireEvent(portfolio);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(fepa.equals(fepa));
		assertFalse(fepa.equals(null));
		assertFalse(fepa.equals(this));
		assertTrue(fepa.equals(new FirePortfolioAvailable(portfolios)));
		assertFalse(fepa.equals(new FirePortfolioAvailable(
				control.createMock(EditablePortfolios.class))));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121129, 164831)
			.append(portfolios)
			.toHashCode();
		assertEquals(hashCode, fepa.hashCode());
	}

}
