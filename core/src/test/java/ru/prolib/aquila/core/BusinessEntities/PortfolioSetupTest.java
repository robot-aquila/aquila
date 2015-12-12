package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;

public class PortfolioSetupTest {
	private static Symbol symbol1, symbol2, symbol3;
	private PortfolioSetup setup;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("SBER", "EQBR", "RUB",SymbolType.STK);
		symbol2 = new Symbol("AAPL", "ARCA", "USD",SymbolType.STK);
		symbol3 = new Symbol("RIM2", "SPB", "USD",SymbolType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		setup = new PortfolioSetup();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		PositionSetup	sp1 = setup.getPosition(symbol1),
						sp2 = setup.getPosition(symbol2);
		assertEquals(new PositionSetup(), sp1);
		assertEquals(new PositionSetup(), sp2);
		assertNotSame(sp1, sp2);
		assertSame(sp1, setup.getPosition(symbol1));
		assertSame(sp2, setup.getPosition(symbol2));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testRemovePosition() throws Exception {
		PositionSetup sp1 = setup.getPosition(symbol1);
		sp1.setQuota(new Price(PriceUnit.PERCENT, 120.0d));
		sp1.setTarget(PositionType.SHORT);
		setup.removePosition(symbol1);
		
		assertEquals(new LinkedHashMap(), setup.getPositions());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testRemoveAll() throws Exception {
		setup.getPosition(symbol1);
		setup.getPosition(symbol2);
		setup.getPosition(symbol3);
		setup.removeAll();
		
		assertEquals(new LinkedHashMap(), setup.getPositions());
	}
	
	@Test
	public void testGetPositions() throws Exception {
		PositionSetup	sp1 = setup.getPosition(symbol1),
						sp2 = setup.getPosition(symbol2),
						sp3 = setup.getPosition(symbol3);
		Map<Symbol, PositionSetup> expected =
			new LinkedHashMap<Symbol, PositionSetup>();
		expected.put(symbol1, sp1);
		expected.put(symbol2, sp2);
		expected.put(symbol3, sp3);
		assertEquals(expected, setup.getPositions());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(setup.equals(setup));
		assertFalse(setup.equals(null));
		assertFalse(setup.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		setup.getPosition(symbol1);
		setup.getPosition(symbol2);
		
		PortfolioSetup setup2 = new PortfolioSetup();
		setup2.getPosition(symbol1);
		setup2.getPosition(symbol2);
		
		PortfolioSetup setup3 = new PortfolioSetup();
		setup3.getPosition(symbol1);
		setup3.getPosition(symbol2);
		setup3.getPosition(symbol3);

		PortfolioSetup setup4 = new PortfolioSetup();
		setup4.getPosition(symbol2);
		setup4.getPosition(symbol3);
		
		PortfolioSetup setup5 = new PortfolioSetup();
		setup5.getPosition(symbol1);

		assertTrue(setup.equals(setup2));
		assertFalse(setup.equals(setup3));
		assertFalse(setup.equals(setup4));
		assertFalse(setup.equals(setup5));
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		setup.getPosition(symbol2);
		setup.getPosition(symbol1);
		setup.getPosition(symbol3);
		
		List<Symbol> expected = new Vector<Symbol>();
		expected.add(symbol2);
		expected.add(symbol1);
		expected.add(symbol3);
		
		assertEquals(expected, setup.getSecurities());
	}

}
