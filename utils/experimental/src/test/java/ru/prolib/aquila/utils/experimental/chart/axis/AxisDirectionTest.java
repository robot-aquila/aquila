package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Test;

public class AxisDirectionTest {

	@Test
	public void testConstants() {
		assertTrue(AxisDirection.RIGHT.isHorizontal());
		assertFalse(AxisDirection.UP.isHorizontal());
		
		assertFalse(AxisDirection.RIGHT.isVertical());
		assertTrue(AxisDirection.UP.isVertical());
	}

}
