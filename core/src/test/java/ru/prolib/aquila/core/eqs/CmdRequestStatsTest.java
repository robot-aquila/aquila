package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdRequestStatsTest {
	private CmdRequestStats service;

	@Before
	public void setUp() throws Exception {
		service = new CmdRequestStats();
	}

	@Test
	public void testGetters() {
		assertEquals(CmdType.GET_STATS, service.getType());
		assertNotNull(service.getResult());
	}

}
