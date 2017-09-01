package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateEventFactory;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateImpl;
import ru.prolib.aquila.core.utils.Variant;

public class ObservableTSeriesImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private EditableTSeries<Integer> underlyingSeriesMock1, underlyingSeriesMock2;
	private EventQueue queueMock1, queueMock2;
	private ObservableTSeriesImpl<Integer> series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		underlyingSeriesMock1 = control.createMock(EditableTSeries.class);
		underlyingSeriesMock2 = control.createMock(EditableTSeries.class);
		queueMock1 = control.createMock(EventQueue.class);
		queueMock2 = control.createMock(EventQueue.class);
		expect(underlyingSeriesMock1.getId()).andStubReturn("foo");
		control.replay();
		series = new ObservableTSeriesImpl<>(queueMock1, underlyingSeriesMock1);
		control.reset();
	}
	
	@Test
	public void testCtor() {
		assertSame(queueMock1, series.getEventQueue());
		assertSame(underlyingSeriesMock1, series.getUnderlyingSeries());
		assertEquals("foo.UPDATE", series.onUpdate().getId());
	}
	
	@Test
	public void testGet1_T() {
		expect(underlyingSeriesMock1.get(T("2017-08-23T00:00:00Z"))).andReturn(856);
		control.replay();
		
		assertEquals(856, (int) series.get(T("2017-08-23T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(underlyingSeriesMock1.getTimeFrame()).andReturn(TimeFrame.M15);
		control.replay();
		
		assertEquals(TimeFrame.M15, series.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGetId() {
		expect(underlyingSeriesMock1.getId()).andReturn("foobar");
		control.replay();
		
		assertEquals("foobar", series.getId());
		
		control.verify();
	}
	
	@Test
	public void testGet0() throws Exception {
		expect(underlyingSeriesMock1.get()).andReturn(86);
		control.replay();
		
		assertEquals(86, (int) series.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		expect(underlyingSeriesMock1.get(915)).andReturn(21);
		control.replay();
		
		assertEquals(21, (int) series.get(915));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(underlyingSeriesMock1.getLength()).andReturn(753);
		control.replay();
		
		assertEquals(753, series.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(underlyingSeriesMock1.getLID()).andReturn(lid);
		control.replay();
		
		assertEquals(lid, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		underlyingSeriesMock1.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		underlyingSeriesMock1.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}
	
	@Test
	public void testSet2_TV_HasChanged() throws Exception {
		Interval interval = Interval.of(Instant.EPOCH, Duration.ofMinutes(5));
		TSeriesUpdate update = new TSeriesUpdateImpl(interval)
				.setNewNode(true)
				.setNodeIndex(215)
				.setOldValue(null)
				.setNewValue(850);
		expect(underlyingSeriesMock1.set(T("2017-08-28T00:00:00Z"), 850)).andReturn(update);
		queueMock1.enqueue(series.onUpdate(), new TSeriesUpdateEventFactory(update));
		control.replay();
		
		assertSame(update, series.set(T("2017-08-28T00:00:00Z"), 850));
		
		control.verify();
	}
	
	@Test
	public void testSet2_TV_NoChanges() throws Exception {
		Interval interval = Interval.of(Instant.EPOCH, Duration.ofMinutes(5));
		TSeriesUpdate update = new TSeriesUpdateImpl(interval)
				.setNewNode(true)
				.setNodeIndex(10)
				.setOldValue(850)
				.setNewValue(850);
		expect(underlyingSeriesMock1.set(T("2017-08-28T10:30:00Z"), 850)).andReturn(update);
		control.replay();
		
		assertSame(update, series.set(T("2017-08-28T10:30:00Z"), 850));

		control.verify();
	}
	
	@Test
	public void testClear() throws Exception {
		underlyingSeriesMock1.clear();
		control.replay();
		
		series.clear();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(series.equals(series));
		assertFalse(series.equals(this));
		assertFalse(series.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<EventQueue> vQue = new Variant<>(queueMock1, queueMock2);
		Variant<EditableTSeries<Integer>> vSer = new Variant<EditableTSeries<Integer>>(vQue)
				.add(underlyingSeriesMock1)
				.add(underlyingSeriesMock2);
		Variant<?> iterator = vSer;
		int foundCnt = 0;
		ObservableTSeriesImpl<Integer> x, found = null;
		do {
			control.reset();
			expect(vSer.get().getId()).andReturn("zulu");
			control.replay();
			x = new ObservableTSeriesImpl<Integer>(vQue.get(), vSer.get());
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(queueMock1, found.getEventQueue());
		assertSame(underlyingSeriesMock1, found.getUnderlyingSeries());
	}
	
	@Test
	public void testToIndex() {
		expect(underlyingSeriesMock1.toIndex(T("2017-09-01T07:12:00Z"))).andReturn(500);
		control.replay();
		
		assertEquals(500, series.toIndex(T("2017-09-01T07:12:00Z")));
		
		control.verify();
	}

}
