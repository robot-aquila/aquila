package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class MTFLabelTest {
	private MTFLabel label;

	@Before
	public void setUp() throws Exception {
		label = new MTFLabel(LocalTime.of(22, 57), "22:57", false);
	}
	
	@Test
	public void testCtor() {
		assertEquals(LocalTime.of(22, 57), label.getTime());
		assertEquals("22:57", label.getText());
		assertFalse(label.isHourBoundary());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(label.equals(label));
		assertFalse(label.equals(null));
		assertFalse(label.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<LocalTime> vTime = new Variant<LocalTime>()
			.add(LocalTime.of(22, 57))
			.add(LocalTime.of(0, 0));
		Variant<String> vText = new Variant<>(vTime, "22:57", "00:00");
		Variant<Boolean> vHB = new Variant<>(vText, false, true);
		Variant<?> iterator = vHB;
		int foundCnt = 0;
		MTFLabel x, found = null;
		do {
			x = new MTFLabel(vTime.get(), vText.get(), vHB.get());
			if ( label.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(LocalTime.of(22, 57), found.getTime());
		assertEquals("22:57", found.getText());
		assertFalse(found.isHourBoundary());
	}
	
	@Test
	public void testToString() {
		String expected = "MTFLabel[time=22:57 text=22:57 hourBoundary=false]";
		assertEquals(expected, label.toString());
	}

}
