package ru.prolib.aquila.datatools.storage;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SecurityStateUpdateTest {
	private Symbol symbol;
	private SecurityStateUpdate update;
	
	@Before
	public void setUp() {
		symbol = new Symbol("SBER");
	}

	@Test
	public void testCtor4() {
		byte data[] = { 1, 2, 3, 4, 5, 6 };
		update = new SecurityStateUpdate(symbol,
				LocalDateTime.of(2015, 12, 16, 10, 26, 15), data, true);
		
		assertEquals(symbol, update.getSymbol());
		assertEquals(LocalDateTime.of(2015, 12, 16, 10, 26, 15), update.getTimestamp());
		assertArrayEquals(data, update.getData());
		assertTrue(update.isFullRefresh());
	}
	
	@Test
	public void testCtor3() {
		byte data[] = { 9, 8, 7, 6, 5, 4, 3 };
		update = new SecurityStateUpdate(symbol, LocalDateTime.of(2015, 12, 1, 1, 0, 0), data);
		
		assertEquals(symbol, update.getSymbol());
		assertEquals(LocalDateTime.of(2015, 12, 1, 1, 0, 0), update.getTimestamp());
		assertArrayEquals(data, update.getData());
		assertFalse(update.isFullRefresh());
	}
}
