package ru.prolib.aquila.core.data.finam;

import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.junit.*;
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
	
	@Test
	public void testRead2() throws Exception {
		csv = new CsvReader("fixture/GAZP_ticks.csv");
		csv.readHeaders();
		new TickReader_FunctionalTest().testStreamContent(new CsvTickReader(csv));
	}

	@Test
	public void testClose() {
		csv.close();
		control.replay();
		
		reader.close();
		
		control.verify();
	}

}
