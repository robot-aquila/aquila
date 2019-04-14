package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;

public class QEMATSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeries<CDecimal> sourceMock;
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
		expect(mathMock.qema(sourceMock, 34, 7)).andReturn(CDecimalBD.of("12.34"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(CDecimalBD.of("12.34"), series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		sourceMock.lock();
		expect(mathMock.qema(sourceMock, 85, 7)).andReturn(CDecimalBD.of("86.12"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(CDecimalBD.of("86.12"), series.get(85));
		
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
		expect(mathMock.qema(sourceMock, 25, 7)).andReturn(CDecimalBD.of("41.12"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(CDecimalBD.of("41.12"), series.get(T("2017-09-01T07:34:00Z")));
		
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
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M15);
		control.replay();
		
		assertEquals(ZTFrame.M15, series.getTimeFrame());
		
		control.verify();
	}

	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2017-09-01T07:49:00Z"))).andReturn(18);
		control.replay();
		
		assertEquals(18, series.toIndex(T("2017-09-01T07:49:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(sourceMock.toKey(661)).andReturn(T("2018-12-15T12:20:31Z"));
		control.replay();
		
		assertEquals(T("2018-12-15T12:20:31Z"), series.toKey(661));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstIndexBefore() throws Exception {
		expect(sourceMock.getFirstIndexBefore(T("1917-10-11T13:45:00Z"))).andReturn(7265);
		control.replay();
		
		assertEquals(7265, series.getFirstIndexBefore(T("1917-10-11T13:45:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstBefore() throws Exception {
		sourceMock.lock();
		expect(sourceMock.getFirstIndexBefore(T("2015-01-01T00:00:00Z"))).andReturn(727);
		expect(mathMock.qema(sourceMock, 727, 7)).andReturn(of("234.12"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(of("234.12"), series.getFirstBefore(T("2015-01-01T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstBefore_NotFound() throws Exception {
		sourceMock.lock();
		expect(sourceMock.getFirstIndexBefore(T("2015-01-01T00:00:00Z"))).andReturn(-1);
		sourceMock.unlock();
		control.replay();
		
		assertNull(series.getFirstBefore(T("2015-01-01T00:00:00Z")));
		
		control.verify();
	}

}
