package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.DailySegmentsCombiner;
import ru.prolib.aquila.data.storage.ohlcv.segstor.IntradayMDStorageOverSDSS;
import ru.prolib.aquila.data.storage.ohlcv.utils.LimitedAmountIterator;
import ru.prolib.aquila.data.storage.ohlcv.utils.PreciseTimeLimitsIterator;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class IntradayMDStorageOverSDSSTest {
	private static final Symbol
		symbol1 = new Symbol("MSFT"),
		symbol2 = new Symbol("AAPL"),
		symbol3 = new Symbol("GAZP");
	private static final TFSymbol
		tsymbol1 = new TFSymbol(symbol1, TimeFrame.M1),
		tsymbol2 = new TFSymbol(symbol2, TimeFrame.M1),
		tsymbol3 = new TFSymbol(symbol3, TimeFrame.M1);
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Interval IM1(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(1));
	}
	
	static Interval IM5(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(5));
	}
	
	private IMocksControl control;
	private SymbolDailySegmentStorage<Candle> segstorMock;
	private List<Candle> fixture;
	private CloseableIteratorStub<Candle> iteratorStub1, iteratorStub2, iteratorStub3;
	private IntradayMDStorageOverSDSS storage;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		segstorMock = control.createMock(SymbolDailySegmentStorage.class);
		storage = new IntradayMDStorageOverSDSS(segstorMock, ZoneId.of("Europe/Moscow"), TimeFrame.M1);
		iteratorStub1 = new CloseableIteratorStub<>();
		iteratorStub2 = new CloseableIteratorStub<>();
		iteratorStub3 = new CloseableIteratorStub<>();
		fixture = new ArrayList<>();
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfNotIntraday() throws Exception {
		storage = new IntradayMDStorageOverSDSS(segstorMock, ZoneId.of("Europe/Moscow"), TimeFrame.D1);
	}
	
	@Test
	public void testGetKeys() throws Exception {
		Set<Symbol> symbols = new HashSet<>();
		symbols.add(symbol1);
		symbols.add(symbol2);
		symbols.add(symbol3);
		expect(segstorMock.listSymbols()).andReturn(symbols);
		control.replay();
		Set<TFSymbol> actual, expected = new HashSet<>();
		
		actual = storage.getKeys();
		
		expected.add(tsymbol1);
		expected.add(tsymbol2);
		expected.add(tsymbol3);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_1K() throws Exception {
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2012, 10, 24));
		segments.add(new SymbolDaily(symbol1, 2013,  1,  1));
		segments.add(new SymbolDaily(symbol1, 2014, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = new DailySegmentsCombiner(segstorMock, segments);
		
		actual = storage.createReader(tsymbol1);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_1K_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1)).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = storage.createReader(tsymbol1);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_1K_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReader(new TFSymbol(symbol1, TimeFrame.M10));
	}
	
	@Test
	public void testCreateReaderFrom_2KT() throws Exception {
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 15))).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = 
				new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstorMock, segments),
						T("2017-09-14T21:05:00Z"), null);
		
		actual = storage.createReaderFrom(tsymbol1, T("2017-09-14T21:05:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReaderFrom_2KT_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 15)))
			.andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = storage.createReaderFrom(tsymbol1, T("2017-09-14T21:05:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReaderFrom_2KT_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReaderFrom(new TFSymbol(symbol1, TimeFrame.M15), Instant.now());
	}
	
	@Test
	public void testCreateReader_3KTI() throws Exception {
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 15))).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = new LimitedAmountIterator(
			new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstorMock, segments),
						T("2017-09-14T21:05:00Z"), null), 25);
		
		actual = storage.createReader(tsymbol1, T("2017-09-14T21:05:00Z"), 25);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KTI_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 14))).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = storage.createReader(tsymbol1, T("2017-09-14T20:00:00Z"), 25);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KTI_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReader(new TFSymbol(symbol1, TimeFrame.D1), T("2017-09-14T20:00:00Z"), 25);
	}
	
	@Test
	public void testCreateReader_3KTT() throws Exception {
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 15),
				new DatePoint(2018, 5, 10))).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = 
				new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstorMock, segments),
						T("2017-09-14T21:05:00Z"), T("2018-05-10T18:56:00Z"));
		
		actual = storage.createReader(tsymbol1, T("2017-09-14T21:05:00Z"), T("2018-05-10T18:56:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_3KTT_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1, new DatePoint(2017, 9, 15),
				new DatePoint(2018, 5, 10))).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();

		actual = storage.createReader(tsymbol1, T("2017-09-14T21:05:00Z"), T("2018-05-10T18:56:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KTT_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReader(new TFSymbol(symbol1, TimeFrame.D1),
				T("2017-09-14T21:05:00Z"), T("2018-05-10T18:56:00Z"));
	}
	
	private void testCreateReader_3KIT_FillStubIterators() {
		fixture.add(new Candle(IM1("2017-09-15T18:01:00Z"), 150, 156, 150, 155, 1700));
		fixture.add(new Candle(IM1("2017-09-15T18:02:00Z"), 155, 155, 149, 151, 1800));
		fixture.add(new Candle(IM1("2017-09-15T18:03:00Z"), 151, 152, 145, 148, 1900));
		fixture.add(new Candle(IM1("2017-10-01T12:01:00Z"), 143, 145, 140, 142, 5000));
		fixture.add(new Candle(IM1("2017-10-01T12:02:00Z"), 142, 149, 141, 145, 1500));
		fixture.add(new Candle(IM1("2017-10-01T12:03:00Z"), 145, 152, 145, 150, 1700));
		fixture.add(new Candle(IM1("2017-12-31T10:00:00Z"), 150, 152, 148, 151, 1000));
		fixture.add(new Candle(IM1("2017-12-31T10:01:00Z"), 151, 156, 150, 155, 2000));
		fixture.add(new Candle(IM1("2017-12-31T10:02:00Z"), 155, 158, 152, 156, 3000));
		iteratorStub1 = new CloseableIteratorStub<>(fixture.subList(0, 3));
		iteratorStub2 = new CloseableIteratorStub<>(fixture.subList(3, 6));
		iteratorStub3 = new CloseableIteratorStub<>(fixture.subList(6, 9));
	}
	
	@Test
	public void testCreateReader_3KIT_IfNoSegmentsAfterSpecifiedTime() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 12, 31))).andReturn(iteratorStub3);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 10,  1))).andReturn(iteratorStub2);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017,  9, 15))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result = storage.createReader(tsymbol1, 5, T("2017-12-31T10:01:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(2, 7));
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_3KIT_IfHasSegmentsAfterSpecifiedTime() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 10,  1))).andReturn(iteratorStub2);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017,  9, 15))).andReturn(iteratorStub1);
		control.replay();
		
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result = storage.createReader(tsymbol1, 4, T("2017-10-01T12:03:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(1, 5));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_IfLastAvailableSegmentBeforeSpecifiedTime() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 12, 31))).andReturn(iteratorStub3);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 10,  1))).andReturn(iteratorStub2);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result = storage.createReader(tsymbol1, 5, T("2018-06-01T00:00:00Z"));

		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(4, 9));
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_3KIT_IfNotEnoughValuesBeforeSpecifiedTime() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017, 10,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017, 10,  1))).andReturn(iteratorStub2);
		expect(segstorMock.createReader(new SymbolDaily(symbol1, 2017,  9, 15))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result = storage.createReader(tsymbol1, 5, T("2017-10-01T12:02:00Z"));

		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(0, 4));
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_3KIT_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1)).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = storage.createReader(tsymbol1, 5, T("2017-10-01T12:02:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KIT_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReader(new TFSymbol(symbol1, TimeFrame.M2), 5, T("2017-10-01T12:02:00Z"));
	}
	
	@Test
	public void testCreateReaderTo_2KT() throws Exception {
		List<SymbolDaily> segments = new ArrayList<>();
		segments.add(new SymbolDaily(symbol1, 2017,  9, 15));
		segments.add(new SymbolDaily(symbol1, 2017,  9,  1));
		segments.add(new SymbolDaily(symbol1, 2017, 12, 31));
		expect(segstorMock.listDailySegments(symbol1)).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = 
				new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstorMock, segments),
						null, T("2017-09-14T21:05:00Z"));
		
		actual = storage.createReaderTo(tsymbol1, T("2017-09-14T21:05:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReaderTo_2KT_IfNoSegmentsFound() throws Exception {
		expect(segstorMock.listDailySegments(symbol1)).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = storage.createReaderTo(tsymbol1, T("2017-09-14T21:05:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReaderTo_2KT_ThrowsIfTimeFrameUnsupported() throws Exception {
		storage.createReaderTo(new TFSymbol(symbol1, TimeFrame.M30), T("2017-09-14T21:05:00Z"));
	}

}
