package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

/**
 * 2013-01-11<br>
 * $Id: SetupPositionsImplTest.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionsImplTest {
	private static SecurityDescriptor descr1,descr2,descr3;
	private SetupPositionsImpl setup;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER","EQBR","RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("AAPL","ARCA","USD",SecurityType.STK);
		descr3 = new SecurityDescriptor("RIM2","SPBFUT","USD",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		setup = new SetupPositionsImpl();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		SetupPosition	sp1 = setup.getPosition(descr1),
						sp2 = setup.getPosition(descr2);
		assertEquals(new SetupPositionImpl(descr1), sp1);
		assertEquals(new SetupPositionImpl(descr2), sp2);
		assertNotSame(sp1, sp2);
		assertSame(sp1, setup.getPosition(descr1));
		assertSame(sp2, setup.getPosition(descr2));
	}
	
	@Test
	public void testRemovePosition() throws Exception {
		SetupPosition sp1 = setup.getPosition(descr1);
		sp1.setQuota(new Price(PriceUnit.PERCENT, 120.0d));
		sp1.setType(PositionType.SHORT);
		setup.removePosition(descr1);
		assertEquals(new SetupPositionImpl(descr1), setup.getPosition(descr1));
	}
	
	@Test
	public void testGetPositions() throws Exception {
		SetupPosition	sp1 = setup.getPosition(descr1),
						sp2 = setup.getPosition(descr2),
						sp3 = setup.getPosition(descr3);
		Vector<SetupPosition> expected = new Vector<SetupPosition>();
		expected.add(sp1);
		expected.add(sp2);
		expected.add(sp3);
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
		setup.getPosition(descr1);
		setup.getPosition(descr2);
		
		SetupPositions setup2 = new SetupPositionsImpl();
		setup2.getPosition(descr1);
		setup2.getPosition(descr2);
		
		SetupPositions setup3 = new SetupPositionsImpl();
		setup3.getPosition(descr1);
		setup3.getPosition(descr2);
		setup3.getPosition(descr3);

		SetupPositions setup4 = new SetupPositionsImpl();
		setup4.getPosition(descr2);
		setup4.getPosition(descr3);
		
		SetupPositions setup5 = new SetupPositionsImpl();
		setup5.getPosition(descr1);

		assertTrue(setup.equals(setup2));
		assertFalse(setup.equals(setup3));
		assertFalse(setup.equals(setup4));
		assertFalse(setup.equals(setup5));
	}

	@Test
	public void testClone() throws Exception {
		setup.getPosition(descr1).setQuota(new Price(PriceUnit.PERCENT, 10.0d));
		setup.getPosition(descr2).setType(PositionType.SHORT);
		
		SetupPositions clone = setup.clone();
		assertEquals(setup, clone);
		List<SetupPosition> list = clone.getPositions();
		for ( int i = 0; i < list.size(); i ++ ) {
			SetupPosition c = list.get(i);
			SetupPosition o = setup.getPosition(c.getSecurityDescriptor());
			assertEquals(o, c);
			assertNotSame(o, c); 
		}
	}

}
