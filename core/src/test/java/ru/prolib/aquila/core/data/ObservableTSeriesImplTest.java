package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.concurrency.LID;

public class ObservableTSeriesImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private EditableTSeries<Integer> underlyingSeriesMock;
	private EventQueue queueMock;
	private ObservableTSeriesImpl<Integer> series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		underlyingSeriesMock = control.createMock(EditableTSeries.class);
		queueMock = control.createMock(EventQueue.class);
		expect(underlyingSeriesMock.getId()).andStubReturn("foo");
		control.replay();
		series = new ObservableTSeriesImpl<>(queueMock, underlyingSeriesMock);
		control.reset();
	}
	
	@Test
	public void testCtor() {
		assertSame(queueMock, series.getEventQueue());
		assertSame(underlyingSeriesMock, series.getUnderlyingSeries());
		assertEquals("foo.UPDATE", series.onUpdate().getId());
	}
	
	@Test
	public void testGet1_T() {
		expect(underlyingSeriesMock.get(T("2017-08-23T00:00:00Z"))).andReturn(856);
		control.replay();
		
		assertEquals(856, (int) series.get(T("2017-08-23T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(underlyingSeriesMock.getTimeFrame()).andReturn(TimeFrame.M15);
		control.replay();
		
		assertEquals(TimeFrame.M15, series.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGetId() {
		expect(underlyingSeriesMock.getId()).andReturn("foobar");
		control.replay();
		
		assertEquals("foobar", series.getId());
		
		control.verify();
	}
	
	@Test
	public void testGet0() throws Exception {
		expect(underlyingSeriesMock.get()).andReturn(86);
		control.replay();
		
		assertEquals(86, (int) series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		expect(underlyingSeriesMock.get(915)).andReturn(21);
		control.replay();
		
		assertEquals(21, (int) series.get(915));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(underlyingSeriesMock.getLength()).andReturn(753);
		control.replay();
		
		assertEquals(753, series.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(underlyingSeriesMock.getLID()).andReturn(lid);
		control.replay();
		
		assertEquals(lid, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		underlyingSeriesMock.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		underlyingSeriesMock.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}

}
