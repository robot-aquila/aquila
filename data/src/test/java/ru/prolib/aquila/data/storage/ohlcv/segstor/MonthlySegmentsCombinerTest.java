package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

public class MonthlySegmentsCombinerTest {
	private static final Symbol
		symbol1 = new Symbol("AAPL"),
		symbol2 = new Symbol("MSFT");

	private IMocksControl control;
	private SymbolMonthlySegmentStorage<Candle> smssMock1, smssMock2;
	private CloseableIterator<Candle> readerMock1, readerMock2;
	private List<SymbolMonthly> segments1, segments2;
	private MonthlySegmentsCombiner service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		smssMock1 = control.createMock(SymbolMonthlySegmentStorage.class);
		smssMock2 = control.createMock(SymbolMonthlySegmentStorage.class);
		readerMock1 = control.createMock(CloseableIterator.class);
		readerMock2 = control.createMock(CloseableIterator.class);
		segments1 = new ArrayList<>();
		segments2 = new ArrayList<>();
	}
	
	@Test
	public void testIterator() throws Exception {
		segments1.add(new SymbolMonthly(symbol1, 2015, 1));
		segments1.add(new SymbolMonthly(symbol1, 2015, 5));
		segments1.add(new SymbolMonthly(symbol1, 2019, 1));
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		List<Candle> fixture = new ArrayList<>();
		// segment 1
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-01-15T00:00:00Z")
				.withOpenPrice(150L)
				.withHighPrice(155L)
				.withLowPrice(149L)
				.withClosePrice(152L)
				.withVolume(1000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-01-20T00:00:00Z")
				.withOpenPrice(158L)
				.withHighPrice(159L)
				.withLowPrice(152L)
				.withClosePrice(155L)
				.withVolume(2000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-01-25T00:00:00Z")
				.withOpenPrice(154L)
				.withHighPrice(155L)
				.withLowPrice(151L)
				.withClosePrice(151L)
				.withVolume(1500L)
				.buildCandle());
		// segment 2
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-05-01T00:00:00Z")
				.withOpenPrice(140L)
				.withHighPrice(148L)
				.withLowPrice(140L)
				.withClosePrice(143L)
				.withVolume(2200L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-05-10T00:00:00Z")
				.withOpenPrice(143L)
				.withHighPrice(145L)
				.withLowPrice(142L)
				.withClosePrice(142L)
				.withVolume(1000L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2015-05-11T00:00:00Z")
				.withOpenPrice(142L)
				.withHighPrice(143L)
				.withLowPrice(138L)
				.withClosePrice(140L)
				.withVolume(3200L)
				.buildCandle());
		// segment 3
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2019-01-01T00:00:00Z")
				.withOpenPrice(133L)
				.withHighPrice(137L)
				.withLowPrice(133L)
				.withClosePrice(135L)
				.withVolume(1400L)
				.buildCandle());
		fixture.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.D1)
				.withTime("2019-01-11T00:00:00Z")
				.withOpenPrice(128L)
				.withHighPrice(128L)
				.withLowPrice(127L)
				.withClosePrice(127L)
				.withVolume(1000L)
				.buildCandle());
		expect(smssMock1.createReader(new SymbolMonthly(symbol1, 2015, 1)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(0, 3)));
		expect(smssMock1.createReader(new SymbolMonthly(symbol1, 2015, 5)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(3, 6)));
		expect(smssMock1.createReader(new SymbolMonthly(symbol1, 2019, 1)))
			.andReturn(new CloseableIteratorStub<>(fixture.subList(6, 8)));
		control.replay();
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>(fixture);
		
		while ( service.next() ) {
			actual.add(service.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		service.close();
		
		service.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		service.close();
		
		service.item();
	}

	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfNotPointedTo() throws Exception {
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		
		service.item();
	}
	
	@Test
	public void testClose() throws Exception {
		readerMock1.close();
		control.replay();
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		service.setSegmentReader(readerMock1);
		
		service.close();
		assertTrue(service.isClosed());
		service.close(); // has no effect
		
		control.verify();
		assertNull(service.getSegmentReader());
	}

	@Test
	public void testEquals_SpecialCases() {
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		segments1.add(new SymbolMonthly(symbol1, 2010,  1));
		segments1.add(new SymbolMonthly(symbol1, 2010,  2));
		segments2.add(new SymbolMonthly(symbol2, 2015, 10));
		service = new MonthlySegmentsCombiner(smssMock1, segments1);
		service.setSegmentReader(readerMock1);
		Variant<SymbolMonthlySegmentStorage<Candle>> vSmss = new Variant<>(smssMock1, smssMock2);
		Variant<List<SymbolMonthly>> vSegs = new Variant<>(vSmss, segments1, segments2);
		Variant<CloseableIterator<Candle>> vRdr = new Variant<CloseableIterator<Candle>>(vSegs)
				.add(readerMock1)
				.add(readerMock2)
				.add(null);
		Variant<Boolean> vCls = new Variant<>(vRdr, false, true);
		Variant<?> it = vCls;
		int foundCnt = 0;
		MonthlySegmentsCombiner x, found = null;
		do {
			x = new MonthlySegmentsCombiner(vSmss.get(), vSegs.get());
			x.setSegmentReader(vRdr.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( it.next() );
		assertEquals(1, foundCnt);
		assertEquals(smssMock1, found.getSMSS());
		assertEquals(segments1, found.getSegments());
		assertEquals(readerMock1, found.getSegmentReader());
		assertFalse(found.isClosed());
	}

}
