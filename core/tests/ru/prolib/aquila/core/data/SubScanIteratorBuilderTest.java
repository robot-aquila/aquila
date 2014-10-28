package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.data.finam.storage.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SubScanIteratorBuilderTest {
	private IMocksControl control;
	private Aqiterator<FileEntry> dirList;
	private SubScanner mainScanner, dirScanner;
	private SubScanIteratorBuilder builder;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dirList = control.createMock(Aqiterator.class);
		mainScanner = control.createMock(SubScanner.class);
		dirScanner = control.createMock(SubScanner.class);
		builder = new SubScanIteratorBuilder(mainScanner, dirScanner);
	}
	
	@Test
	public void testMakeScan() throws Exception {
		FileEntry entry = control.createMock(FileEntry.class);
		expect(mainScanner.makeScan(eq(entry))).andReturn(dirList);
		control.replay();
		
		Aqiterator expected = new SubScanIterator(dirList, dirScanner),
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
		SubScanner mainScanner2, dirScanner2;
		mainScanner2 = control.createMock(SubScanner.class);
		dirScanner2 = control.createMock(SubScanner.class);
		
		assertTrue(builder.equals(new SubScanIteratorBuilder(mainScanner, dirScanner)));
		assertFalse(builder.equals(new SubScanIteratorBuilder(mainScanner2, dirScanner)));
		assertFalse(builder.equals(new SubScanIteratorBuilder(mainScanner2, dirScanner2)));
		assertFalse(builder.equals(new SubScanIteratorBuilder(mainScanner, dirScanner2)));
	}

}
