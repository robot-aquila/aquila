package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-12-26<br>
 * $Id: SetupPositionImplTest.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionTest {
	private static SecurityDescriptor descr1, descr2;
	private SetupPosition setup;
	
	static {
		descr1 = new SecurityDescriptor("GAZP", "AST", "RUB", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM3", "FUT", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		setup = new SetupPosition(descr1);
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(new Price(PriceUnit.PERCENT, 0d), setup.getQuota());
		assertEquals(PositionType.CLOSE, setup.getTarget());
		assertEquals(PositionType.BOTH, setup.getAllowedType());
	}
	
	@Test
	public void testSetQuota() throws Exception {
		setup.setQuota(new Price(PriceUnit.PERCENT, 20.0d));
		assertEquals(new Price(PriceUnit.PERCENT, 20.0d), setup.getQuota());
	}
	
	@Test
	public void testSetTarget() throws Exception {
		setup.setTarget(PositionType.LONG);
		assertEquals(PositionType.LONG, setup.getTarget());
	}
	
	@Test
	public void testSetAllowedType() throws Exception {
		setup.setAllowedType(PositionType.SHORT);
		assertEquals(PositionType.SHORT, setup.getAllowedType());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(setup.equals(setup));
		assertFalse(setup.equals(this));
		assertFalse(setup.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		// initial state
		setup.setQuota(new Price(PriceUnit.MONEY, 200.00d));
		setup.setTarget(PositionType.LONG);
		setup.setAllowedType(PositionType.CLOSE);
		
		Variant<SecurityDescriptor> vSec = new Variant<SecurityDescriptor>()
			.add(descr1)
			.add(descr2);
		Variant<PriceUnit> vUnit = new Variant<PriceUnit>(vSec)
			.add(PriceUnit.MONEY)
			.add(PriceUnit.POINT);
		Variant<Double> vVal = new Variant<Double>(vUnit)
			.add(200.0d)
			.add(1200.00d);
		Variant<PositionType> vType = new Variant<PositionType>(vVal)
			.add(PositionType.LONG)
			.add(PositionType.CLOSE);
		Variant<PositionType> vAlwd = new Variant<PositionType>(vType)
			.add(PositionType.CLOSE)
			.add(PositionType.BOTH);
		Variant<?> iterator = vAlwd;
		int foundCnt = 0;
		SetupPosition x = null, found = null;
		do {
			x = new SetupPosition(vSec.get());
			x.setQuota(new Price(vUnit.get(), vVal.get()));
			x.setTarget(vType.get());
			x.setAllowedType(vAlwd.get());
			if ( setup.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(descr1, found.getSecurityDescriptor());
		assertEquals(new Price(PriceUnit.MONEY, 200.00d), found.getQuota());
		assertEquals(PositionType.LONG, found.getTarget());
		assertEquals(PositionType.CLOSE, found.getAllowedType());
	}
	
	@Test
	public void testHashCode() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(PositionType.LONG);
		setup.setAllowedType(PositionType.SHORT);
		assertEquals(new HashCodeBuilder(20121231, 165329)
			.append(descr1)
			.append(new Price(PriceUnit.POINT, 50d))
			.append(PositionType.LONG)
			.append(PositionType.SHORT) // allowed type
			.toHashCode(), setup.hashCode());
	}
	
	@Test
	public void testClone() throws Exception {
		// initial state
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(PositionType.LONG);
		setup.setAllowedType(PositionType.SHORT);
		
		SetupPosition clone = setup.clone();
		assertNotNull(clone);
		assertNotSame(clone, setup);
		assertEquals(setup, clone);
		assertEquals(new Price(PriceUnit.POINT, 50d), clone.getQuota());
		assertEquals(PositionType.LONG, clone.getTarget());
		assertEquals(PositionType.SHORT, clone.getAllowedType());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(PositionType.LONG);

		SetupPosition setup2 = new SetupPosition(descr1,
				new Price(PriceUnit.POINT, 50d), PositionType.LONG);
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(PositionType.CLOSE);

		SetupPosition setup2 = new SetupPosition(descr1,
				new Price(PriceUnit.POINT, 50d));
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct1Copy() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(PositionType.CLOSE);
		setup.setAllowedType(PositionType.CLOSE);

		SetupPosition setup2 = new SetupPosition(setup);
		assertEquals(setup, setup2);
		assertNotSame(setup, setup2);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetTarget_ThrowsForBoth() throws Exception {
		setup.setTarget(PositionType.BOTH);
	}
	
	@Test
	public void testIsTargetAllowed() throws Exception {
		Object fix[][] = {
				// allowed, target, result?
				{ PositionType.BOTH, PositionType.LONG, true },
				{ PositionType.BOTH, PositionType.SHORT, true },
				{ PositionType.BOTH, PositionType.CLOSE, true },
				{ PositionType.BOTH, PositionType.BOTH, true },
				{ PositionType.LONG, PositionType.LONG, true },
				{ PositionType.LONG, PositionType.SHORT, false },
				{ PositionType.LONG, PositionType.CLOSE, true },
				{ PositionType.LONG, PositionType.BOTH, false },
				{ PositionType.SHORT, PositionType.LONG, false },
				{ PositionType.SHORT, PositionType.SHORT, true },
				{ PositionType.SHORT, PositionType.CLOSE, true },
				{ PositionType.SHORT, PositionType.BOTH, false },
				{ PositionType.CLOSE, PositionType.LONG, false },
				{ PositionType.CLOSE, PositionType.SHORT, false },
				{ PositionType.CLOSE, PositionType.CLOSE, true },
				{ PositionType.CLOSE, PositionType.BOTH, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			setup.setAllowedType((PositionType) fix[i][0]);
			assertEquals(msg, fix[i][2],
					setup.isTargetAllowed((PositionType) fix[i][1]));
		}
	}
	
	@Test
	public void testShouldClose_ForClosed() throws Exception {
		// Для закрытой позиции результат проверки всегда негативный
		Variant<PositionType> vAlwd = new Variant<PositionType>()
			.add(PositionType.BOTH)
			.add(PositionType.LONG)
			.add(PositionType.SHORT)
			.add(PositionType.CLOSE);
		Variant<PositionType> vTgt = new Variant<PositionType>(vAlwd)
			.add(PositionType.LONG)
			.add(PositionType.SHORT)
			.add(PositionType.CLOSE);
		Variant<?> iterator = vTgt;
		do {
			setup.setAllowedType(vAlwd.get());
			setup.setTarget(vTgt.get());
			assertFalse(setup.shouldClose(PositionType.CLOSE));
		} while ( iterator.next() );
	}
	
	@Test
	public void testShouldClose_ForBoth() throws Exception {
		assertFalse(setup.shouldClose(PositionType.BOTH));
	}
	
	@Test
	public void testShouldClose_ForLong() throws Exception {
		Object fix[][] = {
			// allowed, target, close?
			{ PositionType.BOTH, PositionType.CLOSE, true },
			{ PositionType.BOTH, PositionType.LONG, false },
			{ PositionType.BOTH, PositionType.SHORT, false }, // разворот
			{ PositionType.CLOSE, PositionType.CLOSE, true },
			{ PositionType.CLOSE, PositionType.LONG, false },
			{ PositionType.CLOSE, PositionType.SHORT, true },
			{ PositionType.LONG, PositionType.CLOSE, true },
			{ PositionType.LONG, PositionType.LONG, false },
			{ PositionType.LONG, PositionType.SHORT, true },
			{ PositionType.SHORT, PositionType.CLOSE, true },
			{ PositionType.SHORT, PositionType.LONG, false },
			{ PositionType.SHORT, PositionType.SHORT, false }, // разворот
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			setup.setAllowedType((PositionType) fix[i][0]);
			setup.setTarget((PositionType) fix[i][1]);
			assertEquals(msg,fix[i][2],setup.shouldClose(PositionType.LONG));
		}
	}
	
	@Test
	public void testShouldClose_ForShort() throws Exception {
		Object fix[][] = {
			// allowed, target, close?
			{ PositionType.BOTH, PositionType.CLOSE, true },
			{ PositionType.BOTH, PositionType.LONG, false }, // разворот
			{ PositionType.BOTH, PositionType.SHORT, false },
			{ PositionType.CLOSE, PositionType.CLOSE, true },
			{ PositionType.CLOSE, PositionType.LONG, true },
			{ PositionType.CLOSE, PositionType.SHORT, false },
			{ PositionType.LONG, PositionType.CLOSE, true },
			{ PositionType.LONG, PositionType.LONG, false }, // д.б. разворот
			{ PositionType.LONG, PositionType.SHORT, false },
			{ PositionType.SHORT, PositionType.CLOSE, true },
			{ PositionType.SHORT, PositionType.LONG, true },
			{ PositionType.SHORT, PositionType.SHORT, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			setup.setAllowedType((PositionType) fix[i][0]);
			setup.setTarget((PositionType) fix[i][1]);
			assertEquals(msg,fix[i][2],setup.shouldClose(PositionType.SHORT));
		}
	}

	@Test
	public void testShouldOpen_ForAllowedClose() throws Exception {
		setup.setAllowedType(PositionType.CLOSE);
		Variant<PositionType> vTgt = new Variant<PositionType>()
			.add(PositionType.SHORT)
			.add(PositionType.LONG)
			.add(PositionType.CLOSE);
		Variant<PositionType> vCur = new Variant<PositionType>(vTgt)
			.add(PositionType.SHORT)
			.add(PositionType.LONG)
			.add(PositionType.CLOSE);
		Variant<?> iterator = vCur;
		int foundCnt = 0;
		do {
			if ( setup.shouldOpen(vCur.get()) ) {
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(0, foundCnt);
	}
	
	@Test
	public void testShouldOpen() throws Exception {
		PositionType BOTH = PositionType.BOTH,
			LONG = PositionType.LONG,
			SHORT = PositionType.SHORT,
			CLOSE = PositionType.CLOSE;
		Object fix[][] = {
			// allowed, target, current, open?
			{ BOTH, LONG, LONG, false },
			{ BOTH, LONG, SHORT, false },
			{ BOTH, LONG, CLOSE, true },
			{ BOTH, SHORT, LONG, false },
			{ BOTH, SHORT, SHORT, false },
			{ BOTH, SHORT, CLOSE, true },
			{ BOTH, CLOSE, LONG, false },
			{ BOTH, CLOSE, SHORT, false },
			{ BOTH, CLOSE, CLOSE, false }

			
		};
		
		fail("TODO: incomplete");
	}
	
	@Test
	public void testShouldSwap() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void testIsDifferentTarget() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void testIsDifferentQuota() throws Exception {
		fail("TODO: incomplete");
	}

}
