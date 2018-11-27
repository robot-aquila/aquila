package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.utils.LimitedAmountIterator;
import ru.prolib.aquila.data.storage.ohlcv.utils.PreciseTimeLimitsIterator;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

@SuppressWarnings("unchecked")
public class InterdayMDStorageOverSMSSTest {
	private static final Symbol
		symbol1 = new Symbol("MSFT"),
		symbol2 = new Symbol("AAPL"),
		symbol3 = new Symbol("GAZP");
	private static final TFSymbol
		tsymbol1 = new TFSymbol(symbol1, ZTFrame.D1MSK),
		tsymbol2 = new TFSymbol(symbol2, ZTFrame.D1MSK),
		tsymbol3 = new TFSymbol(symbol3, ZTFrame.D1MSK);
	private static final ZoneId MSK = ZoneId.of("Europe/Moscow");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Interval IM1(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(1));
	}
	
	static Interval IM5(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(5));
	}
	
	static List<SymbolMonthly> getTestSegments() {
		List<SymbolMonthly> segments = new ArrayList<>();
		segments.add(new SymbolMonthly(symbol1, 2017,  9));
		segments.add(new SymbolMonthly(symbol1, 2017, 10));
		segments.add(new SymbolMonthly(symbol1, 2017, 12));
		return segments;
	}
	
	private IMocksControl control;
	private SymbolMonthlySegmentStorage<Candle> smssMock;
	private List<Candle> fixture;
	private CloseableIteratorStub<Candle> iteratorStub1, iteratorStub2, iteratorStub3;
	private InterdayMDStorageOverSMSS service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		smssMock = control.createMock(SymbolMonthlySegmentStorage.class);
		iteratorStub1 = new CloseableIteratorStub<>();
		iteratorStub2 = new CloseableIteratorStub<>();
		iteratorStub3 = new CloseableIteratorStub<>();
		fixture = new ArrayList<>();
		service = new InterdayMDStorageOverSMSS(smssMock, ZTFrame.D1MSK);
	}
	
	@Test
	public void testGetKeys() throws Exception {
		Set<Symbol> symbols = new HashSet<>();
		symbols.add(symbol1);
		symbols.add(symbol2);
		symbols.add(symbol3);
		expect(smssMock.listSymbols()).andReturn(symbols);
		control.replay();
		Set<TFSymbol> actual, expected = new HashSet<>();
		
		actual = service.getKeys();
		
		expected.add(tsymbol1);
		expected.add(tsymbol2);
		expected.add(tsymbol3);
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_1K() throws Exception {
		List<SymbolMonthly> segments = new ArrayList<>();
		segments.add(new SymbolMonthly(symbol1, 2012, 1));
		segments.add(new SymbolMonthly(symbol1, 2012, 2));
		segments.add(new SymbolMonthly(symbol1, 2013, 5));
		expect(smssMock.listMonthlySegments(symbol1)).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual, expected = new MonthlySegmentsCombiner(smssMock, segments);
		
		actual = service.createReader(tsymbol1);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_1K_IfNoSegmentsFound() throws Exception {
		expect(smssMock.listMonthlySegments(symbol1)).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = service.createReader(tsymbol1);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_1K_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReader(new TFSymbol(symbol1, ZTFrame.H1));
	}
	
	@Test
	public void testCreateReaderFrom_2KT() throws Exception {
		expect(smssMock.getZoneID()).andReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER)
			)).andReturn(segments);
		control.replay();
		
		CloseableIterator<Candle> actual, expected = 
			new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smssMock, segments),
				T("2017-08-31T21:00:00Z"), // <-- aligned
				null
			);
		
		actual = service.createReaderFrom(tsymbol1, T("2017-08-31T21:05:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReaderFrom_2KT_IfNoSegmentsFound() throws Exception {
		expect(smssMock.getZoneID()).andReturn(MSK);
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER)
			)).andReturn(new ArrayList<>());
		control.replay();
		
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = service.createReaderFrom(tsymbol1, T("2017-09-14T21:05:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReaderFrom_2KT_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReaderFrom(new TFSymbol(symbol1, ZTFrame.M15), Instant.now());
	}
	
	@Test
	public void testCreateReaderFrom_2KT_StartTimeIsAlignedByTF() throws Exception {
		expect(smssMock.getZoneID()).andReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(
				symbol1,
				new MonthPoint(2017, Month.SEPTEMBER)
			)).andReturn(segments);
		control.replay();
		
		CloseableIterator<Candle> actual, expected = 
			new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smssMock, segments),
				T("2017-08-31T21:00:00Z"),
				null
			);
		
		actual = service.createReaderFrom(tsymbol1, T("2017-08-31T21:05:08.804Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KTI() throws Exception {
		expect(smssMock.getZoneID()).andReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER)
			)).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual,
			expected = new LimitedAmountIterator(
					new PreciseTimeLimitsIterator(
						new MonthlySegmentsCombiner(smssMock, segments),
						T("2017-08-31T21:00:00Z"), // <-- aligned!
						null
					),
				25);
		
		actual = service.createReader(tsymbol1, T("2017-08-31T21:05:00Z"), 25);
		
		control.verify();
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateReader_3KTI_IfNoSegmentsFound() throws Exception {
		expect(smssMock.getZoneID()).andReturn(MSK);
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.AUGUST)
			)).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = service.createReader(tsymbol1, T("2017-08-31T20:00:00Z"), 25);
		
		control.verify();
		assertEquals(expected, actual);
	}

	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KTI_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReader(new TFSymbol(symbol1, ZTFrame.H1), T("2017-09-14T20:00:00Z"), 25);
	}
	
	@Test
	public void testCreateReader_3KTI_StartTimeIsAlignedByTF() throws Exception {
		testCreateReader_3KTI();
	}
	
	@Test
	public void testCreateReader_3KTT() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER),
				new MonthPoint(2017, Month.DECEMBER)))
			.andReturn(segments);
		control.replay();
		
		CloseableIterator<Candle> actual,
			expected = new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smssMock, segments),
				T("2017-08-31T21:00:00Z"),
				T("2017-12-10T21:00:00Z")
			);
		
		actual = service.createReader(
				tsymbol1,
				T("2017-08-31T21:00:00Z"),
				T("2017-12-10T21:00:00Z")
			);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KTT_IfNoSegmentsFound() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER),
				new MonthPoint(2017, Month.DECEMBER)))
			.andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = service.createReader(tsymbol1, T("2017-08-31T21:05:00Z"), T("2017-12-10T18:56:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KTT_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReader(
				new TFSymbol(symbol1, ZTFrame.M30),
				T("2017-08-31T21:05:00Z"),
				T("2017-12-10T18:56:00Z")
			);
	}
	
	@Test
	public void testCreateReader_3KTT_EndTimeIsExclusive() throws Exception {
		testCreateReader_3KTT();
	}
	
	@Test
	public void testCreateReader_3KTT_StartTimeIsAlignedByTF() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER),
				new MonthPoint(2017, Month.DECEMBER)))
			.andReturn(segments);
		control.replay();
		
		CloseableIterator<Candle> actual,
			expected = new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smssMock, segments),
				T("2017-08-31T21:00:00Z"),
				T("2017-12-10T21:00:00Z")
			);
		
		actual = service.createReader(
				tsymbol1,
				T("2017-08-31T21:32:18.005Z"),
				T("2017-12-10T21:00:00Z")
			);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KTT_EndTimeIsAlignedByTF() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(2017, Month.SEPTEMBER),
				new MonthPoint(2017, Month.DECEMBER)))
			.andReturn(segments);
		control.replay();
		
		CloseableIterator<Candle> actual,
			expected = new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smssMock, segments),
				T("2017-08-31T21:00:00Z"),
				T("2017-12-10T21:00:00Z")
			);
		
		actual = service.createReader(
				tsymbol1,
				T("2017-08-31T21:00:00Z"),
				T("2017-12-11T18:18:46.202Z")
			);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReaderTo_2KT() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual,
			expected = new PreciseTimeLimitsIterator(
					new MonthlySegmentsCombiner(smssMock, segments),
					null,
					T("2017-12-14T21:00:00Z")
				);
		
		actual = service.createReaderTo(tsymbol1, T("2017-12-14T21:05:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReaderTo_2KT_IfNoSegmentsFound() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();
		
		actual = service.createReaderTo(tsymbol1, T("2017-12-14T21:05:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReaderTo_2KT_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReaderTo(new TFSymbol(symbol1, ZTFrame.M30), T("2017-09-14T21:05:00Z"));
	}
	
	@Test
	public void testCreateReaderTo_2KT_EndTimeIsExclusive() throws Exception {
		testCreateReaderTo_2KT();
	}
	
	@Test
	public void testCreateReaderTo_2KT_EndTimeIsAlignedByTF() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		control.replay();
		CloseableIterator<Candle> actual,
			expected = new PreciseTimeLimitsIterator(
					new MonthlySegmentsCombiner(smssMock, segments),
					null,
					T("2017-12-14T21:00:00Z") // <-- aligned
				);
		
		actual = service.createReaderTo(tsymbol1, T("2017-12-14T21:05:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	private void testCreateReader_3KIT_FillStubIterators() {
		CandleBuilder cb = new CandleBuilder().withTimeFrame(ZTFrame.D1MSK);
		fixture.add(cb.buildCandle("2017-09-11T21:00:00Z", 150, 156, 150, 155, 1700));
		fixture.add(cb.buildCandle("2017-09-15T21:00:00Z", 155, 155, 149, 151, 1800));
		fixture.add(cb.buildCandle("2017-09-22T21:00:00Z", 151, 152, 145, 148, 1900));
		fixture.add(cb.buildCandle("2017-10-01T21:00:00Z", 143, 145, 140, 142, 5000));
		fixture.add(cb.buildCandle("2017-10-07T21:00:00Z", 142, 149, 141, 145, 1500));
		fixture.add(cb.buildCandle("2017-10-12T21:00:00Z", 145, 152, 145, 150, 1700));
		fixture.add(cb.buildCandle("2017-12-10T21:00:00Z", 150, 152, 148, 151, 1000));
		fixture.add(cb.buildCandle("2017-12-11T21:00:00Z", 151, 156, 150, 155, 2000));
		fixture.add(cb.buildCandle("2017-12-12T21:00:00Z", 155, 158, 152, 156, 3000));
		iteratorStub1 = new CloseableIteratorStub<>(fixture.subList(0, 3));
		iteratorStub2 = new CloseableIteratorStub<>(fixture.subList(3, 6));
		iteratorStub3 = new CloseableIteratorStub<>(fixture.subList(6, 9));
	}
	
	@Test
	public void testCreateReader_3KIT_AvailCountLtMaxCount() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2020, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017,  9))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 15, T("2020-12-01T00:00:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_LastSegmentPartiallyIncluded() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017,  9))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();

		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 15, T("2017-12-11T21:00:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(0, 7));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_FirstIncludedSegmentPartiallyIncluded() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017,  9))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();

		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 7, T("2017-12-31T00:00:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(2, 9));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_SkipHeadSegments() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();

		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 4, T("2017-12-31T00:00:00Z"));

		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(5, 9));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_IfNoSegmentsFound() throws Exception {
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(new ArrayList<>());
		control.replay();
		CloseableIterator<Candle> actual, expected = new CloseableIteratorStub<>();

		actual = service.createReader(tsymbol1, 4, T("2017-12-31T00:00:00Z"));

		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateReader_3KIT_ThrowsIfTimeFrameUnsupported() throws Exception {
		service.createReader(new TFSymbol(symbol1, ZTFrame.M2), 5, T("2017-10-01T12:02:00Z"));
	}
	
	@Test
	public void testCreateReader_3KIT_EndTimeIsExclusive() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017,  9))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 100, T("2017-12-11T21:00:00Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(0, 7));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReader_3KIT_EndTimeIsAlignedByTF() throws Exception {
		testCreateReader_3KIT_FillStubIterators();
		expect(smssMock.getZoneID()).andStubReturn(MSK);
		List<SymbolMonthly> segments = getTestSegments();
		expect(smssMock.listMonthlySegments(symbol1,
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(2017, Month.DECEMBER))
			).andReturn(segments);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 12))).andReturn(iteratorStub3);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017, 10))).andReturn(iteratorStub2);
		expect(smssMock.createReader(new SymbolMonthly(symbol1, 2017,  9))).andReturn(iteratorStub1);
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		CloseableIterator<Candle> result =
				service.createReader(tsymbol1, 100, T("2017-12-12T19:09:27.954Z"));
		
		control.verify();
		while ( result.next() ) {
			actual.add(result.item());
		}
		expected.addAll(fixture.subList(0, 7));
		assertEquals(expected, actual);
	}

}
