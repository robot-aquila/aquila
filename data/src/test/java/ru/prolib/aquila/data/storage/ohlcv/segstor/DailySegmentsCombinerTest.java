package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.data.storage.ohlcv.segstor.DailySegmentsCombiner;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class DailySegmentsCombinerTest {
	private static final Symbol
		symbol1 = new Symbol("AAPL"),
		symbol2 = new Symbol("MSFT");
	
	private IMocksControl control;
	private SymbolDailySegmentStorage<Candle> segstorMock1, segstorMock2;
	private CloseableIterator<Candle> readerMock1, readerMock2;
	private List<SymbolDaily> segments1, segments2;
	private DailySegmentsCombiner iterator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		segstorMock1 = control.createMock(SymbolDailySegmentStorage.class);
		segstorMock2 = control.createMock(SymbolDailySegmentStorage.class);
		readerMock1 = control.createMock(CloseableIterator.class);
		readerMock2 = control.createMock(CloseableIterator.class);
		segments1 = new ArrayList<>();
		segments2 = new ArrayList<>();
	}
	
	@Test
	public void testIterator() throws Exception {
		segments1.add(new SymbolDaily(symbol1, 2010,  5, 15));
		segments1.add(new SymbolDaily(symbol1, 2010,  7, 12));
		segments1.add(new SymbolDaily(symbol1, 2013, 12,  1));
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		List<Candle> fixture = new ArrayList<>();
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-05-15T10:00:00Z")
				.withOpenPrice(150L)
				.withHighPrice(155L)
				.withLowPrice(149L)
				.withClosePrice(152L)
				.withVolume(1000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-05-15T11:17:00Z")
				.withOpenPrice(158L)
				.withHighPrice(159L)
				.withLowPrice(152L)
				.withClosePrice(155L)
				.withVolume(2000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-05-15T15:30:00Z")
				.withOpenPrice(154L)
				.withHighPrice(155L)
				.withLowPrice(151L)
				.withClosePrice(151L)
				.withVolume(1500L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-07-12T12:38:00Z")
				.withOpenPrice(140L)
				.withHighPrice(148L)
				.withLowPrice(140L)
				.withClosePrice(143L)
				.withVolume(2200L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-07-12T12:39:00Z")
				.withOpenPrice(143L)
				.withHighPrice(145L)
				.withLowPrice(142L)
				.withClosePrice(142L)
				.withVolume(1000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2010-07-12T12:40:00Z")
				.withOpenPrice(142L)
				.withHighPrice(143L)
				.withLowPrice(138L)
				.withClosePrice(140L)
				.withVolume(3200L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2013-12-01T20:15:00Z")
				.withOpenPrice(133L)
				.withHighPrice(137L)
				.withLowPrice(133L)
				.withClosePrice(135L)
				.withVolume(1400L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2013-12-01T23:19:00Z")
				.withOpenPrice(128L)
				.withHighPrice(128L)
				.withLowPrice(127L)
				.withClosePrice(127L)
				.withVolume(1000L)
				.buildCandle());
		expect(segstorMock1.createReader(new SymbolDaily(symbol1, 2010,  5, 15)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(0, 3)));
		expect(segstorMock1.createReader(new SymbolDaily(symbol1, 2010,  7, 12)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(3, 6)));
		expect(segstorMock1.createReader(new SymbolDaily(symbol1, 2013, 12, 1)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(6, 8)));
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>(fixture);
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		iterator.close();
		
		iterator.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		iterator.close();

		iterator.item();
	}

	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfNotPointedTo() throws Exception {
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		
		iterator.item();
	}
	
	@Test
	public void testClose() throws Exception {
		readerMock1.close();
		control.replay();
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		iterator.setSegmentReader(readerMock1);
		
		iterator.close();
		assertTrue(iterator.isClosed());
		iterator.close(); // has no effect
		
		control.verify();
		assertNull(iterator.getSegmentReader());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		assertTrue(iterator.equals(iterator));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		segments1.add(new SymbolDaily(symbol1, 2010,  1, 12));
		segments1.add(new SymbolDaily(symbol1, 2010,  2,  1));
		segments2.add(new SymbolDaily(symbol2, 2015, 10, 20));
		iterator = new DailySegmentsCombiner(segstorMock1, segments1);
		iterator.setSegmentReader(readerMock1);
		Variant<SymbolDailySegmentStorage<Candle>> vStor =new Variant<SymbolDailySegmentStorage<Candle>>()
				.add(segstorMock1)
				.add(segstorMock2);
		Variant<List<SymbolDaily>> vSegs = new Variant<List<SymbolDaily>>(vStor)
				.add(segments1)
				.add(segments2);
		Variant<CloseableIterator<Candle>> vRdr = new Variant<CloseableIterator<Candle>>(vSegs)
				.add(readerMock1)
				.add(readerMock2)
				.add(null);
		Variant<Boolean> vCls = new Variant<>(vRdr, false, true);
		Variant<?> it = vCls;
		int foundCnt = 0;
		DailySegmentsCombiner x, found = null;
		do {
			x = new DailySegmentsCombiner(vStor.get(), vSegs.get());
			x.setSegmentReader(vRdr.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( iterator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( it.next() );
		assertEquals(1, foundCnt);
		assertEquals(segstorMock1, found.getSegmentStorage());
		assertEquals(segments1, found.getSegments());
		assertEquals(readerMock1, found.getSegmentReader());
		assertFalse(found.isClosed());
	}

}
