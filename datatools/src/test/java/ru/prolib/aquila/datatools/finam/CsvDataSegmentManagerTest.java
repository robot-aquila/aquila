package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*; 

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegment;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentImpl;

@SuppressWarnings("unused")
public class CsvDataSegmentManagerTest {
	private static final Logger logger;
	private static final File root;
	private static final SecurityDescriptor descr1;

	static {
		logger = LoggerFactory.getLogger(CsvDataSegmentManagerTest.class);
		root = new File("fixture", "finam-tests");
		descr1 = new SecurityDescriptor("RTS", "SPB", "USD", SecurityType.FUT);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
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
		
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr1);
		security.setPrecision(2);
		security.setMinStepSize(0.01d);
		
		manager = new CsvDataSegmentManager(terminal, helper);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test (expected=FinamException.class)
	public void testCtor_ThrowsIfDirectoryNotExists() throws Exception {
		new CsvDataSegmentManager(terminal, new File(root, "bergandabupkhta"));
	}
	
	@Test
	public void testOpenSegment() throws Exception {
		LocalDate date = new LocalDate(1998, 1, 15);
		CsvTickWriter csvWriter = control.createMock(CsvTickWriter.class);
		TickWriter flusher = control.createMock(TickWriter.class);
		expect(helper.getFile(descr1, date, ".csv.part")).andReturn(file1);
		expect(file1.getParentFile()).andReturn(file2);
		expect(file2.exists()).andReturn(false);
		expect(file2.mkdirs()).andReturn(true);
		expect(helper.createOutputStream(file1, false)).andReturn(outputStream);
		expect(helper.createCsvTickWriter(security, outputStream))
			.andReturn(csvWriter);
		csvWriter.writeHeader();
		expect(helper.addSmartFlush(csvWriter, "[RTS@SPB(FUT/USD)#1998-01-15]"))
			.andReturn(flusher);
		control.replay();
		
		DataSegment dummy = manager.openSegment(descr1, date);
		
		assertNotNull(dummy);
		DataSegmentImpl x = (DataSegmentImpl) dummy;
		assertSame(flusher, x.getWriter());
		assertEquals(descr1, x.getSecurityDescriptor());
		assertEquals(date, x.getDate());
		control.verify();
	}
	
	@Test
	public void testCloseSegment() throws Exception {
		LocalDate date = new LocalDate(2001, 9, 11);
		DataSegment writer = control.createMock(DataSegment.class);
		expect(writer.getDate()).andStubReturn(date);
		expect(writer.getSecurityDescriptor()).andStubReturn(descr1);
		writer.close();
		expect(helper.getFile(descr1, date, ".csv.part")).andReturn(file1);
		expect(helper.getFile(descr1, date, ".csv.gz.part")).andReturn(file2);
		expect(helper.getFile(descr1, date, ".csv.gz")).andReturn(file3);
		expect(helper.createInputStream(file1)).andReturn(inputStream);
		expect(helper.createGzipOutputStream(file2)).andReturn(outputStream);
		helper.copyStream(inputStream, outputStream);
		inputStream.close();
		outputStream.close();
		expect(file2.renameTo(file3)).andReturn(true);
		expect(file1.delete()).andReturn(true);
		control.replay();
		
		manager.closeSegment(writer);
		
		control.verify();
	}
	
