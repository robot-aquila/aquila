package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FidexpFilesTest {
	private FidexpFiles files;

	@Before
	public void setUp() throws Exception {
		files = new FidexpFiles();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals(".csv.gz", files.getRegularSuffix());
		assertEquals(".part.csv.gz", files.getTemporarySuffix());
	}

}
