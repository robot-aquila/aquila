package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TimeFrame;

public class QEMATSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeries<Double> sourceMock;
	private TAMath mathMock;
	private QEMATSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		mathMock = control.createMock(TAMath.class);
		series = new QEMATSeries("foo", sourceMock, 7, mathMock);
	}
	
	@Test
	public void testCtor4() {
		assertEquals("foo", series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(7, series.getPeriod());
		assertSame(mathMock, series.getMath());
	}
	
	@Test
	public void testCtor3() {
		series = new QEMATSeries("zulu", sourceMock, 8);
		assertEquals("zulu", series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(8, series.getPeriod());
		assertSame(TAMath.getInstance(), series.getMath());
	}
	
	@Test
	public void testCtor2() {
		series = new QEMATSeries(sourceMock, 14);
		assertEquals(TSeries.DEFAULT_ID, series.getId());
		assertSame(sourceMock, series.getSource());
		assertEquals(14, series.getPeriod());
		assertSame(TAMath.getInstance(), series.getMath());
	}
	
	@Test
	public void testGet0() throws Exception {
		sourceMock.lock();
		expect(sourceMock.getLength()).andReturn(35);
		expect(mathMock.qema(sourceMock, 34, 7)).andReturn(12.34d);
		sourceMock.unlock();
		control.replay();
		
		assertEquals(12.34d, series.get(), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		sourceMock.lock();
		expect(mathMock.qema(sourceMock, 85, 7)).andReturn(86.12d);
		sourceMock.unlock();
		control.replay();
		
		assertEquals(86.12d, series.get(85), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(sourceMock.getLength()).andReturn(15);
		control.replay();
		
		assertEquals(15, series.getLength());
		
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
		expect(sourceMock.toIndex(T("2017-09-01T07:34:00Z"))).andReturn(25);
		expect(mathMock.qema(sourceMock, 25, 7)).andReturn(41.12d);
		sourceMock.unlock();
		control.replay();
		
		assertEquals(41.12d, series.get(T("2017-09-01T07:34:00Z")), 0.001d);
		
		control.verify();
	}

	@Test
	public void testGet1_T_IfIntervalNotExists() throws Exception {
		sourceMock.lock();
		expect(sourceMock.toIndex(T("2017-09-01T07:34:00Z"))).andReturn(-1);
		sourceMock.unlock();
		control.replay();
		
		assertNull(series.get(T("2017-09-01T07:34:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(sourceMock.getTimeFrame()).andReturn(TimeFrame.M15);
		control.replay();
		
		assertEquals(TimeFrame.M15, series.getTimeFrame());
		
		control.verify();
	}

	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2017-09-01T07:49:00Z"))).andReturn(18);
		control.replay();
		
		assertEquals(18, series.toIndex(T("2017-09-01T07:49:00Z")));
		
		control.verify();
	}

}
