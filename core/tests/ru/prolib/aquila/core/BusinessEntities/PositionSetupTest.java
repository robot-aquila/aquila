package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-12-26<br>
 * $Id: SetupPositionImplTest.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class PositionSetupTest {
	private static PositionType BOTH = PositionType.BOTH;
	private static PositionType LONG = PositionType.LONG;
	private static PositionType SHORT = PositionType.SHORT;
	private static PositionType CLOSE = PositionType.CLOSE;
	private static SecurityDescriptor descr1, descr2;
	private static Variant<PositionType> vAlw, vTgt, vCur, iterator;
	private PositionSetup setup;
	
	static {
		descr1 = new SecurityDescriptor("GAZP", "AST", "RUB", SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM3", "FUT", "USD", SecurityType.FUT);
		vAlw = new Variant<PositionType>()
			.add(BOTH)
			.add(CLOSE)
			.add(LONG)
			.add(SHORT);
		vTgt = new Variant<PositionType>(vAlw)
			.add(CLOSE)
			.add(LONG)
			.add(SHORT);
		vCur = new Variant<PositionType>(vTgt)
			.add(CLOSE)
			.add(LONG)
			.add(SHORT);
	}

	@Before
	public void setUp() throws Exception {
		setup = new PositionSetup(descr1);
		iterator = vCur;
		iterator.rewind();
	}
	
	/**
	 * Комбинация параметров разрешенного {@link #vAlw}, целевого {@link #vTgt}
	 * и текущего {@link #vCur} типов позиций.
	 */
	private static class Combo {
		private final PositionType allowed, target, current;
		
		public Combo(PositionType allowed, PositionType target,
				PositionType current)
		{
			super();
			this.allowed = allowed;
			this.target = target;
			this.current = current;
		}
		
		public Combo(PositionType allowed, PositionType target) {
			this(allowed, target, null);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null ) {
				return false;
			}
			if ( other.getClass() != Combo.class ) {
				return false;
			}
			Combo o = (Combo) other;
			return new EqualsBuilder()
				.append(allowed, o.allowed)
				.append(target, o.target)
				.append(current, o.current)
				.isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
				.append(allowed)
				.append(target)
				.append(current)
				.toHashCode();
		}
		
		@Override
		public String toString() {
			return "Combo[allowed=" + allowed
				+ ",target=" + target
				+ (current != null ? " current=" + current : "")
				+ "]";
		}
		
	}
	
	/**
	 * Формирует текущую комбинации параметров разрешенного {@link #vAlw},
	 * целевого {@link #vTgt} и текущего {@link #vCur} типов позиций.
	 * <p>
	 * @return текущая комбинация
	 */
	private Combo getCurrentCombo() {
		return new Combo(vAlw.get(), vTgt.get(), vCur.get());
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(new Price(PriceUnit.PERCENT, 0d), setup.getQuota());
		assertEquals(CLOSE, setup.getTarget());
		assertEquals(BOTH, setup.getAllowedType());
	}
	
	@Test
	public void testSetQuota() throws Exception {
		setup.setQuota(new Price(PriceUnit.PERCENT, 20.0d));
		assertEquals(new Price(PriceUnit.PERCENT, 20.0d), setup.getQuota());
	}
	
	@Test
	public void testSetTarget() throws Exception {
		setup.setTarget(LONG);
		assertEquals(LONG, setup.getTarget());
	}
	
	@Test
	public void testSetAllowedType() throws Exception {
		setup.setAllowedType(SHORT);
		assertEquals(SHORT, setup.getAllowedType());
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
		setup.setTarget(LONG);
		setup.setAllowedType(CLOSE);
		
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
			.add(LONG)
			.add(CLOSE);
		Variant<PositionType> vAlwd = new Variant<PositionType>(vType)
			.add(CLOSE)
			.add(BOTH);
		Variant<?> iterator = vAlwd;
		int foundCnt = 0;
		PositionSetup x = null, found = null;
		do {
			x = new PositionSetup(vSec.get());
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
		assertEquals(LONG, found.getTarget());
		assertEquals(CLOSE, found.getAllowedType());
	}
	
	@Test
	public void testHashCode() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(LONG);
		setup.setAllowedType(SHORT);
		assertEquals(new HashCodeBuilder(20121231, 165329)
			.append(descr1)
			.append(new Price(PriceUnit.POINT, 50d))
			.append(LONG)
			.append(SHORT) // allowed type
			.toHashCode(), setup.hashCode());
	}
	
	@Test
	public void testClone() throws Exception {
		// initial state
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(LONG);
		setup.setAllowedType(SHORT);
		
		PositionSetup clone = setup.clone();
		assertNotNull(clone);
		assertNotSame(clone, setup);
		assertEquals(setup, clone);
		assertEquals(new Price(PriceUnit.POINT, 50d), clone.getQuota());
		assertEquals(LONG, clone.getTarget());
		assertEquals(SHORT, clone.getAllowedType());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(LONG);

		PositionSetup setup2 = new PositionSetup(descr1,
				new Price(PriceUnit.POINT, 50d), LONG);
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(CLOSE);

		PositionSetup setup2 = new PositionSetup(descr1,
				new Price(PriceUnit.POINT, 50d));
		assertEquals(setup, setup2);
	}
	
	@Test
	public void testConstruct1Copy() throws Exception {
		setup.setQuota(new Price(PriceUnit.POINT, 50d));
		setup.setTarget(CLOSE);
		setup.setAllowedType(CLOSE);

		PositionSetup setup2 = new PositionSetup(setup);
		assertEquals(setup, setup2);
		assertNotSame(setup, setup2);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetTarget_ThrowsForBoth() throws Exception {
		setup.setTarget(BOTH);
	}
	
	@Test
	public void testIsTargetAllowed() throws Exception {
		Set<Combo> expected = new HashSet<Combo>();
		expected.add(new Combo(BOTH, LONG));
		expected.add(new Combo(BOTH, SHORT));
		expected.add(new Combo(BOTH, CLOSE));
		expected.add(new Combo(LONG, LONG));
		expected.add(new Combo(LONG, CLOSE));
		expected.add(new Combo(SHORT, SHORT));
		expected.add(new Combo(SHORT, CLOSE));
		expected.add(new Combo(CLOSE, CLOSE));
		Set<Combo> actual = new HashSet<Combo>();
		iterator = vTgt;
		do {
			Combo combo = new Combo(vAlw.get(), vTgt.get());
			String msg = combo.toString();
			setup.setAllowedType(combo.allowed);
			setup.setTarget(combo.target);
			if ( expected.contains(combo) ) {
				assertTrue(msg, setup.isTargetAllowed(combo.target));
				actual.add(combo);
			} else {
				assertFalse(msg, setup.isTargetAllowed(combo.target));
			}
		} while ( iterator.next() );
		assertEquals(expected, actual);
	}
	
	@Test
	public void testShouldClose() throws Exception {
		Set<Combo> expected = new HashSet<Combo>();
		// always true for target CLOSE
		expected.add(new Combo(BOTH, CLOSE, LONG));
		expected.add(new Combo(BOTH, CLOSE, SHORT));
		expected.add(new Combo(LONG, CLOSE, LONG));
		expected.add(new Combo(LONG, CLOSE, SHORT));
		expected.add(new Combo(SHORT, CLOSE, LONG));
		expected.add(new Combo(SHORT, CLOSE, SHORT));
		// true for swap if restricted
		expected.add(new Combo(SHORT, LONG, SHORT));
		expected.add(new Combo(LONG, SHORT, LONG));
		// true for restricted
		expected.add(new Combo(CLOSE, SHORT, LONG));
		expected.add(new Combo(CLOSE, CLOSE, LONG));
		expected.add(new Combo(CLOSE, LONG, SHORT));
		expected.add(new Combo(CLOSE, CLOSE, SHORT));
		Set<Combo> actual = new HashSet<Combo>();
		do {
			Combo combo = getCurrentCombo();
			String msg = combo.toString();
			setup.setAllowedType(combo.allowed);
			setup.setTarget(combo.target);
			if ( expected.contains(combo) ) {
				assertTrue(msg, setup.shouldClose(combo.current));
				actual.add(combo);
			} else {
				assertFalse(msg, setup.shouldClose(combo.current));
			}
		} while ( iterator.next() );
		assertEquals(expected, actual);
	}

	@Test
	public void testShouldOpen() throws Exception {
		Set<Combo> expected = new HashSet<Combo>();
		expected.add(new Combo(BOTH, LONG, CLOSE));
		expected.add(new Combo(BOTH, SHORT, CLOSE));
		expected.add(new Combo(LONG, LONG, CLOSE));
		expected.add(new Combo(SHORT, SHORT, CLOSE));
		Set<Combo> actual = new HashSet<Combo>();
		do {
			Combo combo = getCurrentCombo();
			String msg = combo.toString();
			setup.setAllowedType(combo.allowed);
			setup.setTarget(combo.target);
			// always false for zero quota
			setup.setQuota(new Price(PriceUnit.PERCENT, 0d));
			assertFalse(setup.shouldOpen(combo.current));
			setup.setQuota(new Price(PriceUnit.PERCENT, 1d));
			if ( expected.contains(combo) ) {
				assertTrue(msg, setup.shouldOpen(combo.current));
				actual.add(combo);
			} else {
				assertFalse(msg, setup.shouldOpen(combo.current));
			}
		} while ( iterator.next() );
		assertEquals(expected, actual);
	}
	
	@Test
	public void testShouldSwap() throws Exception {
		Set<Combo> expected = new HashSet<Combo>();
		expected.add(new Combo(BOTH, LONG, SHORT));
		expected.add(new Combo(BOTH, SHORT, LONG));
		expected.add(new Combo(LONG, LONG, SHORT ));
		expected.add(new Combo(SHORT, SHORT, LONG));
		Set<Combo> actual = new HashSet<Combo>();
		do {
			Combo combo = getCurrentCombo();
			String msg = combo.toString();
			setup.setAllowedType(combo.allowed);
			setup.setTarget(combo.target);
			setup.setQuota(new Price(PriceUnit.PERCENT, 0d));
			assertFalse(msg, setup.shouldSwap(combo.current));
			setup.setQuota(new Price(PriceUnit.PERCENT, 1d));
			if ( expected.contains(combo) ) {
				assertTrue(msg, setup.shouldSwap(combo.current));
				actual.add(combo);
			} else {
				assertFalse(msg, setup.shouldSwap(combo.current));
			}
		} while ( iterator.next() );
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIsDifferentTarget() throws Exception {
		// initial target
		setup.setTarget(LONG);
		
		do {
			Combo combo = getCurrentCombo();
			String msg = combo.toString();
			PositionSetup other = new PositionSetup(descr2);
			other.setTarget(combo.target);
			if ( combo.target == LONG ) {
				assertFalse(msg, setup.isDifferentTarget(other));
			} else {
				assertTrue(msg, setup.isDifferentTarget(other));
			}
		} while ( iterator.next() );
	}
	
	@Test
	public void testIsDifferentQuota() throws Exception {
		setup.setQuota(new Price(PriceUnit.PERCENT, 80d));
		PositionSetup other = new PositionSetup(descr2);
		
		assertTrue(setup.isDifferentQuota(other));
		other.setQuota(new Price(PriceUnit.PERCENT, 80d));
		assertFalse(setup.isDifferentQuota(other));
	}

}
