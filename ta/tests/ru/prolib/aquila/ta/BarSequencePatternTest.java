package ru.prolib.aquila.ta;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ta.BarPattern;
import ru.prolib.aquila.ta.BarSequencePattern;

public class BarSequencePatternTest {
	IMocksControl control;
	LinkedList<BarPattern> bars;
	BarSequencePattern pattern;
	

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		bars = new LinkedList<BarPattern>();
		bars.add(new BarPattern(1,2,3,4));
		bars.add(new BarPattern(2,3,4,5));
		bars.add(new BarPattern(3,4,5,6));
		pattern = new BarSequencePattern(bars);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertNotNull(pattern.getBars());
		assertNotSame(bars, pattern.getBars());
		assertEquals(bars, pattern.getBars());
		assertEquals(3, pattern.getLength());
	}
	
	@Test
	public void testEquals_NeIfDiffLength() throws Exception {
		LinkedList<BarPattern> otherBars = new LinkedList<BarPattern>();
		otherBars.add(new BarPattern(1,2,3,4));
		otherBars.add(new BarPattern(2,3,4,5));
		BarSequencePattern other = new BarSequencePattern(otherBars);
		
		assertFalse(pattern.equals(other));
	}
	
	@Test
	public void testEquals_NeIfNotAllBarsAreEq() throws Exception {
		LinkedList<BarPattern> otherBars = new LinkedList<BarPattern>();
		otherBars.add(new BarPattern(1,2,3,4));
		otherBars.add(new BarPattern(2,3,4,5));
		otherBars.add(new BarPattern(3,4,5,66));
		BarSequencePattern other = new BarSequencePattern(otherBars);
		
		assertFalse(pattern.equals(other));
	}
	
	@Test
	public void testEquals_EqIfAllBarsAreEq() throws Exception {
		LinkedList<BarPattern> otherBars = new LinkedList<BarPattern>();
		otherBars.add(new BarPattern(1,2,3,4));
		otherBars.add(new BarPattern(2,3,4,5));
		otherBars.add(new BarPattern(3,4,5,6));
		BarSequencePattern other = new BarSequencePattern(otherBars);
		
		assertTrue(pattern.equals(other));
	}
	
	@Test
	public void testEquals_EqIfSameObject() throws Exception {
		assertTrue(pattern.equals(pattern));
	}
	
	@Test
	public void testEquals_NeIfDifferClass() throws Exception {
		assertFalse(pattern.equals(this));
	}
	
	@Test
	public void testEquals_NeIfNull() throws Exception {
		assertFalse(pattern.equals(null));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("[T:1 B:2 O:3 C:4; T:2 B:3 O:4 C:5; T:3 B:4 O:5 C:6]",
				pattern.toString());
	}

}
