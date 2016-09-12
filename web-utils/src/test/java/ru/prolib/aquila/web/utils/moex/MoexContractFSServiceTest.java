package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MoexContractFSServiceTest {
	private MoexContractFSService service;

	@Before
	public void setUp() throws Exception {
		service = new MoexContractFSService();
	}

	@Test
	public void testGetSuffixes() {
		assertEquals("-moex-contract-details-daily.txt", service.getRegularSuffix());
		assertEquals("-moex-contract-details-daily.tmp", service.getTemporarySuffix());
	}

}
