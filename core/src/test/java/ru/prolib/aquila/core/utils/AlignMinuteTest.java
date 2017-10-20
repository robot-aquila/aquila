package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * 2013-03-02<br>
 * $Id: AlignDateMinuteTest.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class AlignMinuteTest {
	AlignMinute aligner;
	
	@Before
	public void setUp() throws Exception {
		aligner = new AlignMinute(10);
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(aligner.equals(aligner));
		assertFalse(aligner.equals(null));
		assertFalse(aligner.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(aligner.equals(new AlignMinute(10)));
		assertFalse(aligner.equals(new AlignMinute(11)));
	}

}
