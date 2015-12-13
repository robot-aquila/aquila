package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushTickWriter;

public class IOHelperTest {
	private static final File root;
	private static final Symbol symbol;
	private static final IdUtils idUtils;
	
	static {
		root = new File("fixture", "finam-tests");
		symbol = new Symbol("Si-6.15","SPB","USD",SymbolType.FUTURE);
		idUtils = new IdUtils();
	}
	
	private IOHelper helper;
	private IMocksControl control;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		root.mkdirs();
		helper = new IOHelper(root);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	private InputStream createTestInput() {
		return createTestInput("<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
			+ "100000,100.02,105,957\n"
			+ "100001,100.18,102,13\n"
			+ "100002,100.96,238,512\n");
	}
	
	private InputStream createTestInput(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}
	
	@Test
	public void testCtor1() {
		assertNotNull(helper.getScheduler());
		assertNotNull(helper.getFlushSetup());
		assertTrue(helper.getScheduler() instanceof SchedulerLocal);
	}
	
	@Test
	public void testCreateOutputStream_OverrideExisting() throws Exception {
		File file = new File(root, "xxx");
		
		OutputStream os = helper.createOutputStream(file, false);
		
		assertNotNull(os);
		os.write("Hello, World!".getBytes());
		os.close();
		String actual = FileUtils.readFileToString(file);
		assertEquals("Hello, World!", actual);
	}
	
