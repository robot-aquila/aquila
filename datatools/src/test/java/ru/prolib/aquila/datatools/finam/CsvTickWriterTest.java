package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.easymock.IMocksControl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.data.Tick;

public class CsvTickWriterTest {
	private static final SecurityDescriptor descr;
	private static final DateTimeFormatter df;
	
	static {
		descr = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		df = DateTimeFormat.forPattern("yyy-MM-dd HH:mm:ss.SSS");
	}
	
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private File file;
	private FileOutputStream output;
	private CsvTickWriter writer;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		file = File.createTempFile("finam-tick-writer-test-", ".csv");
		file.deleteOnExit();
		output = new FileOutputStream(file);
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr);
		security.setPrecision(2);
		security.setMinStepSize(0.01d);
		writer = new CsvTickWriter(security, output);
	}
	
	private Tick createTick(String time, double price, long qty) {
		return new Tick(df.parseDateTime(time), price, new Double(qty));
	}
	
	@Test
	public void testWrite() throws Exception {
		writer.writeHeader();
		writer.write(createTick("2015-05-09 17:44:50.111", 76.03112, 10));
		writer.write(createTick("2015-05-09 18:12:44.015", 76.15010, 20));
		writer.write(createTick("2015-05-09 18:15:00.999", 76.13001, 25));
		writer.close();
		
		List<String> expected = new Vector<String>();
		expected.add("<TIME>,<LAST>,<VOL>,<MILLISECONDS>");
		expected.add("174450,76.03,10,111");
		expected.add("181244,76.15,20,15");
		expected.add("181500,76.13,25,999");
		InputStream input = new FileInputStream(file);
		List<String> actual = IOUtils.readLines(input, Charset.defaultCharset());
		input.close();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWrite_CutDecimalsWhenPrecisionIsZero() throws Exception {
		security.setPrecision(0);
		security.setMinStepSize(10d);
		
		writer.write(createTick("2015-05-09 18:15:00.982", 120.131, 100));
		writer.write(createTick("2015-05-09 18:30:00.001", 125.923, 200));
		writer.close();
		
		List<String> expected = new Vector<String>();
		expected.add("181500,120,100,982");
		expected.add("183000,130,200,1");
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
		
		writer = new CsvTickWriter(security, output);
		writer.close();
		
		control.verify();
	}
	
	@Test
	public void testFlush() throws Exception {
		OutputStream output = control.createMock(OutputStream.class);
		output.flush();
		control.replay();
		
		writer = new CsvTickWriter(security, output);
		writer.flush();
		
		control.verify();
	}

}
