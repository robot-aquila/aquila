package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*; 

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegment;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentImpl;

@SuppressWarnings("unused")
public class CsvDataSegmentManagerTest {
	private static final Logger logger;
	private static final File root;
	private static final Symbol symbol1;

	static {
		logger = LoggerFactory.getLogger(CsvDataSegmentManagerTest.class);
		root = new File("fixture", "finam-tests");
		symbol1 = new Symbol("RTS", "SPB", "USD", SymbolType.FUTURE);
	}
	
	private Scheduler scheduler;
	private CsvDataSegmentManager manager;
	private InputStream inputStream;
	private OutputStream outputStream;
	private File file1, file2, file3;
	private IOHelper helper; 
	private IMocksControl control;
	
	@Before
	public void setUp() throws Exception {
		root.mkdirs(); // to pass IOHelper's ctor check
		
		control = createStrictControl();
		inputStream = control.createMock(InputStream.class);
		outputStream = control.createMock(OutputStream.class);
		file1 = control.createMock(File.class);
		file2 = control.createMock(File.class);
		file3 = control.createMock(File.class);
		helper = control.createMock(IOHelper.class);
		
		manager = new CsvDataSegmentManager(helper);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test (expected=FinamException.class)
	public void testCtor_ThrowsIfDirectoryNotExists() throws Exception {
		new CsvDataSegmentManager(new File(root, "bergandabupkhta"));
	}
	
	@Test
	public void testOpenSegment() throws Exception {
		LocalDate date = LocalDate.of(1998, 1, 15);
		CsvTickWriter csvWriter = control.createMock(CsvTickWriter.class);
		TickWriter flusher = control.createMock(TickWriter.class);
		expect(helper.getFile(symbol1, date, ".csv.part")).andReturn(file1);
		expect(file1.getParentFile()).andReturn(file2);
		expect(file2.exists()).andReturn(false);
		expect(file2.mkdirs()).andReturn(true);
		expect(helper.createOutputStream(file1, false)).andReturn(outputStream);
		expect(helper.createCsvTickWriter(outputStream)).andReturn(csvWriter);
		csvWriter.writeHeader();
		expect(helper.addSmartFlush(csvWriter, "[F:RTS@SPB:USD#1998-01-15]"))
			.andReturn(flusher);
		control.replay();
		
		DataSegment dummy = manager.openSegment(symbol1, date);
		
		assertNotNull(dummy);
		DataSegmentImpl x = (DataSegmentImpl) dummy;
		assertSame(flusher, x.getWriter());
		assertEquals(symbol1, x.getSymbol());
		assertEquals(date, x.getDate());
		control.verify();
	}
	
	@Test
	public void testCloseSegment() throws Exception {
		LocalDate date = LocalDate.of(2001, 9, 11);
		DataSegment writer = control.createMock(DataSegment.class);
		expect(writer.getDate()).andStubReturn(date);
		expect(writer.getSymbol()).andStubReturn(symbol1);
		writer.close();
		expect(helper.getFile(symbol1, date, ".csv.part")).andReturn(file1);
		expect(helper.getFile(symbol1, date, ".csv.gz.part")).andReturn(file2);
		expect(helper.getFile(symbol1, date, ".csv.gz")).andReturn(file3);
		expect(helper.createInputStream(file1)).andReturn(inputStream);
		expect(helper.createGzipOutputStream(file2)).andReturn(outputStream);
		helper.copyStream(inputStream, outputStream);
		inputStream.close();
		outputStream.close();
		expect(file3.delete()).andReturn(true);
		expect(file2.renameTo(file3)).andReturn(true);
		expect(file1.delete()).andReturn(true);
		control.replay();
		
		manager.closeSegment(writer);
		
		control.verify();
	}
	
	@Test
	public void testOpenReader_Zipped() throws Exception {
		LocalDate date = LocalDate.of(1998, 1, 15);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(symbol1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(symbol1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(true);
		expect(helper.createGzipInputStream(file1)).andReturn(inputStream);
		expect(helper.createCsvTickReader(inputStream, date)).andReturn(reader);
		reader.readHeader();
		control.replay();
		
		Aqiterator<Tick> actual = manager.openReader(symbol1, date);
		
		assertNotNull(actual);
		assertSame(reader, actual);
	}
	
	@Test
	public void testOpenReader_Raw() throws Exception {
		LocalDate date = LocalDate.of(2001, 1, 29);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(symbol1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(symbol1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(false);
		expect(file2.exists()).andReturn(true);
		expect(helper.createInputStream(file2)).andReturn(inputStream);
		expect(helper.createCsvTickReader(inputStream, date)).andReturn(reader);
		reader.readHeader();
		control.replay();
		
		Aqiterator<Tick> actual = manager.openReader(symbol1, date);
		
		assertNotNull(actual);
		assertSame(reader, actual);
	}
	
	@Test (expected=IOException.class)
	public void testOpenReader_NoSegmentFile() throws Exception {
		LocalDate date = LocalDate.of(2001, 1, 29);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(symbol1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(symbol1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(false);
		expect(file2.exists()).andReturn(false);
		control.replay();
		
		manager.openReader(symbol1, date);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCloseReader() throws Exception {
		Aqiterator<Tick> iterator = control.createMock(Aqiterator.class);
		iterator.close();
		control.replay();
		
		manager.closeReader(iterator);
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable1_Yes() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.now());
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		assertTrue(manager.isDataAvailable(symbol1));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable1_No() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		assertFalse(manager.isDataAvailable(symbol1));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable2_Yes() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(2015, 8, 24));
		list.add(LocalDate.of(2015, 8, 25));
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		assertTrue(manager.isDataAvailable(symbol1, LocalDate.of(2015, 8, 24)));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable2_No() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(2015, 8, 24));
		list.add(LocalDate.of(2015, 8, 25));
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		assertFalse(manager.isDataAvailable(symbol1, LocalDate.of(2015, 8, 23)));
		
		control.verify();
	}
	
	@Test
	public void testGetDateOfFirstSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(2015, 8, 24));
		list.add(LocalDate.of(2015, 8, 25));
		list.add(LocalDate.of(2015, 8, 26));
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		LocalDate expected = LocalDate.of(2015, 8, 24),
				actual = manager.getDateOfFirstSegment(symbol1);
		
		assertEquals(expected, actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfFirstSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		LocalDate actual = manager.getDateOfFirstSegment(symbol1);
		
		assertNull(actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfLastSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(2011, 9, 1));
		list.add(LocalDate.of(2012, 1, 1));
		list.add(LocalDate.of(2013, 2, 1));
		list.add(LocalDate.of(2014, 3, 1));
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		LocalDate expected = LocalDate.of(2014, 3, 1),
				actual = manager.getDateOfLastSegment(symbol1);
		
		assertEquals(expected, actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfLastSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		LocalDate actual = manager.getDateOfLastSegment(symbol1);
		
		assertNull(actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfNextSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(1998,  8,  1));
		list.add(LocalDate.of(1999, 11,  1));
		list.add(LocalDate.of(1999, 12, 29));
		list.add(LocalDate.of(1999, 12, 30));
		list.add(LocalDate.of(1999, 12, 31));
		list.add(LocalDate.of(2003,  8,  1));
		list.add(LocalDate.of(2004,  6, 10));
		list.add(LocalDate.of(2004,  6, 11));
		list.add(LocalDate.of(2004,  6, 12));
		list.add(LocalDate.of(2005,  6, 10));
		expect(helper.getAvailableDataSegments(symbol1)).andStubReturn(list);
		control.replay();
		
		LocalDate expected1 = LocalDate.of(2004, 6, 12),
			expected2 = LocalDate.of(1999, 12, 29),
			actual1 = manager.getDateOfNextSegment(symbol1, LocalDate.of(2004, 6, 11)),
			actual2 = manager.getDateOfNextSegment(symbol1, LocalDate.of(1999, 11, 1));
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
		control.verify();
	}
	
	@Test
	public void testGetDateOfNextSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(symbol1)).andStubReturn(list);
		control.replay();
		
		assertNull(manager.getDateOfNextSegment(symbol1, LocalDate.of(1999, 12, 1)));
		assertNull(manager.getDateOfNextSegment(symbol1, LocalDate.of(2004, 6, 11)));
		
		control.verify();
	}
	
	@Test
	public void testGetSegmentList() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(LocalDate.of(1999, 11, 2));
		expect(helper.getAvailableDataSegments(symbol1)).andReturn(list);
		control.replay();
		
		assertSame(list, manager.getSegmentList(symbol1));
		
		control.verify();
	}
	
	@Test 
	public void testCloseSegment_ExestingArchivBugFix() throws Exception {
		DataSegment dataSegment = new DataSegmentImpl(symbol1, LocalDate.of(2001, 9, 11), new CsvTickWriter(new ByteArrayOutputStream()));
		new File(root, "RTS-SPB-USD-F/2001/09").mkdirs();
		File f1 = new File(root, "RTS-SPB-USD-F/2001/09/RTS-SPB-USD-F-20010911.csv.part");
		FileUtils.copyFile(new File("fixture/CsvDataSegmentManager-Test1.csv.part"), f1);
		File f2 = new File(root, "RTS-SPB-USD-F/2001/09/RTS-SPB-USD-F-20010911.csv.gz");
		f2.createNewFile();
		manager = new CsvDataSegmentManager(root);
		
		manager.closeSegment(dataSegment);
		
	    assertTrue(FileUtils.contentEquals(new File("fixture/EttalonRTS-SPB-USD-F-20010911.csv.gz"), f2));
		assertFalse(new File(root, "RTS-SPB-USD-F/2001/09/RTS-SPB-USD-F-20010911.csv.gz.part").exists());
	}

}
