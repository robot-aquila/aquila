package ru.prolib.aquila.data.storage.ohlcv.utils;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
import ru.prolib.aquila.data.storage.ohlcv.utils.LimitedAmountIterator;

public class LimitedAmountIteratorTest {

	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private static List<Candle> fixture;
	
	static {
		CandleBuilder cb = new CandleBuilder().withTimeFrame(ZTFrame.M1);
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
	private LimitedAmountIterator iterator;

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
		iterator = new LimitedAmountIterator(underlyingStub, 5);
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(0, 5));
		assertEquals(expected, actual);
	}

	@Test
	public void testIterator_IfAmountIsGreaterThanActual() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		expected.addAll(fixture.subList(0, 8));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testClose() throws Exception {
		iterator = new LimitedAmountIterator(underlyingMock, 15);		
		underlyingMock.close();
		control.replay();
		
		iterator.close();
		iterator.close(); // consecutive calls have no effect
		
		control.verify();
	}
	
	@Test
	public void testItem() throws Exception {
		Candle result = fixture.get(0);
		iterator = new LimitedAmountIterator(underlyingMock, 15);
		expect(underlyingMock.next()).andReturn(true);
		expect(underlyingMock.item()).andReturn(result);
		control.replay();
		iterator.next(); // make started
		
		assertSame(result, iterator.item());

		control.verify();
	}

	@Test (expected=IOException.class)
	public void testNext_IfClosed() throws Exception {
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		iterator.close();
		
		iterator.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		iterator.close();
		
		iterator.item();
	}

	@Test (expected=NoSuchElementException.class)
	public void testItem_ThrowsIfFinished() throws Exception {
		underlyingStub = new CloseableIteratorStub<Candle>(fixture);
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		while ( iterator.next() ) { }
		
		iterator.item();
	}

	@Test
	public void testEquals_SpecialCases() {
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		assertTrue(iterator.equals(iterator));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}

	@Test
	public void testEquals() {
		iterator = new LimitedAmountIterator(underlyingStub, 15);
		Variant<CloseableIterator<Candle>> vUndr = new Variant<CloseableIterator<Candle>>()
				.add(underlyingStub)
				.add(underlyingMock);
		Variant<Integer> vAmnt = new Variant<>(vUndr, 15, 5);
		Variant<?> it = vAmnt;
		int foundCnt = 0;
		LimitedAmountIterator x, found = null;
		do {
			x = new LimitedAmountIterator(vUndr.get(), vAmnt.get());
			if ( iterator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( it.next() );
		assertEquals(1, foundCnt);
		assertSame(underlyingStub, found.getUnderlyingIterator());
		assertEquals(15, found.getCurrentAmount());
	}

}
