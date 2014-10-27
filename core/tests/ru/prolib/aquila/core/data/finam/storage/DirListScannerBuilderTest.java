package ru.prolib.aquila.core.data.finam.storage;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.Aqiterator;

public class DirListScannerBuilderTest {
	private IMocksControl control;
	private Aqiterator<FileEntry> dirList;
	private DirectoryScanner mainScanner, dirScanner;
	private DirListScannerBuilder builder;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dirList = control.createMock(Aqiterator.class);
		mainScanner = control.createMock(DirectoryScanner.class);
		dirScanner = control.createMock(DirectoryScanner.class);
		builder = new DirListScannerBuilder(mainScanner, dirScanner);
	}
	
	@Test
	public void testMakeScan() throws Exception {
		FileEntry entry = control.createMock(FileEntry.class);
		expect(mainScanner.makeScan(eq(entry))).andReturn(dirList);
		control.replay();
		
		Aqiterator<FileEntry> expected = new DirListScanner(dirList, dirScanner),
				actual = builder.makeScan(entry);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		DirectoryScanner mainScanner2, dirScanner2;
		mainScanner2 = control.createMock(DirectoryScanner.class);
		dirScanner2 = control.createMock(DirectoryScanner.class);
		
		assertTrue(builder.equals(new DirListScannerBuilder(mainScanner, dirScanner)));
		assertFalse(builder.equals(new DirListScannerBuilder(mainScanner2, dirScanner)));
		assertFalse(builder.equals(new DirListScannerBuilder(mainScanner2, dirScanner2)));
		assertFalse(builder.equals(new DirListScannerBuilder(mainScanner, dirScanner2)));
	}

}
