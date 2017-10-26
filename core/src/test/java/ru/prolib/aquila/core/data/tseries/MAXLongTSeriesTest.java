package ru.prolib.aquila.core.data.tseries;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;

public class MAXLongTSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeries<Long> sourceMock;
	private TAMath mathMock;
	private MAXLongTSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		mathMock = control.createMock(TAMath.class);
		series = new MAXLongTSeries("foobar", sourceMock, 5, mathMock);
	}
	
	@Test
	public void testCtor4() {
		assertEquals("foobar", series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(5, series.getPeriod());
		assertSame(mathMock, series.getMath());
	}
	
	@Test
	public void testCtor3() {
		series = new MAXLongTSeries("zulu", sourceMock, 3);
		assertEquals("zulu", series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(3, series.getPeriod());
		assertSame(TAMath.getInstance(), series.getMath());
	}
	
	@Test
	public void testCtor2() {
		series = new MAXLongTSeries(sourceMock, 2);
		assertEquals(TSeries.DEFAULT_ID, series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(2, series.getPeriod());
		assertSame(TAMath.getInstance(), series.getMath());		
	}
	
	@Test
	public void testGet0() throws Exception {
		sourceMock.lock();
		expect(mathMock.maxL(sourceMock, 5)).andReturn(80L);
		sourceMock.unlock();
		control.replay();
		
		assertEquals((Long) 80L, series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		sourceMock.lock();
		expect(mathMock.maxL(sourceMock, 700, 5)).andReturn(120L);
		sourceMock.unlock();
		control.replay();
		
		assertEquals((Long) 120L, series.get(700));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(sourceMock.getLength()).andReturn(500);
		control.replay();
		
		assertEquals(500, series.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(sourceMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		sourceMock.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		sourceMock.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_IfIntervalExists() throws Exception {
		sourceMock.lock();
		expect(sourceMock.toIndex(T("2017-09-01T08:58:00Z"))).andReturn(10);
		expect(mathMock.maxL(sourceMock, 10, 5)).andReturn(580L);
		sourceMock.unlock();
		control.replay();
		
		assertEquals((Long) 580L, series.get(T("2017-09-01T08:58:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_IfIntervalNotExists() throws Exception {
		sourceMock.lock();
		expect(sourceMock.toIndex(T("2017-09-01T09:02:00Z"))).andReturn(-1);
		sourceMock.unlock();
		control.replay();
		
		assertNull(series.get(T("2017-09-01T09:02:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M10);
		control.replay();
		
		assertEquals(ZTFrame.M10, series.getTimeFrame());
		
		control.verify();
	}

	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2017-09-01T09:03:00Z"))).andReturn(15);
		control.replay();
		
		assertEquals(15, series.toIndex(T("2017-09-01T09:03:00Z")));
		
		control.verify();
	}

}
