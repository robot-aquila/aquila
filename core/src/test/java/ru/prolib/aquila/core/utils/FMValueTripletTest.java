package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.FMoney;

public class FMValueTripletTest {
	private FMValueTriplet value;

	@Before
	public void setUp() throws Exception {
		value = new FMValueTriplet();
	}
	
	@Test
	public void testSetters_InitialAndFinal_FMoney() {
		value.setInitialValue(new FMoney("210.05", "USD"));
		value.setFinalValue(new FMoney("86.34", "USD"));
		
		assertEquals(new FMoney("210.05", "USD"), value.getInitialValue());
		assertEquals(new FMoney("86.34", "USD"), value.getFinalValue());
		assertEquals(new FMoney("-123.71", "USD"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange_FMoney() {
		value.setInitialValue(new FMoney("210.05", "RUB"));
		value.setChangeValue(new FMoney("-123.71", "RUB"));
		
		assertEquals(new FMoney("210.05", "RUB"), value.getInitialValue());
		assertEquals(new FMoney("86.34", "RUB"), value.getFinalValue());
		assertEquals(new FMoney("-123.71", "RUB"), value.getChangeValue());
	}
	
	@Test
	public void testSetters_ChangeAndFinal_FMoney() {
		value.setChangeValue(new FMoney("-123.71", "JPY"));
		value.setFinalValue(new FMoney("86.34", "JPY"));
		
		assertEquals(new FMoney("210.05", "JPY"), value.getInitialValue());
		assertEquals(new FMoney("86.34", "JPY"), value.getFinalValue());
		assertEquals(new FMoney("-123.71", "JPY"), value.getChangeValue());		
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetInitialValue_ThrowsWhenFinalAndChangeAreDefined() {
		value.setFinalValue(new FMoney("15.28", "EUR"));
		value.setChangeValue(new FMoney("10.08", "EUR"));
		
		value.setInitialValue(new FMoney("115.62", "EUR"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetFinalValue_ThrowsWhenInitialAndChangeAreDefined() {
		value.setInitialValue(new FMoney("115.92", "CAD"));
		value.setChangeValue(new FMoney("-15.01", "CAD"));
		
		value.setFinalValue(new FMoney("280.15", "CAD"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetChangeValue_ThrowsWhenInitialAndFinalAreDefined() {
		value.setInitialValue(new FMoney("-350.98", "USD"));
		value.setFinalValue(new FMoney("760.12", "USD"));
		
		value.setChangeValue(new FMoney("11.34", "USD"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setChangeValue(new FMoney("1.12", "RUB"));
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setFinalValue(new FMoney("390.12", "RUB"));
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setChangeValue(new FMoney("82.34", "CAD"));
		
		value.getFinalValue();
	}

	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setInitialValue(new FMoney("115.08", "USD"));
		
		value.getFinalValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setFinalValue(new FMoney("12.38", "CAD"));
		
		value.getChangeValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setInitialValue(new FMoney("56.12", "JPY"));
		
		value.getChangeValue();
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_FMoney_ThrowsIfScaleMismatch() {
		value.setFinalValue(new FMoney("112.07", "RUB"));
		
		value.setInitialValue(new FMoney("345.982", "RUB"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetInitialValue_FMoney_ThrowsIfCurrencyMismatch() {
		value.setFinalValue(new FMoney("112.07", "RUB"));
		
		value.setInitialValue(new FMoney("112.07", "CAD"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_FMoney_ThrowsIfScaleMismatch() {
		value.setInitialValue(new FMoney("112.07", "USD"));
		
		value.setFinalValue(new FMoney("347.289", "USD"));
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetFinalValue_FMoney_ThrowsIfCurrencyMismatch() {
		value.setInitialValue(new FMoney("112.07", "USD"));
		
		value.setFinalValue(new FMoney("347.28", "RUB"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_FMoney_ThrowsIfScaleMismatch() {
		value.setInitialValue(new FMoney("112.07", "CAD"));
		
		value.setChangeValue(new FMoney("115.476", "CAD"));
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetChangeValue_FMoney_ThrowsIfCurrencyMismatch() {
		value.setInitialValue(new FMoney("112.07", "CAD"));
		
		value.setChangeValue(new FMoney("112.07", "RUB"));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(value.equals(value));
		assertFalse(value.equals(null));
		assertFalse(value.equals(this));
	}

	@Test
	public void testEquals() {
		assertTrue(value.equals(new FMValueTriplet()));
		
		value.setInitialValue(new FMoney("115.26", "USD"));
		value.setFinalValue(new FMoney("120.16", "USD"));
		FMValueTriplet x = new FMValueTriplet();
		x.setInitialValue(new FMoney("115.26", "USD"));
		x.setFinalValue(new FMoney("120.16", "USD"));
		assertTrue(value.equals(x));
		
		x = new FMValueTriplet();
		x.setInitialValue(new FMoney("115.26", "USD"));
		x.setChangeValue(new FMoney("4.95", "USD"));
		assertFalse(value.equals(x));
		
		x = new FMValueTriplet();
		x.setChangeValue(new FMoney("4.95", "USD"));
		x.setFinalValue(new FMoney("120.16", "USD"));
		assertFalse(value.equals(x));
		
		x = new FMValueTriplet();
		x.setInitialValue(new FMoney("115.26", "RUB"));
		x.setFinalValue(new FMoney("120.16", "RUB"));
		assertFalse(value.equals(x));
	}

	@Test
	public void testToString() {
		FMValueTriplet v1 = new FMValueTriplet(),
				v2 = new FMValueTriplet(),
				v3 = new FMValueTriplet(),
				v4 = new FMValueTriplet();
		v1.setInitialValue(new FMoney("42.97", "USD"));
		v1.setFinalValue(new FMoney("12.15", "USD"));
		v2.setInitialValue(new FMoney("42.975", "RUB"));
		v2.setChangeValue(new FMoney("26.032", "RUB"));
		v3.setChangeValue(new FMoney("10.1", "CAD"));
		v3.setFinalValue(new FMoney("8.5", "CAD"));
		
		assertEquals("[i=42.97 USD c=null f=12.15 USD]", v1.toString());
		assertEquals("[i=42.975 RUB c=26.032 RUB f=null]", v2.toString());
		assertEquals("[i=null c=10.1 CAD f=8.5 CAD]", v3.toString());
		assertEquals("[i=null c=null f=null]", v4.toString());
	}

}
