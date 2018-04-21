package ru.prolib.aquila.core.data.tseries;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;

public class QATRTSeriesTest {
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeries<Candle> sourceMock;
	private TAMath mathMock;
	private QATRTSeries service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		mathMock = control.createMock(TAMath.class);
		service = new QATRTSeries("foobar", sourceMock, 5, mathMock);
	}
	
	@Test
	public void testCtor4() {
		assertEquals("foobar", service.getId());
		assertSame(sourceMock, service.getSource());
		assertEquals(5, service.getPeriod());
		assertSame(mathMock, service.getMath());
	}
	
	@Test
	public void testCtor3() {
		service = new QATRTSeries("zulu24", sourceMock, 7);
		assertEquals("zulu24", service.getId());
		assertSame(sourceMock, service.getSource());
		assertEquals(7, service.getPeriod());
		assertSame(TAMath.getInstance(), service.getMath());
	}
	
	@Test
	public void testCtor2() {
		service = new QATRTSeries(sourceMock, 8);
		assertEquals(TSeries.DEFAULT_ID, service.getId());
		assertSame(sourceMock, service.getSource());
		assertEquals(8, service.getPeriod());
		assertSame(TAMath.getInstance(), service.getMath());
	}
	
	@Test
	public void testGet0() throws Exception {
		sourceMock.lock();
		expect(mathMock.qatr(sourceMock, 5)).andReturn(of("215.35"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(of("215.35"), service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		sourceMock.lock();
		expect(mathMock.qatr(sourceMock, 10, 5)).andReturn(of("240.726"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(of("240.726"), service.get(10));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() throws Exception {
		expect(sourceMock.getLength()).andReturn(100);
		control.replay();
		
		assertEquals(100, service.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() throws Exception {
		LID lid = LID.createInstance();
		expect(sourceMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() throws Exception {
		sourceMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() throws Exception {
		sourceMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_IfIntervalExists() throws Exception {
		sourceMock.lock();
		expect(sourceMock.toIndex(T("2017-09-01T08:58:00Z"))).andReturn(10);
		expect(mathMock.qatr(sourceMock, 10, 5)).andReturn(of("580.17"));
		sourceMock.unlock();
		control.replay();
		
		assertEquals(of("580.17"), service.get(T("2017-09-01T08:58:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_IfIntervalNotExists() throws Exception {
		sourceMock.lock();
		expect(sourceMock.toIndex(T("2017-09-01T09:02:00Z"))).andReturn(-1);
		sourceMock.unlock();
		control.replay();
		
		assertNull(service.get(T("2017-09-01T09:02:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M15);
		control.replay();
		
		assertEquals(ZTFrame.M15, service.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2017-09-01T09:03:00Z"))).andReturn(15);
		control.replay();
		
		assertEquals(15, service.toIndex(T("2017-09-01T09:03:00Z")));
		
		control.verify();
	}

}
