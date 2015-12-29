package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Tick;

public class CsvTickWriterTest {
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss.SSS");
	}
	
	private IMocksControl control;
	private File file;
	private FileOutputStream output;
	private CsvTickWriter writer;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		file = File.createTempFile("finam-tick-writer-test-", ".csv");
		file.deleteOnExit();
		output = new FileOutputStream(file);
		writer = new CsvTickWriter(output);
	}
	
	private Tick createTick(String time, double price, long qty) {
		return new Tick(LocalDateTime.parse(time, df), price, new Double(qty));
	}
	
	@Test
	public void testWrite() throws Exception {
		writer.writeHeader();
		writer.write(createTick("2015-05-09 17:44:50.111", 76.03112, 10));
		writer.write(createTick("2015-05-09 18:12:44.015", 76.1501,  20));
		writer.write(createTick("2015-05-09 18:15:00.999", 76.13,    25));
		writer.close();
		
		List<String> expected = new Vector<String>();
		expected.add("<TIME>,<LAST>,<VOL>,<MILLISECONDS>");
		expected.add("174450,76.03112,10,111");
		expected.add("181244,76.1501,20,15");
		expected.add("181500,76.13,25,999");
		InputStream input = new FileInputStream(file);
		List<String> actual = IOUtils.readLines(input, Charset.defaultCharset());
		input.close();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClose() throws Exception {
		OutputStream output = control.createMock(OutputStream.class);
		output.close();
		control.replay();
		
		writer = new CsvTickWriter(output);
		writer.close();
		
		control.verify();
	}
	
	@Test
	public void testFlush() throws Exception {
		OutputStream output = control.createMock(OutputStream.class);
		output.flush();
		control.replay();
		
		writer = new CsvTickWriter(output);
		writer.flush();
		
		control.verify();
	}

}
