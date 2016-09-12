package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FidexpFSServiceTest {
	private FidexpFSService service;

	@Before
	public void setUp() throws Exception {
		service = new FidexpFSService();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals(".csv.gz", service.getRegularSuffix());
		assertEquals(".part.csv.gz", service.getTemporarySuffix());
	}

}
