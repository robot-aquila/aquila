package ru.prolib.aquila.core.data.finam.storage;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.ISO4217;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.SubScanIteratorBuilder;
import ru.prolib.aquila.core.data.SubScanner;
import ru.prolib.aquila.core.utils.FileNameEncoder;

public class DataStorageHelperTest {
	private IMocksControl control;
	private FileNameEncoder nameEncoder;
	private DataStorageHelper helper;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		nameEncoder = control.createMock(FileNameEncoder.class);
		helper = new DataStorageHelper(nameEncoder);
	}
	
	@Test
	public void testGetSafeFilename() throws Exception {
		expect(nameEncoder.encode("ZULU")).andReturn("OK1");
		expect(nameEncoder.encode("MOON")).andReturn("OK2");
		expect(nameEncoder.encode("GBP")).andReturn("OK3");
		expect(nameEncoder.encode("BOND")).andReturn("OK4");
		control.replay();
		
		assertEquals("OK1-OK2-OK3-OK4", helper.getSafeFilename(
				new SecurityDescriptor("ZULU", "MOON", ISO4217.GBP,
						SecurityType.BOND)));
		
		control.verify();
	}

	@Test
	public void testCreateTickFilesScanner() throws Exception {
		SubScanner<FileEntry> expected =
			new SubScanIteratorBuilder<FileEntry>(new DirectoryScannerY(),
				new SubScanIteratorBuilder<FileEntry>(new DirectoryScannerM(),
								new DirectoryScannerD("boom!"))),
		actual = helper.createTickFilesScanner("boom!");
		
		assertEquals(expected, actual);
	}

}