	@Test
	public void testOpenReader_Zipped() throws Exception {
		LocalDate date = new LocalDate(1998, 1, 15);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(descr1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(descr1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(true);
		expect(helper.createGzipInputStream(file1)).andReturn(inputStream);
		expect(helper.createCsvTickReader(inputStream, date)).andReturn(reader);
		reader.readHeader();
		control.replay();
		
		Aqiterator<Tick> actual = manager.openReader(descr1, date);
		
		assertNotNull(actual);
		assertSame(reader, actual);
	}
	
	@Test
	public void testOpenReader_Raw() throws Exception {
		LocalDate date = new LocalDate(2001, 1, 29);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(descr1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(descr1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(false);
		expect(file2.exists()).andReturn(true);
		expect(helper.createInputStream(file2)).andReturn(inputStream);
		expect(helper.createCsvTickReader(inputStream, date)).andReturn(reader);
		reader.readHeader();
		control.replay();
		
		Aqiterator<Tick> actual = manager.openReader(descr1, date);
		
		assertNotNull(actual);
		assertSame(reader, actual);
	}
	
	@Test (expected=IOException.class)
	public void testOpenReader_NoSegmentFile() throws Exception {
		LocalDate date = new LocalDate(2001, 1, 29);
		CsvTickReader reader = control.createMock(CsvTickReader.class);
		expect(helper.getFile(descr1, date, ".csv.gz")).andReturn(file1);
		expect(helper.getFile(descr1, date, ".csv")).andReturn(file2);
		expect(file1.exists()).andReturn(false);
		expect(file2.exists()).andReturn(false);
		control.replay();
		
		manager.openReader(descr1, date);
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
		list.add(new LocalDate());
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		assertTrue(manager.isDataAvailable(descr1));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable1_No() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		assertFalse(manager.isDataAvailable(descr1));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable2_Yes() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(2015, 8, 24));
		list.add(new LocalDate(2015, 8, 25));
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		assertTrue(manager.isDataAvailable(descr1, new LocalDate(2015, 8, 24)));
		
		control.verify();
	}
	
	@Test
	public void testIsDataAvailable2_No() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(2015, 8, 24));
		list.add(new LocalDate(2015, 8, 25));
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		assertFalse(manager.isDataAvailable(descr1, new LocalDate(2015, 8, 23)));
		
		control.verify();
	}
	
	@Test
	public void testGetDateOfFirstSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(2015, 8, 24));
		list.add(new LocalDate(2015, 8, 25));
		list.add(new LocalDate(2015, 8, 26));
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		LocalDate expected = new LocalDate(2015, 8, 24),
				actual = manager.getDateOfFirstSegment(descr1);
		
		assertEquals(expected, actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfFirstSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		LocalDate actual = manager.getDateOfFirstSegment(descr1);
		
		assertNull(actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfLastSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(2011, 9, 1));
		list.add(new LocalDate(2012, 1, 1));
		list.add(new LocalDate(2013, 2, 1));
		list.add(new LocalDate(2014, 3, 1));
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		LocalDate expected = new LocalDate(2014, 3, 1),
				actual = manager.getDateOfLastSegment(descr1);
		
		assertEquals(expected, actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfLastSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		LocalDate actual = manager.getDateOfLastSegment(descr1);
		
		assertNull(actual);
		control.verify();
	}
	
	@Test
	public void testGetDateOfNextSegment() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(1998,  8,  1));
		list.add(new LocalDate(1999, 11,  1));
		list.add(new LocalDate(1999, 12, 29));
		list.add(new LocalDate(1999, 12, 30));
		list.add(new LocalDate(1999, 12, 31));
		list.add(new LocalDate(2003,  8,  1));
		list.add(new LocalDate(2004,  6, 10));
		list.add(new LocalDate(2004,  6, 11));
		list.add(new LocalDate(2004,  6, 12));
		list.add(new LocalDate(2005,  6, 10));
		expect(helper.getAvailableDataSegments(descr1)).andStubReturn(list);
		control.replay();
		
		LocalDate expected1 = new LocalDate(2004, 6, 12),
			expected2 = new LocalDate(1999, 12, 29),
			actual1 = manager.getDateOfNextSegment(descr1, new LocalDate(2004, 6, 11)),
			actual2 = manager.getDateOfNextSegment(descr1, new LocalDate(1999, 11, 1));
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
		control.verify();
	}
	
	@Test
	public void testGetDateOfNextSegment_NoDataAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(helper.getAvailableDataSegments(descr1)).andStubReturn(list);
		control.replay();
		
		assertNull(manager.getDateOfNextSegment(descr1, new LocalDate(1999, 12, 1)));
		assertNull(manager.getDateOfNextSegment(descr1, new LocalDate(2004, 6, 11)));
		
		control.verify();
	}
	
	@Test
	public void testGetSegmentList() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(1999, 11, 2));
		expect(helper.getAvailableDataSegments(descr1)).andReturn(list);
		control.replay();
		
		assertSame(list, manager.getSegmentList(descr1));
		
		control.verify();
	}

}
