package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class CDValueTripletTest {
	private static final String RUB = "RUB";
	private static final String USD = "USD";
	private CDValueTriplet value, value2;

	@Before
	public void setUp() throws Exception {
		value = new CDValueTriplet();
		value2 = new CDValueTriplet(2, USD);
	}
	
	@Test
	public void testSetters_InitialAndFinal_FDecimal() {
		value.setInitialValue(CDecimalBD.of("210.05"));
		value.setFinalValue(CDecimalBD.of("86.34"));
		
		assertEquals(CDecimalBD.of("210.05"), value.getInitialValue());
		assertEquals(CDecimalBD.of("86.34"), value.getFinalValue());
		assertEquals(CDecimalBD.of("-123.71"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_IniialAndFinal_FixedScale() {
		value2.setInitialValue(CDecimalBD.of("210", USD));
		value2.setFinalValue(CDecimalBD.of("540", USD));
		
		assertEquals(CDecimalBD.of("210.00", USD), value2.getInitialValue());
		assertEquals(CDecimalBD.of("540.00", USD), value2.getFinalValue());
		assertEquals(CDecimalBD.of("330.00", USD), value2.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange_FDecimal() {
		value.setInitialValue(CDecimalBD.of("210.05"));
		value.setChangeValue(CDecimalBD.of("-123.71"));
		
		assertEquals(CDecimalBD.of("210.05"), value.getInitialValue());
		assertEquals(CDecimalBD.of("86.34"), value.getFinalValue());
		assertEquals(CDecimalBD.of("-123.71"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange_FixedScale() {
		value2.setInitialValue(CDecimalBD.of("210", USD));
		value2.setChangeValue(CDecimalBD.of("-123", USD));
		
		assertEquals(CDecimalBD.of( "210.00", USD), value2.getInitialValue());
		assertEquals(CDecimalBD.of(  "87.00", USD), value2.getFinalValue());
		assertEquals(CDecimalBD.of("-123.00", USD), value2.getChangeValue());
	}
	
	@Test
	public void testSetters_ChangeAndFinal_FDecimal() {
		value.setChangeValue(CDecimalBD.of("-123.71"));
		value.setFinalValue(CDecimalBD.of("86.34"));
		
		assertEquals(CDecimalBD.of("210.05"), value.getInitialValue());
		assertEquals(CDecimalBD.of("86.34"), value.getFinalValue());
		assertEquals(CDecimalBD.of("-123.71"), value.getChangeValue());		
	}
	
	@Test
	public void testSetters_ChangeAndFinale_FixedScale() {
		value2.setChangeValue(CDecimalBD.of("-124", USD));
		value2.setFinalValue(CDecimalBD.of("86", USD));
		
		assertEquals(CDecimalBD.of( "210.00", USD), value2.getInitialValue());
		assertEquals(CDecimalBD.of(  "86.00", USD), value2.getFinalValue());
		assertEquals(CDecimalBD.of("-124.00", USD), value2.getChangeValue());
	}

	@Test (expected=IllegalStateException.class)
	public void testSetInitialValue_ThrowsWhenFinalAndChangeAreDefined() {
		value.setFinalValue(CDecimalBD.of("15.28"));
		value.setChangeValue(CDecimalBD.of("10.08"));
		
		value.setInitialValue(CDecimalBD.of("115.62"));
	}

	@Test (expected=IllegalStateException.class)
	public void testSetFinalValue_ThrowsWhenInitialAndChangeAreDefined() {
		value.setInitialValue(CDecimalBD.of("115.92"));
		value.setChangeValue(CDecimalBD.of("-15.01"));
		
		value.setFinalValue(CDecimalBD.of("280.15"));
	}

	@Test (expected=IllegalStateException.class)
	public void testSetChangeValue_ThrowsWhenInitialAndFinalAreDefined() {
		value.setInitialValue(CDecimalBD.of("-350.98"));
		value.setFinalValue(CDecimalBD.of("760.12"));
		
		value.setChangeValue(CDecimalBD.of("11.34"));;
	}

	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setChangeValue(CDecimalBD.of("1.12"));
		
		value.getInitialValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setFinalValue(CDecimalBD.of("390.12"));
		
		value.getInitialValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setChangeValue(CDecimalBD.of("82.34"));
		
		value.getFinalValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setInitialValue(CDecimalBD.of("115.08"));
		
		value.getFinalValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setFinalValue(CDecimalBD.of("12.38"));
		
		value.getChangeValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setInitialValue(CDecimalBD.of("56.12"));
		
		value.getChangeValue();
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_CDecimal_ThrowsIfScaleMismatch() {
		value.setFinalValue(CDecimalBD.of("112.07"));
		
		value.setInitialValue(CDecimalBD.of("345.982"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_CDecimal_ThrowsIfUnitMismatch() {
		value.setFinalValue(CDecimalBD.of("26.07", RUB));
		
		value.setInitialValue(CDecimalBD.of("15.46", USD));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_CDecimal_ThrowsIfScaleMismatch() {
		value.setInitialValue(CDecimalBD.of("112.07"));
		
		value.setFinalValue(CDecimalBD.of("347.289"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_CDecimal_ThrowsIfUnitMismatch() {
		value.setInitialValue(CDecimalBD.of("12.075", RUB));
		
		value.setFinalValue(CDecimalBD.of("3.000", USD));
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_CDecimal_ThrowsIfScaleMismatch() {
		value.setInitialValue(CDecimalBD.of("112.07"));
		
		value.setChangeValue(CDecimalBD.of("115.476"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_CDecimal_ThrowsIfUnitMismatch() {
		value.setInitialValue(CDecimalBD.of("15.02", RUB));
		
		value.setChangeValue(CDecimalBD.of("0.01", USD));
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(value.equals(value));
		assertFalse(value.equals(null));
		assertFalse(value.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(value.equals(new CDValueTriplet()));
		
		value.setInitialValue(CDecimalBD.of("115.26"));
		value.setFinalValue(CDecimalBD.of("120.16"));
		CDValueTriplet x = new CDValueTriplet();
		x.setInitialValue(CDecimalBD.of("115.26"));
		x.setFinalValue(CDecimalBD.of("120.16"));
		assertTrue(value.equals(x));
		
		x = new CDValueTriplet();
		x.setInitialValue(CDecimalBD.of("115.26"));
		x.setChangeValue(CDecimalBD.of("4.95"));
		assertFalse(value.equals(x));
		
		x = new CDValueTriplet();
		x.setChangeValue(CDecimalBD.of("4.95"));
		x.setFinalValue(CDecimalBD.of("120.16"));
		assertFalse(value.equals(x));
	}
	
	@Test
	public void testToString() {
		CDValueTriplet v1 = new CDValueTriplet(),
				v2 = new CDValueTriplet(),
				v3 = new CDValueTriplet(),
				v4 = new CDValueTriplet();
		v1.setInitialValue(CDecimalBD.of("42.97"));
		v1.setFinalValue(CDecimalBD.of("12.15"));
		v2.setInitialValue(CDecimalBD.of("42.975"));
		v2.setChangeValue(CDecimalBD.of("26.032"));
		v3.setChangeValue(CDecimalBD.of("10.1"));
		v3.setFinalValue(CDecimalBD.of("8.5"));
		
		assertEquals("[i=42.97 c=null f=12.15]", v1.toString());
		assertEquals("[i=42.975 c=26.032 f=null]", v2.toString());
		assertEquals("[i=null c=10.1 f=8.5]", v3.toString());
		assertEquals("[i=null c=null f=null]", v4.toString());
	}
	
	@Test
	public void testSetChangeValue_FixedScale_Rescale() {
		value2.setChangeValue(CDecimalBD.of("15", USD));
		
		assertEquals(CDecimalBD.of("15.00", USD), value2.getChangeValue());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_FixedScale_ThrowsIfRoundingNecessary() {
		value2.setChangeValue(CDecimalBD.of("2.9871", USD));
	}
	
	@Test
	public void testSetFinalValue_FixedScale_Rescale() {
		value2.setFinalValue(CDecimalBD.of("2.9", USD));

		assertEquals(CDecimalBD.of("2.90", USD), value2.getFinalValue());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_FixedScale_ThrowsIfRoundingNecessary() {
		value2.setFinalValue(CDecimalBD.of("5.502", USD));
	}
	
	@Test
	public void testSetInitialValue_FixedScale_Rescale() {
		value2.setInitialValue(CDecimalBD.of("500", USD));
		
		assertEquals(CDecimalBD.of("500.00", USD), value2.getInitialValue());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_FixedScale_ThrowsIfRoundingNecessary() {
		value2.setInitialValue(CDecimalBD.of("126.944", USD));
	}
	
	@Test
	public void testSetChangeValue_FixedUnit_OK() {
		value2.setChangeValue(CDecimalBD.of("250", USD));
		
		assertEquals(CDecimalBD.of("250.00", USD), value2.getChangeValue());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_FixedUnit_ThrowsIfUnitMismatch() {
		value2.setChangeValue(CDecimalBD.of("15.02", RUB));
	}

	@Test
	public void testSetFinalValue_FixedUnit_OK() {
		value2.setFinalValue(CDecimalBD.of("15.02", USD));
		
		assertEquals(CDecimalBD.of("15.02", USD), value2.getFinalValue());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_FixedUnit_ThrowsIfUnitMismatch() {
		value2.setFinalValue(CDecimalBD.of("26.19", RUB));
	}
	
	@Test
	public void testSetInitialValue_FixedUnit_OK() {
		value2.setInitialValue(CDecimalBD.of("115.24", USD));
		
		assertEquals(CDecimalBD.of("115.24", USD), value2.getInitialValue());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_FixedUnit_ThrowsIfUnitMismatch() {
		value2.setFinalValue(CDecimalBD.of("15.02", RUB));
	}

}
