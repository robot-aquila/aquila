package ru.prolib.aquila.ta.ds.csv;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ta.ds.DataSetIteratorEmpty;
import ru.prolib.aquila.ta.ds.DataSetIteratorLimit;

import com.csvreader.CsvReader;

public class MarketDataReaderCsvTest {
	IMocksControl control;
	CsvReader csv;
	MarketDataReaderCsv reader;

	@Before
	public void setUp() throws Exception {
		csv = CsvReader.parse("one,two,three\n1,2,3");
		reader = new MarketDataReaderCsv(csv);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(csv, reader.getCsvReader());
	}
	
	@Test
	public void testPrepare_ReturnEmpty() throws Exception {
		DataSetIteratorEmpty iterator = (DataSetIteratorEmpty) reader.prepare();
		assertNotNull(iterator);
	}
	
	@Test
	public void testUpdate() throws Exception {
		reader.prepare();
		DataSetIteratorLimit limit = (DataSetIteratorLimit) reader.update();
		assertEquals(1, limit.getLimit());
		DataSetIteratorCsv iterator =
			(DataSetIteratorCsv) limit.getDataSetIterator(); 
		assertNotNull(iterator);
		assertSame(csv, iterator.getCsvReader());
	}

}
