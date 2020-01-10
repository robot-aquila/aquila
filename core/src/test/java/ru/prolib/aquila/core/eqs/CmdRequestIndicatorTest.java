package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdRequestIndicatorTest {
	private CmdRequestIndicator service;

	@Before
	public void setUp() throws Exception {
		service = new CmdRequestIndicator();
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.CREATE_INDICATOR, service.getType());
		assertNotNull(service.getResult());
	}

}
