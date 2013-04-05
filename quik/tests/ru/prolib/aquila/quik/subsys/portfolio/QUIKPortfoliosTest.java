package ru.prolib.aquila.quik.subsys.portfolio;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * 2013-01-24<br>
 * $Id: QUIKPortfoliosTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class QUIKPortfoliosTest {
	private static IMocksControl control;
	private static EditablePortfolios basePortfolios;
	private static EventType type;
	private static EditablePortfolio portfolio;
	private static QUIKPortfolios quikPortfolios;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		basePortfolios = control.createMock(EditablePortfolios.class);
		type = control.createMock(EventType.class);
		portfolio = control.createMock(EditablePortfolio.class);
		quikPortfolios = new QUIKPortfolios(basePortfolios);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(basePortfolios, quikPortfolios.getPortfoliosInstance());
	}
	
	@Test
	public void testOnPortfolioAvailable() throws Exception {
		expect(basePortfolios.OnPortfolioAvailable()).andReturn(type);
		control.replay();
		assertSame(type, quikPortfolios.OnPortfolioAvailable());
		control.verify();
	}
	
	@Test
	public void testOnPortfolioChanged() throws Exception {
		expect(basePortfolios.OnPortfolioChanged()).andReturn(type);
		control.replay();
		assertSame(type, quikPortfolios.OnPortfolioChanged());
		control.verify();
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		expect(basePortfolios.OnPositionAvailable()).andReturn(type);
		control.replay();
		assertSame(type, quikPortfolios.OnPositionAvailable());
		control.verify();
	}
	
	@Test
	public void testOnPositionChanged() throws Exception {
		expect(basePortfolios.OnPositionChanged()).andReturn(type);
		control.replay();
		assertSame(type, quikPortfolios.OnPositionChanged());
		control.verify();
	}

	@Test
	public void testGetDefaultPortfolio() throws Exception {
		expect(basePortfolios.getDefaultPortfolio()).andReturn(portfolio);
		control.replay();
		assertSame(portfolio, quikPortfolios.getDefaultPortfolio());
		control.verify();
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		expect(basePortfolios.getPortfolio(eq(new Account("125", "ABC"))))
				.andReturn(portfolio);
		control.replay();
		Portfolio actual =
				quikPortfolios.getPortfolio(new Account("125", "ABC", "Z")); 
		control.verify();
		assertSame(portfolio, actual);
	}
	
	@Test
	public void testGetPortfolios() throws Exception {
		List<Portfolio> list = new LinkedList<Portfolio>();
		expect(basePortfolios.getPortfolios()).andReturn(list);
		control.replay();
		assertSame(list, quikPortfolios.getPortfolios());
		control.verify();
	}
	
	@Test
	public void testGetPortfoliosCount() throws Exception {
		expect(basePortfolios.getPortfoliosCount()).andReturn(724);
		control.replay();
		assertEquals(724, quikPortfolios.getPortfoliosCount());
		control.verify();
	}
	
	@Test
	public void testIsPortfolioAvailable() throws Exception {
		expect(basePortfolios
				.isPortfolioAvailable(eq(new Account("125", "ABC"))))
				.andReturn(true);
		control.replay();
		assertTrue(quikPortfolios
				.isPortfolioAvailable(new Account("125", "ABC", "Z")));
		control.verify();
	}
	
	@Test
	public void testFirePortfolioAvailableEvent() throws Exception {
		basePortfolios.firePortfolioAvailableEvent(same(portfolio));
		control.replay();
		quikPortfolios.firePortfolioAvailableEvent(portfolio);
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		expect(basePortfolios
				.getEditablePortfolio(eq(new Account("125", "ABC"))))
				.andReturn(portfolio);
		control.replay();
		assertSame(portfolio, quikPortfolios
				.getEditablePortfolio(new Account("125", "ABC", "Z")));
		control.verify();
	}
	
	@Test
	public void testRegisterPortfolio() throws Exception {
		basePortfolios.registerPortfolio(same(portfolio));
		control.replay();
		quikPortfolios.registerPortfolio(portfolio);
		control.verify();
	}
	
	@Test
	public void testSetDefaultPortfolio() throws Exception {
		basePortfolios.setDefaultPortfolio(portfolio);
		control.replay();
		quikPortfolios.setDefaultPortfolio(portfolio);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		EditablePortfolios pts2 = control.createMock(EditablePortfolios.class);
		assertTrue(quikPortfolios.equals(quikPortfolios));
		assertTrue(quikPortfolios.equals(new QUIKPortfolios(basePortfolios)));
		assertFalse(quikPortfolios.equals(null));
		assertFalse(quikPortfolios.equals(this));
		assertFalse(quikPortfolios.equals(new QUIKPortfolios(pts2)));
	}
	
}
