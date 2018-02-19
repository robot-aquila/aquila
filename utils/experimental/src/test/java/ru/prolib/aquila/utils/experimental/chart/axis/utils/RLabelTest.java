package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class RLabelTest {
	private RLabel label;

	@Before
	public void setUp() throws Exception {
		label = new RLabel(of(10L), 25, "foo", 500);
	}
	
	@Test
	public void testCtor4() {
		assertEquals(of(10L), label.getValue());
		assertEquals(25, label.getCategoryIndex());
		assertEquals("foo", label.getText());
		assertEquals(500, label.getCoord());
	}
	
	@Test
	public void testCtor3_DTI() {
		label = new RLabel(of(25L), "25", 39);
		assertEquals(of(25L), label.getValue());
		assertEquals(-1, label.getCategoryIndex());
		assertEquals("25", label.getText());
		assertEquals(39, label.getCoord());
	}
	
	@Test
	public void testCtor3_ITI() {
		label = new RLabel(400, "23:00", 45);
		assertNull(label.getValue());
		assertEquals(400, label.getCategoryIndex());
		assertEquals("23:00", label.getText());
		assertEquals(45, label.getCoord());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(label.equals(label));
		assertFalse(label.equals(null));
		assertFalse(label.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<CDecimal> vVal = new Variant<>(of(10L), of(25L));
		Variant<Integer> vCat = new Variant<>(vVal, 25, 99);
		Variant<String> vTxt = new Variant<>(vCat, "foo", "bar");
		Variant<Integer> vCrd = new Variant<>(vTxt, 500, 928);
		Variant<?> iterator = vCrd;
		int foundCnt = 0;
		RLabel x, found = null;
		do {
			x = new RLabel(vVal.get(), vCat.get(), vTxt.get(), vCrd.get());
			if ( label.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(of(10L), found.getValue());
		assertEquals(25, found.getCategoryIndex());
		assertEquals("foo", found.getText());
		assertEquals(500, found.getCoord());
	}
	
	@Test
	public void testToString() {
		String expected = "RLabel[value=10 category=25 text=foo coord=500]";
		assertEquals(expected, label.toString());
	}

}
