package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;

public class CsvDataSegmentManagerTest {
	private static final File root;
	private static final SecurityDescriptor descr1/*, descr2*/;

	static {
		root = new File("fixture", "finam-tests");
		descr1 = new SecurityDescriptor("RTS", "SPB", "USD", SecurityType.FUT);
		//descr2 = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
	private CsvDataSegmentManager segmentManager;
	
	@Before
	public void setUp() throws Exception {
		root.mkdirs();
		terminal = new TerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr1);
		security.setPrecision(2);
		security.setMinStepSize(0.01d);
		segmentManager = new CsvDataSegmentManager(terminal, root);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testNewSegment() throws Exception {
		DateTime time = new DateTime(2015, 5, 13, 9, 30, 0);
		DataSegmentWriter writer = segmentManager.open(descr1, time.toLocalDate());
		writer.write(new Tick(time, 100.299d, 100));
		writer.write(new Tick(time.plusMinutes(15), 105.456d, 120));
		writer.write(new Tick(time.plusMinutes(30), 106.112d, 135));
		segmentManager.close(writer);

		InputStream is = new GZIPInputStream(new BufferedInputStream(
			new FileInputStream(new File(root,
				"RTS-SPB-USD-FUT/2015/05/RTS-SPB-USD-FUT-20150513.csv.gz"))));
		List<String> actual = IOUtils.readLines(is, Charset.defaultCharset());
		is.close();
		List<String> expected = new Vector<String>();
		expected.add("<DATE>,<TIME>,<LAST>,<VOL>");
		expected.add("20150513,093000,100.30,100");
		expected.add("20150513,094500,105.46,120");
		expected.add("20150513,100000,106.11,135");
		assertEquals(expected, actual);
	}
	
	@Test (expected=GeneralException.class)
	public void testCtor_ThrowsIfDirectoryNotExists() throws Exception {
		new CsvDataSegmentManager(terminal, new File(root, "bergandabupkhta"));
	}

}
