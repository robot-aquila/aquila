package ru.prolib.aquila.web.utils.finam.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

import com.csvreader.CsvReader;

public class FinamCsvL1UpdateReaderTest {
	private static final Symbol symbol = new Symbol("BR-3.17");
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
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
		reader = new FinamCsvL1UpdateReader(symbol, csvReaderMock, 2);
		csvReaderMock.close();
		control.replay();
		
		reader.close();
		reader.close(); // multiple calls is safe
		
		control.verify();
	}
	
	@Test
	public void testIterate() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz", 2);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		List<L1Update> expected = new ArrayList<>();
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T10:39:31Z")
			.withTrade()
			.withPrice("53.67")
			.withSize(1)
			.withComment("20161017133931#0000000001") // is MSK TZ
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T11:09:44Z")
			.withTrade()
			.withPrice("53.75")
			.withSize(1)
			.withComment("20161017140944#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:10:52Z")
			.withTrade()
			.withPrice("53.41")
			.withSize(1)
			.withComment("20161017161052#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:17:10Z")
			.withTrade()
			.withPrice("53.52")
			.withSize(1)
			.withComment("20161017161710#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T13:45:45Z")
			.withTrade()
			.withPrice("53.13")
			.withComment("20161017164545#0000000001")
			.withSize(1)
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T16:52:22Z")
			.withTrade()
			.withPrice("53.25")
			.withComment("20161017195222#0000000001")
			.withSize(1)
			.buildL1Update());
		assertEquals(expected, actual);
	}

	@Test
	public void testIterate_EmptyFile() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161016-empty.csv.gz", 2);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		List<L1Update> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz", 2);
		reader.close();
		
		reader.next();
	}
	
	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfNoElement() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz", 2);
		
		reader.item();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/br-3.17-20161017.csv.gz", 2);
		reader.close();
		
		reader.item();		
	}
	
	@Test
	public void testIterate_TimeErrors() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamCsvL1UpdateReader(symbol, "fixture/_x/br-3.17-20161018.csv.gz", 2);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();

		List<L1Update> expected = new ArrayList<>();
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:39:31Z")
			.withTrade()
			.withPrice("53.67")
			.withSize(1)
			.withComment("20161018133931#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:39:31Z")
			.withTrade()
			.withPrice("53.68")
			.withSize(2)
			.withComment("20161018133931#0000000002")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:39:31Z")
			.withTrade()
			.withPrice("53.50")
			.withSize(5)
			.withComment("20161018133931#0000000003")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:39:00Z")
			.withTrade()
			.withPrice("51.95")
			.withSize(2)
			.withComment("20161018133900#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:39:44Z")
			.withTrade()
			.withPrice("53.75")
			.withSize(7)
			.withComment("20161018133944#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-17T10:39:00Z")
			.withTrade()
			.withPrice("53.74")
			.withSize(1)
			.withComment("20161017133900#0000000001")
			.buildL1Update());
		expected.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T13:10:52Z")
			.withTrade()
			.withPrice("53.41")
			.withSize(1)
			.withComment("20161018161052#0000000001")
			.buildL1Update());
		assertEquals(expected, actual);
		assertEquals(2, reader.getTimeErrorCount());
	}

}