	@Test
	public void testCreateOutputStream_AppendExisting() throws Exception {
		File file = new File(root, "yyy");
		FileUtils.writeStringToFile(file, "Last hope");
		
		OutputStream os = helper.createOutputStream(file, true);
		
		assertNotNull(os);
		os.write(" from the dark side".getBytes());
		os.close();
		String actual = FileUtils.readFileToString(file);
		assertEquals("Last hope from the dark side", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateOutputStream_ThrowsIfFileNotExists()
			throws Exception
	{
		File file = new File(new File(root, "zulu24"), "xxx");
		
		helper.createOutputStream(file, true);
	}
	
	@Test
	public void testCreateGzipOutputStream() throws Exception {
		File file = new File(root, "zzz.gz");
		OutputStream os = helper.createGzipOutputStream(file);
		assertNotNull(os);
		os.write("Help with vocabulary".getBytes());
		os.close();
		
		InputStream is = new GZIPInputStream(new FileInputStream(file));
		
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Help with vocabulary", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateGzipOutputStream_ThrowsIfFileNotExists()
			throws Exception
	{
		File file = new File(new File(root, "charlie"), "xxx");
		
		helper.createGzipOutputStream(file);
	}
	
	@Test
	public void testCreateInputStream() throws Exception {
		File file = new File(root, "test.txt");
		FileUtils.writeStringToFile(file, "Alien encounters");
		
		InputStream is = helper.createInputStream(file);
		
		assertNotNull(is);
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Alien encounters", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateInputStream_ThrowsIfFileNotExists() throws Exception {
		File file = new File(new File(root, "beta"), "foo.txt");
		helper.createInputStream(file);
	}

	@Test
	public void testCreateGzipInputStream() throws Exception {
		File file = new File(root, "test.txt.gz");
		OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
		os.write("Look at the picture".getBytes());
		os.close();
		
		InputStream is = helper.createGzipInputStream(file);
		
		assertNotNull(is);
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Look at the picture", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateGzipInputStream_ThrowsIfFileNotExists() throws Exception {
		File file = new File(new File(root, "gamma"), "bar.csv.gz");
		helper.createGzipInputStream(file);
	}
	
	@Test
	public void testGetFile() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-F"
				+ File.separator + "2015" + File.separator + "10"
				+ File.separator + "Si%2D6%2E15-SPB-USD-F-20151013.xx");
		
		File actual = helper.getFile(symbol, new LocalDate(2015, 10, 13), ".xx");
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetRootDir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-F");
		
		File actual = helper.getRootDir(symbol);
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLeve1Dir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-F" + File.separator + "2015");
		
		File actual = helper.getLevel1Dir(symbol, new LocalDate(2015, 10, 13));
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLevel2Dir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-F" +
			File.separator + "2015" + File.separator + "10");
		
		File actual = helper.getLevel2Dir(symbol, new LocalDate(2015, 10, 13));
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCopyStream() throws Exception {
		String expected = "Matching engine for startup stock";
		File src = new File(root, "input.txt");
		File dst = new File(root, "output.txt");
		FileUtils.writeStringToFile(src, expected);
		InputStream input = new FileInputStream(src);
		OutputStream output = new FileOutputStream(dst);
		
		helper.copyStream(input, output);

		input.close();
		output.close();
		input = new FileInputStream(dst);
		String actual = IOUtils.toString(input);
		input.close();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateCsvTickWriter() throws Exception {
		File file = new File(root, "data.csv");
		OutputStream output = new FileOutputStream(file);
		
		CsvTickWriter writer = helper.createCsvTickWriter(output);
		
		assertNotNull(writer);
		writer.writeHeader();
		writer.write(new Tick(new DateTime(2014, 1, 1, 20, 30, 55, 30), 2d, 5));
		writer.close();
		List<String> expected = new Vector<String>();
		expected.add("<TIME>,<LAST>,<VOL>,<MILLISECONDS>");
		expected.add("203055,2.0,5,30");
		InputStream input = new FileInputStream(file);
		List<String> actual = IOUtils.readLines(input, Charset.defaultCharset());
		input.close();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddSmartFlush() throws Exception {
		TickWriter writer = control.createMock(TickWriter.class);
		
		TickWriter dummy = helper.addSmartFlush(writer, "zulu24");
		
		assertNotNull(dummy);
		SmartFlushTickWriter flusher = (SmartFlushTickWriter) dummy;
		assertSame(helper.getScheduler(), flusher.getScheduler());
		assertSame(helper.getFlushSetup(), flusher.getSetup());
		assertSame(writer, flusher.getTickWriter());
	}
	
	@Test
	public void testCreateCsvTickReader() throws Exception {
		LocalDate date = new LocalDate(2015, 7, 1);
		CsvTickReader reader = helper.createCsvTickReader(createTestInput(), date);
		assertNotNull(reader);
		reader.readHeader();
		reader.next();
		Tick expected = new Tick(new DateTime(2015, 7, 1, 10, 0, 0, 957), 100.02, 105);
		assertEquals(expected, reader.item());
	}
	
	@Test
	public void testGetAvailableDataSegments_() throws Exception {
		String prefix = idUtils.getSafeId(symbol);
		String fs = File.separator; 
		File dir = new File(root, prefix + fs + "2010" + fs + "01");
		dir.mkdirs();
		FileUtils.touch(new File(dir, "badfile.csv"));
		FileUtils.touch(new File(dir, "badfile.csv.gz"));
		FileUtils.touch(new File(dir, prefix + "-20100103.csv.gz"));
		FileUtils.touch(new File(dir, prefix + "-20100102.csv"));
		FileUtils.touch(new File(dir, prefix + "-20100101.csv"));
		
		// this directory and it's content should be ignored
		dir = new File(dir, "subdir");
		dir.mkdirs();
		FileUtils.touch(new File(dir, prefix + "-20100104.csv.gz"));
		FileUtils.touch(new File(dir, prefix + "-20100105.csv"));
		
		dir = new File(root, prefix + fs + "2010" + fs + "05");
		dir.mkdirs();
		FileUtils.touch(new File(dir, prefix + "-20100501.csv")); // should be included once
		FileUtils.touch(new File(dir, prefix + "-20100501.csv.gz"));
		
		dir = new File(root, prefix + fs + "2011");
		dir.mkdirs();
		FileUtils.touch(new File(dir, prefix + "-20110101.csv")); // to ignore
		
		dir = new File(root, prefix + fs + "2011" + fs + "01");
		dir.mkdirs();
		FileUtils.touch(new File(dir, prefix + "-20110101.csv"));
		FileUtils.touch(new File(dir, prefix + "-20110155.csv"));
		FileUtils.touch(new File(dir, prefix + "-20112201.csv"));
		FileUtils.touch(new File(dir, prefix + "-20110102badname.csv"));
		FileUtils.touch(new File(dir, prefix + "-bad.csv"));
		
		List<LocalDate> expected = new Vector<LocalDate>();
		expected.add(new LocalDate(2010, 1, 1));
		expected.add(new LocalDate(2010, 1, 2));
		expected.add(new LocalDate(2010, 1, 3));
		expected.add(new LocalDate(2010, 5, 1));
		expected.add(new LocalDate(2011, 1, 1));
		
		List<LocalDate> actual = helper.getAvailableDataSegments(symbol);
		
		assertEquals(expected, actual);
	}

}
