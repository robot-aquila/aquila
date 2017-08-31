package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateImpl;
import ru.prolib.aquila.core.utils.Variant;

public class TSeriesImplTest {
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
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
		expect(storageMock.get(T("2017-08-22T00:00:00Z"), 1)).andReturn(86);
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
		expect(storageMock.get(1)).andReturn(856);
		control.replay();
		
		assertEquals(856, (int) series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		expect(storageMock.get(-25, 1)).andReturn(102);
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
		expect(storageMock.set(T("2012-01-01T00:00:05Z"), 1, 202)).andReturn(update);
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

}
