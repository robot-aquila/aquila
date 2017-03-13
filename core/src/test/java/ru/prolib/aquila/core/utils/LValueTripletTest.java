package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.LValueTriplet;

public class LValueTripletTest {
	private LValueTriplet value;

	@Before
	public void setUp() throws Exception {
		value = new LValueTriplet();
	}
	
	@Test
	public void testSetters_InitialAndFinal() {
		value.setInitialValue(205L);
		value.setFinalValue(118L);
		
		assertEquals(205L, value.getInitialValue());
		assertEquals(118L, value.getFinalValue());
		assertEquals(-87L, value.getChangeValue());
	}
	
	@Test
	public void testSetters_InitialAndChange() {
		value.setInitialValue(205L);
		value.setChangeValue(-87L);
		
		assertEquals(205L, value.getInitialValue());
		assertEquals(118L, value.getFinalValue());
		assertEquals(-87L, value.getChangeValue());
	}
	
	@Test
	public void testSetter_ChangeAndFinal() {
		value.setChangeValue(-87L);
		value.setFinalValue(118L);
		
		assertEquals(205L, value.getInitialValue());
		assertEquals(118L, value.getFinalValue());
		assertEquals(-87L, value.getChangeValue());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetInitialValue_ThrowsWhenFinalAndChangeAreDefined() {
		value.setChangeValue(87L);
		value.setFinalValue(118L);
		
		value.setInitialValue(205L);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetFinalValue_ThrowsWhenInitialAndChangeAreDefined() {
		value.setInitialValue(205L);
		value.setChangeValue(87L);
		
		value.setFinalValue(118L);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetChangeValue_ThrowsWhenInitialAndFinalAreDefined() {
		value.setInitialValue(205L);
		value.setFinalValue(118L);
		
		value.setChangeValue(87L);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setChangeValue(87L);
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetInitialValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setFinalValue(118L);
		
		value.getInitialValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setChangeValue(87L);
		
		value.getFinalValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetFinalValue_ThrowsWhenUndefinedAndChangeIsNotSet() {
		value.setInitialValue(205L);
		
		value.getFinalValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndInitialIsNotSet() {
		value.setFinalValue(118L);
		
		value.getChangeValue();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetChangeValue_ThrowsWhenUndefinedAndFinalIsNotSet() {
		value.setInitialValue(205L);
		
		value.getChangeValue();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(value.equals(value));
		assertFalse(value.equals(null));
		assertFalse(value.equals(this));
	}
	
	@Test
	public void testEquals() {
		value.setInitialValue(205L);
		value.setFinalValue(118L);
		LValueTriplet v1 = new LValueTriplet(),
				v2 = new LValueTriplet(),
				v3 = new LValueTriplet(),
				v4 = new LValueTriplet();
		v1.setInitialValue(205L);
		v1.setFinalValue(118L);
		v2.setInitialValue(205L);
		v2.setChangeValue(-87L);
		v3.setChangeValue(-87L);
		v3.setFinalValue(118L);
		
		assertTrue(value.equals(v1));
		assertFalse(value.equals(v2));
		assertFalse(value.equals(v3));
		assertFalse(value.equals(v4));
	}

	@Test
	public void testToString() {
		LValueTriplet v1 = new LValueTriplet(),
				v2 = new LValueTriplet(),
				v3 = new LValueTriplet(),
				v4 = new LValueTriplet();
		v1.setInitialValue(25L);
		v1.setFinalValue(100L);
		v2.setInitialValue(100L);
		v2.setChangeValue(56L);
		v3.setChangeValue(-87L);
		v3.setFinalValue(118L);
		
		assertEquals("[i=25 c=null f=100]", v1.toString());
		assertEquals("[i=100 c=56 f=null]", v2.toString());
		assertEquals("[i=null c=-87 f=118]", v3.toString());
		assertEquals("[i=null c=null f=null]", v4.toString());
	}

}
