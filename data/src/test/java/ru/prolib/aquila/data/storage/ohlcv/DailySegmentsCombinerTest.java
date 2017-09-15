package ru.prolib.aquila.data.storage.ohlcv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class DailySegmentsCombinerTest {
	private static final Symbol
		symbol1 = new Symbol("AAPL"),
		symbol2 = new Symbol("MSFT");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Interval IM1(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(1));
	}
	
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
		fixture.add(new Candle(IM1("2010-05-15T10:00:00Z"), 150, 155, 149, 152, 1000));
		fixture.add(new Candle(IM1("2010-05-15T11:17:00Z"), 158, 159, 152, 155, 2000));
		fixture.add(new Candle(IM1("2010-05-15T15:30:00Z"), 154, 155, 151, 151, 1500));
		fixture.add(new Candle(IM1("2010-07-12T12:38:00Z"), 140, 148, 140, 143, 2200));
		fixture.add(new Candle(IM1("2010-07-12T12:39:00Z"), 143, 145, 142, 142, 1000));
		fixture.add(new Candle(IM1("2010-07-12T12:40:00Z"), 142, 143, 138, 140, 3200));
		fixture.add(new Candle(IM1("2013-12-01T20:15:00Z"), 133, 137, 133, 135, 1400));
		fixture.add(new Candle(IM1("2013-12-01T23:19:00Z"), 128, 128, 127, 127, 1000));
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
