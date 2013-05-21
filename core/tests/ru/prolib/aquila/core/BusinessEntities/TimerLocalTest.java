package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

public class TimerLocalTest {
	private TimerLocal timer;

	@Before
	public void setUp() throws Exception {
		timer = new TimerLocal();
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		assertEquals(new Date(), timer.getCurrentTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(timer.equals(timer));
		assertTrue(timer.equals(new TimerLocal()));
		assertFalse(timer.equals(null));
		assertFalse(timer.equals(this));
	}

}
