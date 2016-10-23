package ru.prolib.aquila.web.utils.finam.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

import com.csvreader.CsvReader;

public class FinamCsvL1UpdateReaderTest {
	private static final Symbol symbol = new Symbol("BR-3.17");
	private IMocksControl control;
	private CsvReader csvReaderMock;
	
	private FinamCsvL1UpdateReader reader;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		csvReaderMock = control.createMock(CsvReader.class);
	}
	
	@Test
	public void testClose() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, csvReaderMock);
		csvReaderMock.close();
		control.replay();
		
		reader.close();
		reader.close(); // multiple calls is safe
		
		control.verify();
	}
	
	@Test
	public void testIterate() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz");
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		List<L1Update> expected = new ArrayList<>();
		expected.add(new L1UpdateBuilder(symbol)
				.withTime("2016-10-17T10:39:31Z")
				.withTrade()
				.withPrice(53.67d)
				.withSize(1)
				.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T11:09:44Z")
			.withTrade()
			.withPrice(53.75d)
			.withSize(1)
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:10:52Z")
			.withTrade()
			.withPrice(53.41d)
			.withSize(1)
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:17:10Z")
			.withTrade()
			.withPrice(53.52d)
			.withSize(1)
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:45:45Z")
			.withTrade()
			.withPrice(53.13d)
			.withSize(1)
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T16:52:22Z")
			.withTrade()
			.withPrice(53.25d)
			.withSize(1)
			.buildL1Update());
		assertEquals(expected, actual);
	}

	@Test
	public void testIterate_EmptyFile() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161016-empty.csv.gz");
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		List<L1Update> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz");
		reader.close();
		
		reader.next();
	}
	
	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfNoElement() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz");
		
		reader.item();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz");
		reader.close();
		
		reader.item();		
	}
	

}
