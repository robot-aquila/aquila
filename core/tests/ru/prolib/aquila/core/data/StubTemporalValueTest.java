package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class StubTemporalValueTest {
	private StubTemporalValue<Integer> stub;

	@Before
	public void setUp() throws Exception {
		stub = new StubTemporalValue<Integer>(815);
	}

	@Test
	public void testAt() throws Exception {
		assertEquals(new Integer(815), stub.at(DateTime.now()));
		stub.close();
		assertEquals(new Integer(815), stub.at(DateTime.now()));
	}

}
