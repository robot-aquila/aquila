package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateImpl;
import ru.prolib.aquila.core.utils.Variant;

public class TSeriesImplTest {
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	Interval IM1(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(1));
	}
	
	private IMocksControl control;
	private TSeriesNodeStorage storageMock;
	private TSeriesImpl<Integer> series;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		storageMock = control.createMock(TSeriesNodeStorage.class);
		expect(storageMock.registerSeries()).andStubReturn(1);
		control.replay();
		series = new TSeriesImpl<>("foo", storageMock);
		control.reset();
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", series.getId());
		assertSame(storageMock, series.getStorage());
		assertEquals(1, series.getSeriesID());
	}
	
	@Test
	public void testGet1_T() {
		expect(storageMock.getValue(T("2017-08-22T00:00:00Z"), 1)).andReturn(86);
		control.replay();
		
		assertEquals(86, (int) series.get(T("2017-08-22T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(storageMock.getTimeFrame()).andReturn(TimeFrame.M1);
		control.replay();
		
		assertEquals(TimeFrame.M1, series.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void get0() {
		expect(storageMock.getValue(1)).andReturn(856);
		control.replay();
		
		assertEquals(856, (int) series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		expect(storageMock.getValue(-25, 1)).andReturn(102);
		control.replay();
		
		assertEquals(102, (int) series.get(-25));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(storageMock.getLength()).andReturn(80);
		control.replay();
		
		assertEquals(80, series.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(storageMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		storageMock.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		storageMock.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}
	
	@Test
	public void testSet() {
		TSeriesUpdate update = new TSeriesUpdateImpl(null);
		expect(storageMock.setValue(T("2012-01-01T00:00:05Z"), 1, 202)).andReturn(update);
		control.replay();
		
		assertSame(update, series.set(T("2012-01-01T00:00:05Z"), 202));
		
		control.verify();
	}
	
	@Test
	public void testClear() {
		storageMock.clear();
		control.replay();
		
		series.clear();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}
	
	@Test
	public void testEquals() {
		TSeriesNodeStorage storageMock2 = control.createMock(TSeriesNodeStorage.class);
		Variant<String> vId = new Variant<>("foo", "bar");
		Variant<TSeriesNodeStorage> vStorage = new Variant<>(vId, storageMock, storageMock2);
		Variant<Integer> vSeriesId = new Variant<>(vStorage, 1, 5);
		Variant<?> iterator = vSeriesId;
		int foundCnt = 0;
		TSeriesImpl<Integer> x, found = null;
		do {
			control.reset();
			expect(vStorage.get().registerSeries()).andStubReturn(vSeriesId.get());
			control.replay();
			x = new TSeriesImpl<>(vId.get(), vStorage.get());
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertEquals(1, found.getSeriesID());
		assertSame(storageMock, found.getStorage());
	}
	
	@Test
	public void testToIndex() {
		expect(storageMock.getIntervalIndex(T("2017-09-01T07:10:00Z"))).andReturn(820);
		control.replay();
		
		assertEquals(820, series.toIndex(T("2017-09-01T07:10:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testBugCase1() throws Exception {
		List<Candle> fixture = new ArrayList<>();
		fixture.add(new Candle(IM1("2017-12-31T10:00:00Z"), 150, 152, 148, 151, 1000)); // #0
		fixture.add(new Candle(IM1("2017-10-01T12:01:00Z"), 143, 145, 140, 142, 5000)); // #1
		fixture.add(new Candle(IM1("2017-10-01T12:02:00Z"), 142, 149, 141, 145, 1500)); // #2
		fixture.add(new Candle(IM1("2017-10-01T12:03:00Z"), 145, 152, 145, 150, 1700)); // #3
		fixture.add(new Candle(IM1("2017-09-15T18:01:00Z"), 150, 156, 150, 155, 1700)); // #4
		fixture.add(new Candle(IM1("2017-09-15T18:02:00Z"), 155, 155, 149, 151, 1800)); // #5
		fixture.add(new Candle(IM1("2017-09-15T18:03:00Z"), 151, 152, 145, 148, 1900)); // #6
		
		TSeriesImpl<Candle> series = new TSeriesImpl<>(TimeFrame.M1);
		for ( Candle x : fixture ) {
			series.set(x.getStartTime(), x);	
		}
		
		List<Candle> expected = new ArrayList<>(), actual = new ArrayList<>();
		expected.add(fixture.get(4));
		expected.add(fixture.get(5));
		expected.add(fixture.get(6));
		expected.add(fixture.get(1));
		expected.add(fixture.get(2));
		expected.add(fixture.get(3));
		expected.add(fixture.get(0));
		
		for ( int i = 0; i < series.getLength(); i ++ ) {
			actual.add(series.get(i));
		}
		assertEquals(expected, actual);
	}

}
