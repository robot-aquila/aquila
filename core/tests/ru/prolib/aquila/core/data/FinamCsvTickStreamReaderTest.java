package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import com.csvreader.CsvReader;

public class FinamCsvTickStreamReaderTest {
	private static final Vector<Tick> expected = new Vector<Tick>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d, 250d));
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d,   5d));
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d,  10d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,0,0),144.98d, 1.54d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,0,0),144.80d, 500d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,1,0),144.70d, 300d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,1,0),144.70d, 1.4d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,2,0),144.64d, 80d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,2,0),144.70d, 3d));
	}
	
	private IMocksControl control;
	private CsvReader csv;
	private FinamCsvTickStreamReader reader;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		csv = control.createMock(CsvReader.class);
		reader = new FinamCsvTickStreamReader(csv);
	}
	
	@Test
	public void testRead() throws Exception {
		csv = new CsvReader("fixture/GAZP_ticks.csv");
		csv.readHeaders();
		reader = new FinamCsvTickStreamReader(csv);
		
		Tick t = null;
		Vector<Tick> actual = new Vector<Tick>();
		while ( (t = reader.read()) != null ) {
			actual.add(t);
		}
		reader.close();
		
		assertEquals(expected, actual);
	}

	@Test
	public void testClose() {
		csv.close();
		control.replay();
		
		reader.close();
		
		control.verify();
	}

}
