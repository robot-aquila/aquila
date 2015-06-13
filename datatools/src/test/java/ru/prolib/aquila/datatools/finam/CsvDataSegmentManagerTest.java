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
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriterImpl;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;

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
	private SmartFlushSetup flushSetup;
	private CsvDataSegmentManager segmentManager;
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
		
		segmentManager = new CsvDataSegmentManager(terminal, root);
		segmentManager.setSmartFlushExecutionPeriod(50);
		segmentManager.setFlushPeriod(250);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test (expected=GeneralException.class)
	public void testCtor_ThrowsIfDirectoryNotExists() throws Exception {
		new CsvDataSegmentManager(terminal, new File(root, "bergandabupkhta"));
	}
	
	@Test
	public void testOpenWriter() throws Exception {
		segmentManager = new CsvDataSegmentManager(terminal, helper);
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
		
		DataSegmentWriter dummy = segmentManager.openWriter(descr1, date);
		
		assertNotNull(dummy);
		DataSegmentWriterImpl x = (DataSegmentWriterImpl) dummy;
		assertSame(flusher, x.getWriter());
		assertEquals(descr1, x.getSecurityDescriptor());
		assertEquals(date, x.getDate());
		control.verify();
	}
	
	@Test
	public void testClose_TickWriter() throws Exception {
		segmentManager = new CsvDataSegmentManager(terminal, helper);
		LocalDate date = new LocalDate(2001, 9, 11);
		DataSegmentWriter writer = control.createMock(DataSegmentWriter.class);
		expect(writer.getDate()).andStubReturn(date);
		expect(writer.getSecurityDescriptor()).andStubReturn(descr1);
		writer.close();
		expect(helper.getFile(descr1, date, ".csv.part")).andReturn(file1);
		expect(helper.getFile(descr1, date, ".csv.gz.part")).andReturn(file2);
		expect(helper.createInputStream(file1)).andReturn(inputStream);
		expect(helper.createGzipOutputStream(file2)).andReturn(outputStream);
		helper.copyStream(inputStream, outputStream);
		inputStream.close();
		outputStream.close();
		expect(helper.getFile(descr1, date, ".csv.gz")).andReturn(file3);
		expect(file2.renameTo(file3)).andReturn(true);
		expect(file1.delete()).andReturn(true);
		control.replay();
		
		segmentManager.close(writer);
		
		control.verify();
	}
	
	@Test
	@Ignore
	public void test_() throws Exception {
		fail("TODO: incomplete");
	}

}
