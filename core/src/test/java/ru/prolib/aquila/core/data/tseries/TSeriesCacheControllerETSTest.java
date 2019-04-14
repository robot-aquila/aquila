package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;

public class TSeriesCacheControllerETSTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private EditableTSeries<String> seriesMock;
	private List<TSeriesCache> cachesStub;
	private TSeriesCache cacheMock1, cacheMock2;
	private TSeriesCacheControllerETS<String> service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		seriesMock = control.createMock(EditableTSeries.class);
		cacheMock1 = control.createMock(TSeriesCache.class);
		cacheMock2 = control.createMock(TSeriesCache.class);
		cachesStub = new ArrayList<>();
		service = new TSeriesCacheControllerETS<>(seriesMock, cachesStub);
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(seriesMock.getTimeFrame()).andReturn(ZTFrame.D1MSK);
		control.replay();
		
		assertEquals(ZTFrame.D1MSK, service.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGet1T() throws Exception {
		expect(seriesMock.get(T("2018-04-06T01:00:00Z"))).andReturn("foo");
		control.replay();
		
		assertEquals("foo", service.get(T("2018-04-06T01:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testToIndex() throws Exception {
		expect(seriesMock.toIndex(T("2018-04-06T23:59:59Z"))).andReturn(80);
		control.replay();
		
		assertEquals(80, service.toIndex(T("2018-04-06T23:59:59Z")));
		
		control.verify();
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(seriesMock.toKey(905)).andReturn(T("2018-12-15T12:24:34Z"));
		control.replay();
		
		assertEquals(T("2018-12-15T12:24:34Z"), service.toKey(905));
		
		control.verify();
	}
	
	@Test
	public void testGetId() throws Exception {
		expect(seriesMock.getId()).andReturn("zulu24");
		control.replay();
		
		assertEquals("zulu24", service.getId());
		
		control.verify();
	}
	
	@Test
	public void testGet0() throws Exception {
		expect(seriesMock.get()).andReturn("bar");
		control.replay();
		
		assertEquals("bar", service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1I() throws Exception {
		expect(seriesMock.get(10)).andReturn("buzz");
		control.replay();
		
		assertEquals("buzz", service.get(10));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(seriesMock.getLength()).andReturn(100);
		control.replay();
		
		assertEquals(100, service.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(seriesMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		seriesMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		seriesMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testSet2() throws Exception {
		cachesStub.add(cacheMock1);
		cachesStub.add(cacheMock2);
		TSeriesUpdateImpl expectedUpdate = new TSeriesUpdateImpl(ZTFrame.H1.getInterval(T("2018-04-06T13:00:00Z")));
		expectedUpdate.setNodeIndex(35);
		expect(seriesMock.set(T("2018-04-06T13:00:00Z"), "foobar")).andReturn(expectedUpdate);
		cacheMock1.invalidate(35);
		cacheMock2.invalidate(35);
		control.replay();
		
		TSeriesUpdate actualUpdate = service.set(T("2018-04-06T13:00:00Z"), "foobar");
		
		control.verify();
		assertEquals(expectedUpdate, actualUpdate);
	}

	@Test
	public void testClear() {
		cachesStub.add(cacheMock1);
		cachesStub.add(cacheMock2);
		seriesMock.clear();
		cacheMock1.invalidate(0);
		cacheMock1.shrink();
		cacheMock2.invalidate(0);
		cacheMock2.shrink();
		control.replay();
		
		service.clear();
		
		control.verify();
	}
	
	@Test
	public void testTruncate() {
		cachesStub.add(cacheMock1);
		cachesStub.add(cacheMock2);
		seriesMock.lock();
		seriesMock.truncate(250);
		expect(seriesMock.getLength()).andReturn(218);
		seriesMock.unlock();
		cacheMock1.invalidate(218);
		cacheMock1.shrink();
		cacheMock2.invalidate(218);
		cacheMock2.shrink();
		control.replay();
		
		service.truncate(250);
		
		control.verify();
	}
	
	@Test
	public void testAddCache() {
		assertSame(service, service.addCache(cacheMock2));
		assertSame(service, service.addCache(cacheMock1));
		
		List<TSeriesCache> expected = new ArrayList<>();
		expected.add(cacheMock2);
		expected.add(cacheMock1);
		assertEquals(expected, cachesStub);
	}
	
	@Test
	public void testRemoveCache() {
		cachesStub.add(cacheMock1);
		cachesStub.add(cacheMock2);
		assertSame(service, service.removeCache(cacheMock2));
		
		List<TSeriesCache> expected = new ArrayList<>();
		expected.add(cacheMock1);
		assertEquals(expected, cachesStub);
		
		assertSame(service, service.removeCache(cacheMock1));
		expected.clear();
		assertEquals(expected, cachesStub);
	}
	
	@Test
	public void testGetFirstBefore() {
		expect(seriesMock.getFirstBefore(T("2019-03-25T06:17:26Z"))).andReturn("choo");
		control.replay();
		
		assertEquals("choo", service.getFirstBefore(T("2019-03-25T06:17:26Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstIndexBefore() {
		expect(seriesMock.getFirstIndexBefore(T("2092-07-12T23:19:01Z"))).andReturn(26);
		control.replay();
		
		assertEquals(26, service.getFirstIndexBefore(T("2092-07-12T23:19:01Z")));
		
		control.verify();
	}

}
