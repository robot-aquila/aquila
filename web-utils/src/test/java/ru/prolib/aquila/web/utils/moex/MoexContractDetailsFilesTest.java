package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.data.storage.file.Files;

public class MoexContractDetailsFilesTest {
	private Files files;

	@Before
	public void setUp() throws Exception {
		files = new MoexContractDetailsFiles();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals("-moex-contract-details-daily.txt", files.getRegularSuffix());
		assertEquals("-moex-contract-details-daily.tmp", files.getTemporarySuffix());
	}

}
