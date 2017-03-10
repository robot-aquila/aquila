package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;

public class FDValueTripletTest {
	private FDValueTriplet value;

	@Before
	public void setUp() throws Exception {
		value = new FDValueTriplet();
	}
	
	@Test
	public void testSetters_InitialAndFinal_FDecimal() {
		value.setInitialValue(new FDecimal("210.05"));
		value.setFinalValue(new FDecimal("86.34"));
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange_FDecimal() {
		value.setInitialValue(new FDecimal("210.05"));
		value.setChangeValue(new FDecimal("-123.71"));
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_ChangeAndFinal_FDecimal() {
		value.setChangeValue(new FDecimal("-123.71"));
		value.setFinalValue(new FDecimal("86.34"));
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());		
	}

	@Test
	public void testSetters_InitialAndFinal_String() {
		value.setInitialValue("210.05");
		value.setFinalValue("86.34");
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange_String() {
		value.setInitialValue("210.05");
		value.setChangeValue("-123.71");
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());
	}

	@Test
	public void testSetters_ChangeAndFinal_String() {
		value.setChangeValue("-123.71");
		value.setFinalValue("86.34");
		
		assertEquals(new FDecimal("210.05"), value.getInitialValue());
		assertEquals(new FDecimal("86.34"), value.getFinalValue());
		assertEquals(new FDecimal("-123.71"), value.getChangeValue());		
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetInitialValue_ThrowsWhenFinalAndChangeAreDefined() {
		value.setFinalValue("15.28");
		value.setChangeValue("10.08");
		
		value.setInitialValue("115.62");
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetFinalValue_ThrowsWhenInitialAndChangeAreDefined() {
		value.setInitialValue("115.92");
		value.setChangeValue("-15.01");
		
		value.setFinalValue("280.15");
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetChangeValue_ThrowsWhenInitialAndFinalAreDefined() {
		value.setInitialValue("-350.98");
		value.setFinalValue("760.12");
		
		value.setChangeValue("11.34");;
	}

	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setChangeValue("1.12");
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setFinalValue("390.12");
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setChangeValue("82.34");
		
		value.getFinalValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setInitialValue("115.08");
		
		value.getFinalValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setFinalValue("12.38");
		
		value.getChangeValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setInitialValue("56.12");
		
		value.getChangeValue();
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_String_ThrowsIfScaleMismatch() {
		value.setFinalValue("112.07");
		
		value.setInitialValue("347.289");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_FDecimal_ThrowsIfScaleMismatch() {
		value.setFinalValue("112.07");
		
		value.setInitialValue(new FDecimal("345.982"));
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_String_ThrowsIfScaleMismatch() {
		value.setInitialValue("112.07");
		
		value.setFinalValue("347.289");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_FDecimal_ThrowsIfScaleMismatch() {
		value.setInitialValue("112.07");
		
		value.setFinalValue(new FDecimal("347.289"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_String_ThrowsIfScaleMismatch() {
		value.setInitialValue("112.07");
		
		value.setChangeValue("115.476");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_FDecimal_ThrowsIfScaleMismatch() {
		value.setInitialValue("112.07");
		
		value.setChangeValue(new FDecimal("115.476"));
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(value.equals(value));
		assertFalse(value.equals(null));
		assertFalse(value.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(value.equals(new FDValueTriplet()));
		
		value.setInitialValue("115.26");
		value.setFinalValue("120.16");
		FDValueTriplet x = new FDValueTriplet();
		x.setInitialValue("115.26");
		x.setFinalValue("120.16");
		assertTrue(value.equals(x));
		
		x = new FDValueTriplet();
		x.setInitialValue("115.26");
		x.setChangeValue("4.95");
		assertFalse(value.equals(x));
		
		x = new FDValueTriplet();
		x.setChangeValue("4.95");
		x.setFinalValue("120.16");
		assertFalse(value.equals(x));
	}
	
	@Test
	public void testToString() {
		FDValueTriplet v1 = new FDValueTriplet(),
				v2 = new FDValueTriplet(),
				v3 = new FDValueTriplet(),
				v4 = new FDValueTriplet();
		v1.setInitialValue("42.97");
		v1.setFinalValue("12.15");
		v2.setInitialValue("42.975");
		v2.setChangeValue("26.032");
		v3.setChangeValue("10.1");
		v3.setFinalValue("8.5");
		
		assertEquals("[i=42.97 c=null f=12.15]", v1.toString());
		assertEquals("[i=42.975 c=26.032 f=null]", v2.toString());
		assertEquals("[i=null c=10.1 f=8.5]", v3.toString());
		assertEquals("[i=null c=null f=null]", v4.toString());
	}

}
