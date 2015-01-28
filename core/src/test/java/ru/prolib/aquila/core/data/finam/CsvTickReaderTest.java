package ru.prolib.aquila.core.data.finam;

import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.TickReader_FunctionalTest;
import ru.prolib.aquila.core.data.finam.CsvTickReader;

import com.csvreader.CsvReader;

public class CsvTickReaderTest {
	private IMocksControl control;
	private CsvReader csv;
	private CsvTickReader reader;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		csv = control.createMock(CsvReader.class);
		reader = new CsvTickReader(csv);
	}
	
	private CsvTickReader createReader() throws Exception {
		CsvReader csv = new CsvReader("fixture/GAZP_ticks.csv");
		csv.readHeaders();
		return new CsvTickReader(csv);
	}
	
	@Test
	public void testContent() throws Exception {
		new TickReader_FunctionalTest().testStreamContent(createReader());
	}

	@Test
	public void testClose() {
		csv.close();
		control.replay();
		
		reader.close();
		
		control.verify();
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfBeforeStart() throws Exception {
		reader = createReader();
		reader.item();
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfAfterEnd() throws Exception {
		reader = createReader();
		while ( reader.next() ) { }
		reader.item();
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfClosed() throws Exception {
		reader = createReader();
		reader.next();
		reader.close();
		reader.item();
	}

}
