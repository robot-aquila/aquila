package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.FirePositionAvailableAuto;

/**
 * 2013-01-06<br>
 * $Id: FireEventPositionAvailableAutoTest.java 397 2013-01-06 15:29:12Z whirlwind $
 */
public class FirePositionAvailableAutoTest {
	private static IMocksControl control;
	private static EditablePortfolios portfolios;
	private static EditablePortfolio portfolio;
	private static EditablePosition position;
	private static FirePositionAvailableAuto fire;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		portfolios = control.createMock(EditablePortfolios.class);
		portfolio = control.createMock(EditablePortfolio.class);
		position = control.createMock(EditablePosition.class);
		fire = new FirePositionAvailableAuto(portfolios);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(portfolios, fire.getPortfolios());
	}
	
	@Test
	public void testFireEvent() throws Exception {
		expect(position.getAccount()).andReturn(new Account("ZULU4"));
		expect(portfolios.getEditablePortfolio(eq(new Account("ZULU4"))))
			.andReturn(portfolio);
		portfolio.firePositionAvailableEvent(same(position));
		control.replay();
		fire.fireEvent(position);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(fire.equals(fire));
		assertTrue(fire.equals(new FirePositionAvailableAuto(portfolios)));
		assertFalse(fire.equals(control.createMock(EditablePortfolios.class)));
		assertFalse(fire.equals(this));
		assertFalse(fire.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 192739)
			.append(portfolios)
			.toHashCode(), fire.hashCode());
	}

}
