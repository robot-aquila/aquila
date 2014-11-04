package ru.prolib.aquila.core.data.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.FileNameEncoder;

public class IdUtilsTest {
	private IMocksControl control;
	private FileNameEncoder encoder;
	private IdUtils utils;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		encoder = control.createMock(FileNameEncoder.class);
		utils = new IdUtils(encoder);
	}

	@Test
	public void testGetSafeFilename() throws Exception {
		expect(encoder.encode("ZULU")).andReturn("OK1");
		expect(encoder.encode("MOON")).andReturn("OK2");
		expect(encoder.encode("GBP")).andReturn("OK3");
		expect(encoder.encode("BOND")).andReturn("OK4");
		control.replay();
		
		assertEquals("OK1-OK2-OK3-OK4", utils.getSafeFilename(
				new SecurityDescriptor("ZULU", "MOON", ISO4217.GBP,
						SecurityType.BOND)));
		
		control.verify();
	}

}
