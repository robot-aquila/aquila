package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;

@SuppressWarnings("unused")
public class CsvDataSegmentManagerTest {
	private static final Logger logger;
	private static final File root;
	private static final SecurityDescriptor descr1/*, descr2*/;

	static {
		logger = LoggerFactory.getLogger(CsvDataSegmentManagerTest.class);
		root = new File("fixture", "finam-tests");
		descr1 = new SecurityDescriptor("RTS", "SPB", "USD", SecurityType.FUT);
		//descr2 = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
	private Scheduler scheduler;
	private SmartFlushSetup flushSetup;
	private CsvDataSegmentManager segmentManager;
	
	@Before
	public void setUp() throws Exception {
		root.mkdirs();
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr1);
		security.setPrecision(2);
		security.setMinStepSize(0.01d);
		scheduler = new SchedulerLocal();
		flushSetup = new SmartFlushSetup();
		flushSetup.setExecutionPeriod(50);
		flushSetup.setFlushPeriod(250);
		segmentManager = new CsvDataSegmentManager(terminal, root,
				scheduler, flushSetup);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	private InputStream createStream(String filename) throws IOException {
		return new GZIPInputStream(new BufferedInputStream(
			new FileInputStream(new File(root, filename))));
	}
	
	@Test
	public void testWriterSegment() throws Exception {
		DateTime time = new DateTime(2015, 5, 13, 9, 30, 0);
		DataSegmentWriter writer = segmentManager.open(descr1, time.toLocalDate());
		writer.write(new Tick(time, 100.299d, 100));
		writer.write(new Tick(time.plusMinutes(15), 105.456d, 120));
		writer.write(new Tick(time.plusMinutes(30), 106.112d, 135));
		segmentManager.close(writer);

		InputStream is = createStream("RTS-SPB-USD-FUT/2015/05/RTS-SPB-USD-FUT-20150513.csv.gz");
		List<String> actual = IOUtils.readLines(is, Charset.defaultCharset());
		is.close();
		List<String> expected = new Vector<String>();
		expected.add("<TIME>,<LAST>,<VOL>,<MILLISECONDS>");
		expected.add("093000,100.30,100,0");
		expected.add("094500,105.46,120,0");
		expected.add("100000,106.11,135,0");
		assertEquals(expected, actual);
	}
	
	@Test (expected=GeneralException.class)
	public void testCtor_ThrowsIfDirectoryNotExists() throws Exception {
		new CsvDataSegmentManager(terminal,
				new File(root, "bergandabupkhta"), scheduler, flushSetup);
	}
	
	@Test
	public void testSmartFlush() throws Exception {
		File file = new File(root, "RTS-SPB-USD-FUT/2015/06/RTS-SPB-USD-FUT-20150610.csv.gz");
		DateTime time = new DateTime(2015, 6, 10, 9, 30, 0);
		DataSegmentWriter writer = segmentManager.open(descr1, time.toLocalDate());
		long length = file.length();
		writer.write(new Tick(time, 80.24d, 10));
		writer.write(new Tick(time.plus(20), 82.85d, 10));
		Thread.sleep(100);
		assertEquals(length, file.length());
		writer.write(new Tick(time.plus(120), 83.42d, 1));
		Thread.sleep(100);
		assertEquals(length, file.length());
		writer.write(new Tick(time.plus(200), 79.11d, 1));
		Thread.sleep(350);
		assertNotEquals(length, file.length());
		segmentManager.close(writer);
	}

}
