package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.data.Tick;

public class CsvTickWriterTest {
	private static final SecurityDescriptor descr;
	private static final DateTimeFormatter df;
	
	static {
		descr = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		df = DateTimeFormat.forPattern("yyy-MM-dd HH:mm:ss");
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
	private File file;
	private FileOutputStream output;
	private CsvTickWriter writer;

	@Before
	public void setUp() throws Exception {
		file = File.createTempFile("finam-tick-writer-test-", ".csv");
		file.deleteOnExit();
		output = new FileOutputStream(file);
		terminal = new TerminalBuilder().buildTerminal();
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
		writer.write(createTick("2015-05-09 17:44:50", 76.03112, 10));
		writer.write(createTick("2015-05-09 18:12:44", 76.15010, 20));
		writer.write(createTick("2015-05-09 18:15:00", 76.13001, 25));
		writer.close();
		
		String expected =
				"<DATE>,<TIME>,<LAST>,<VOL>\n" +
				"20150509,174450,76.03,10\n" +
				"20150509,181244,76.15,20\n" +
				"20150509,181500,76.13,25\n";
		InputStream input = new FileInputStream(file);
		String actual = IOUtils.readFully(input);
		input.close();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWrite_CutDecimalsWhenPrecisionIsZero() throws Exception {
		security.setPrecision(0);
		security.setMinStepSize(10d);
		
		writer.write(createTick("2015-05-09 18:15:00", 120.131, 100));
		writer.write(createTick("2015-05-09 18:30:00", 125.923, 200));
		writer.close();
		
		String expected =
				"20150509,181500,120,100\n" +
				"20150509,183000,130,200\n";
		InputStream input = new FileInputStream(file);
		String actual = IOUtils.readFully(input);
		input.close();
		assertEquals(expected, actual);
	}

}
