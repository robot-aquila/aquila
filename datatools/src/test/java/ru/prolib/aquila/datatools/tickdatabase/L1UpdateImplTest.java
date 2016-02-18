package ru.prolib.aquila.datatools.tickdatabase;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class L1UpdateImplTest {
	private Symbol symbol1, symbol2;
	private L1Update update;

	@Before
	public void setUp() throws Exception {
		symbol1 = new Symbol("GAZP");
		symbol2 = new Symbol("SBER");
		update = new L1UpdateImpl(symbol1, Tick.of(Instant.parse("2016-02-18T10:10:00Z"), 14.45d));
	}

	@Test
	public void testCtor() {
		assertEquals(symbol1, update.getSymbol());
		assertEquals(Tick.of(Instant.parse("2016-02-18T10:10:00Z"), 14.45d), update.getTick());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
	}
	
	@Test
	public void testEquals() {
		Tick tick1 = Tick.of(Instant.parse("2016-02-18T10:10:00Z"), 14.45d);
		Tick tick2 = Tick.of(Instant.parse("1998-11-15T00:20:15Z"), 16.78d);
		L1Update update1 = new L1UpdateImpl(symbol1, tick1),
				update2 = new L1UpdateImpl(symbol2, tick1),
				update3 = new L1UpdateImpl(symbol1, tick2),
				update4 = new L1UpdateImpl(symbol2, tick2);
		assertEquals(update, update1);
		assertNotEquals(update, update2);
		assertNotEquals(update, update3);
		assertNotEquals(update, update4);
	}

}
