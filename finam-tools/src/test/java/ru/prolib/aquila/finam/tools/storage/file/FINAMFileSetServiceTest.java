package ru.prolib.aquila.finam.tools.storage.file;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FINAMFileSetServiceTest {
	private FINAMFileSetService service;

	@Before
	public void setUp() throws Exception {
		service = new FINAMFileSetService();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals(".csv.gz", service.getRegularSuffix());
		assertEquals(".part.csv.gz", service.getTemporarySuffix());
	}

}
