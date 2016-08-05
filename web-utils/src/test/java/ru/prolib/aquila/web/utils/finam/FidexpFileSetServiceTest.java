package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FidexpFileSetServiceTest {
	private FidexpFileSetService service;

	@Before
	public void setUp() throws Exception {
		service = new FidexpFileSetService();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals(".csv.gz", service.getRegularSuffix());
		assertEquals(".part.csv.gz", service.getTemporarySuffix());
	}

}
