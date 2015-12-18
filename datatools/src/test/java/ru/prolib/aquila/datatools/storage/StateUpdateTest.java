package ru.prolib.aquila.datatools.storage;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class StateUpdateTest {

	@Test
	public void testCtor3() {
		byte data[] = { 1, 2, 3, 4, 5, 6 };
		StateUpdate update = new StateUpdate(LocalDateTime.of(2015, 12, 16, 10, 26, 15), data, true);
		
		assertEquals(LocalDateTime.of(2015, 12, 16, 10, 26, 15), update.getTimestamp());
		assertArrayEquals(data, update.getData());
		assertTrue(update.isFullRefresh());
	}
	
	@Test
	public void testCtor2() {
		byte data[] = { 9, 8, 7, 6, 5, 4, 3 };
		StateUpdate update = new StateUpdate(LocalDateTime.of(2015, 12, 1, 1, 0, 0), data);
		
		assertEquals(LocalDateTime.of(2015, 12, 1, 1, 0, 0), update.getTimestamp());
		assertArrayEquals(data, update.getData());
		assertFalse(update.isFullRefresh());
	}

}
