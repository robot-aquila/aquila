package ru.prolib.aquila.web.utils.finam.segstor;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.PriceScaleDBImpl;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentNotExistsException;

public class FinalL1UpdateSegmentStorageTest {
	private static Symbol symbol1, symbol2, symbol3;
	private static File root;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("Eu-9.16");
		symbol2 = new Symbol("BR-3.17");
		symbol3 = new Symbol("RTS-12.17");
		root = new File("fixture");
	}
	
	private PriceScaleDBImpl scaleDB;
	private FinamL1UpdateSegmentStorage storage;

	@Before
	public void setUp() throws Exception {
		scaleDB = new PriceScaleDBImpl();
		scaleDB.setScale(symbol2, 2);
		scaleDB.setScale(symbol3, 0);
		storage = new FinamL1UpdateSegmentStorage(root, scaleDB);
	}
	
	@Test
	public void testGetZoneID() {
		assertEquals(ZoneId.of("Europe/Moscow"), storage.getZoneID());
	}
	
	@Test
	public void testListSymbols() throws Exception {
		Set<Symbol> expected = new HashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		
		Set<Symbol> actual = storage.listSymbols();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIsExists() throws Exception {
		assertFalse(storage.isExists(new SymbolDaily(symbol1, 2017, 10, 12)));
		assertTrue(storage.isExists(new SymbolDaily(symbol2, 2016, 9, 22)));
		assertTrue(storage.isExists(new SymbolDaily(symbol2, 2016, 10, 4)));
		assertFalse(storage.isExists(new SymbolDaily(symbol2, 2016, 10, 5)));
		assertFalse(storage.isExists(new SymbolDaily(symbol3, 2016, 1, 1)));
	}
	
	@Test
	public void testListDailySegments1_S() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 15));
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		expected.add(new SymbolDaily(symbol2, 2016, 10, 12));
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2);
		
		assertEquals(expected, actual);
		
		expected.clear();
		
		actual = storage.listDailySegments(symbol3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListDailySegments3_SDD() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 15));
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		expected.add(new SymbolDaily(symbol2, 2016, 10, 12));
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2,
				new DatePoint(2010,  1,  1), new DatePoint(2020,  1,  1));
		
		assertEquals(expected, actual);
		
		expected.clear();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		
		actual = storage.listDailySegments(symbol2,
				new DatePoint(2016,  9, 22), new DatePoint(2016, 10, 12));
		
		assertEquals(expected, actual);
		
		expected.clear();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 15));
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		
		actual = storage.listDailySegments(symbol2,
				new DatePoint(2016,  1,  1), new DatePoint(2016, 10, 12));
		
		assertEquals(expected, actual);

		expected.clear();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		expected.add(new SymbolDaily(symbol2, 2016, 10, 12));

		actual = storage.listDailySegments(symbol2,
				new DatePoint(2016,  9, 16), new DatePoint(2020,  1,  1));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListDailySegments2_SD() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		expected.add(new SymbolDaily(symbol2, 2016, 10, 12));
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2, new DatePoint(2016, 9, 23));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListDailySegments3_SDI() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2,
				new DatePoint(2016,  9, 20), 3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListDailySegments3_SID() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016,  9, 22));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2, 2, new DatePoint(2016, 10,  4));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListDailySegments2_SM() throws Exception {
		List<SymbolDaily> expected = new ArrayList<>();
		expected.add(new SymbolDaily(symbol2, 2016, 10,  1));
		expected.add(new SymbolDaily(symbol2, 2016, 10,  4));
		expected.add(new SymbolDaily(symbol2, 2016, 10, 12));

		List<SymbolDaily> actual = storage.listDailySegments(symbol2, new MonthPoint(2016, Month.OCTOBER));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetMetaData() throws Exception {
		SegmentMetaData actual = storage.getMetaData(new SymbolDaily(symbol2, 2016, 10, 4));
		
		String s = File.separator;
		assertEquals(root.getAbsolutePath() + s + "50" + s + "BR%2D3%2E17" + s + "2016" + s
				+ "10" + s + "BR%2D3%2E17-20161004.csv.gz", actual.getPath());
		assertEquals(3, actual.getNumberOfElements());
		Instant expectedUTime = Instant.ofEpochMilli(new File(actual.getPath()).lastModified());
		assertEquals(expectedUTime, actual.getUpdateTime());
		assertEquals("574CCF072D473E023324B404A45E7F52", actual.getHashCode());
	}
	
	@Test (expected=SymbolDailySegmentNotExistsException.class)
	public void testGetMetaData_ThrowsIfSegmentNotExists() throws Exception {
		storage.getMetaData(new SymbolDaily(symbol3, 2016, 10, 4));
	}
	
	@Test
	public void testCreateReader() throws Exception {
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol2).withTrade();
		List<L1Update> expected = new ArrayList<>();
		expected.add(builder.withPrice("52.16")
				.withTime("2016-10-04T07:43:10Z")
				.withSize(1L)
				.buildL1Update());
		expected.add(builder.withPrice("52.82")
				.withTime("2016-10-04T13:13:05Z")
				.withSize(1L)
				.buildL1Update());
		expected.add(builder.withPrice("52.97")
				.withTime("2016-10-04T13:25:12Z")
				.withSize(5L)
				.buildL1Update());
		
		List<L1Update> actual = new ArrayList<>();
		try ( CloseableIterator<L1Update> it = storage.createReader(new SymbolDaily(symbol2, 2016, 10, 4)) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		assertEquals(expected, actual);
	}

	@Test (expected=SymbolDailySegmentNotExistsException.class)
	public void testCreateReader_ThrowsIfSegmentNotExists() throws Exception {
		storage.createReader(new SymbolDaily(symbol3, 2016, 10, 4));
	}

}
