package ru.prolib.aquila.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.prolib.aquila.util.SequenceLong;


public class SequenceLongTest {

	@Test
	public void testGetNextId() {
		SequenceLong seq = new SequenceLong();
		for ( long i = 1; i < 10; i ++ ) {
			assertEquals((Long)i, seq.next());
		}
	}

}
