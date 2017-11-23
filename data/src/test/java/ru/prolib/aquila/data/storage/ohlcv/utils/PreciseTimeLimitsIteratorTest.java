package ru.prolib.aquila.data.storage.ohlcv.utils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.data.storage.ohlcv.utils.PreciseTimeLimitsIterator;

public class PreciseTimeLimitsIteratorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private static List<Candle> fixture;
	private static CandleBuilder cb;
	
	static {
		cb = new CandleBuilder().withTimeFrame(ZTFrame.M1);
		fixture = new ArrayList<>();
		fixture.add(cb.buildCandle("2017-09-15T03:48:00Z", 150, 158, 149, 152, 1000));
		fixture.add(cb.buildCandle("2017-09-15T03:49:00Z", 152, 153, 145, 148, 2000));
		fixture.add(cb.buildCandle("2017-09-15T03:50:00Z", 148, 149, 145, 147, 3500));
		fixture.add(cb.buildCandle("2017-09-15T03:51:00Z", 147, 149, 141, 142, 1250));
		fixture.add(cb.buildCandle("2017-09-15T03:52:00Z", 142, 145, 142, 143, 1720));
		fixture.add(cb.buildCandle("2017-09-15T03:53:00Z", 143, 149, 142, 148, 2100));
		fixture.add(cb.buildCandle("2017-09-15T03:54:00Z", 148, 152, 147, 147, 1500));
		fixture.add(cb.buildCandle("2017-09-15T03:55:00Z", 147, 151, 145, 151, 4200));
	}
	
	private IMocksControl control;
	private CloseableIterator<Candle> underlyingMock;
	private CloseableIteratorStub<Candle> underlyingStub;
	private PreciseTimeLimitsIterator iterator;

	@SuppressWarnings({ "unchecked" })
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		underlyingMock = control.createMock(CloseableIterator.class);
		underlyingStub = new CloseableIteratorStub<Candle>();
	}
	
	@After
	public void tearDown() throws Exception {
		underlyingStub.close();
	}
	
	@Test
	public void testIterator() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub,
				T("2017-09-15T03:50:00Z"), T("2017-09-15T03:54:00Z"));
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(2, 6));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterator_IfPeriofStartNotDefined() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub, null, T("2017-09-15T03:54:00Z"));
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(0, 6));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterator_IfPeriodEndNotDefined() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub, T("2017-09-15T03:50:00Z"), null);
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(2, 8));
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testIterator_IfPeriodStartTimeBeforeFirst() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub,
				T("2017-09-12T00:00:00Z"), T("2017-09-15T03:54:00Z"));
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(0, 6));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterator_IfPeriodEndTimeAfterLast() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub,
				T("2017-09-15T03:50:00Z"), T("2017-09-16T00:00:00Z"));
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(2, 8));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClose() throws Exception {
		iterator = new PreciseTimeLimitsIterator(underlyingMock, null, null);
		underlyingMock.close();
		control.replay();
		
		iterator.close();
		iterator.close(); // consecutive calls have no effect
		
		control.verify();
	}
	
	@Test
	public void testItem() throws Exception {
		Candle result = fixture.get(0);
		iterator = new PreciseTimeLimitsIterator(underlyingMock, null, null);
		expect(underlyingMock.next()).andReturn(true);
		expect(underlyingMock.item()).andReturn(result);
		control.replay();
		iterator.next(); // make started
		
		assertSame(result, iterator.item());

		control.verify();
	}
	
	@Test
	public void testNext_IfClosed() throws Exception {
		iterator = new PreciseTimeLimitsIterator(underlyingStub, null, null);
		iterator.close();
		
		assertFalse(iterator.next());
		assertFalse(iterator.next());
		assertFalse(iterator.next());
	}
	
	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfNotStarted() throws Exception {
		iterator = new PreciseTimeLimitsIterator(underlyingMock, null, null);
		control.replay();
		
		iterator.item();
	}

	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfFinished() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub, null, null);
		while ( iterator.next() ) { }
		
		iterator.item();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		iterator = new PreciseTimeLimitsIterator(underlyingStub, null, null);
		assertTrue(iterator.equals(iterator));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}

	@Test
	public void testEquals() {
		iterator = new PreciseTimeLimitsIterator(underlyingStub,
				T("2017-09-15T03:50:00Z"), T("2017-09-16T00:00:00Z"));
		Variant<CloseableIterator<Candle>> vUndr = new Variant<>(underlyingStub, underlyingMock);
		Variant<Instant> vStart = new Variant<Instant>(vUndr)
				.add(T("2017-09-15T03:50:00Z"))
				.add(T("2017-01-01T00:00:00Z"))
				.add(null);
		Variant<Instant> vEnd = new Variant<Instant>(vStart)
				.add(T("2017-09-16T00:00:00Z"))
				.add(T("2017-01-01T00:00:00Z"))
				.add(null);
		Variant<?> it = vEnd;
		int foundCnt = 0;
		PreciseTimeLimitsIterator x, found = null;
		do {
			x = new PreciseTimeLimitsIterator(vUndr.get(), vStart.get(), vEnd.get());
			if ( iterator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( it.next() );
		assertEquals(1, foundCnt);
		assertNotNull(found); // ??? to test or not to test
	}
	
	@Test
	public void testIterator_IfPeriodStartNotDefinedButPeriodEndDefined() throws Exception {
		List<Candle> fixture = new ArrayList<>();
		fixture.add(cb.buildCandle("2017-10-01T12:01:00Z", 143, 145, 140, 142, 5000));
		fixture.add(cb.buildCandle("2017-10-01T12:02:00Z", 142, 149, 141, 145, 1500));
		fixture.add(cb.buildCandle("2017-10-01T12:03:00Z", 145, 152, 145, 150, 1700));
		underlyingStub = new CloseableIteratorStub<>(fixture);
		iterator = new PreciseTimeLimitsIterator(underlyingStub, null, T("2017-10-01T12:03:00Z"));
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.add(fixture.get(0));
		expected.add(fixture.get(1));
		assertEquals(expected, actual);
	}

}
