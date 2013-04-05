package ru.prolib.aquila.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.*;

public class AlignDateMinuteTest {
	AlignDateMinute aligner;
	
	@Before
	public void setUp() throws Exception {
		aligner = new AlignDateMinute(10);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(10, aligner.getPeriod());
	}
	
	@Test
	public void testAlign() throws Exception {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		
		c.set(2001, 5, 1, 23, 10, 0);
		Date expected = c.getTime();
		c.set(Calendar.MINUTE, 15);
		c.set(Calendar.SECOND, 22);
		assertEquals(expected, aligner.align(c.getTime()));
		
		c.set(2012, 1, 8, 18, 40, 0);
		expected = c.getTime();
		c.set(Calendar.MINUTE, 49);
		c.set(Calendar.SECOND, 59);
		assertEquals(expected, aligner.align(c.getTime()));
	}

}
