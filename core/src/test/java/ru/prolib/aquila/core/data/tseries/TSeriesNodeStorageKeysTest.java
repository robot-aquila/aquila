package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;

public class TSeriesNodeStorageKeysTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	private IMocksControl control;
	private EventQueue queueMock;
	private TSeriesNodeStorage storageMock;
	private TSeriesNodeStorageKeys service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		storageMock = control.createMock(TSeriesNodeStorage.class);
		service = new TSeriesNodeStorageKeys("foobar", queueMock, storageMock);
	}
	
	@Test
	public void testCtor() {
		assertSame(queueMock, service.getEventQueue());
		assertSame(storageMock, service.getStorage());
		assertEquals("foobar.UPDATE", service.onUpdate().getId());
		assertEquals("foobar.LENGTH_UPDATE", service.onLengthUpdate().getId());
	}
	
	@Test
	public void testLock() {
		storageMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		storageMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(storageMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_NonExistingInterval() {
		storageMock.lock();
		expect(storageMock.getIntervalIndex(T("2017-09-01T03:06:00Z"))).andReturn(-1);
		storageMock.unlock();
		control.replay();
		
		assertNull(service.get(T("2017-09-01T03:06:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_ExistingInterval() throws Exception {
		storageMock.lock();
		expect(storageMock.getIntervalIndex(T("2017-09-01T03:06:00Z"))).andReturn(10);
		expect(storageMock.getIntervalStart(10)).andReturn(T("2017-09-01T03:05:00Z"));
		storageMock.unlock();
		control.replay();
		
		assertEquals(T("2017-09-01T03:05:00Z"), service.get(T("2017-09-01T03:06:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.D1);
		control.replay();
		
		assertSame(ZTFrame.D1, service.getTimeFrame());
		
		control.verify();
	}

	
	@Test
	public void testGet0() throws Exception {
		storageMock.lock();
		expect(storageMock.getLength()).andReturn(100);
		expect(storageMock.getIntervalStart(99)).andReturn(T("2017-09-01T03:20:00Z"));
		storageMock.unlock();
		control.replay();
		
		assertEquals(T("2017-09-01T03:20:00Z"), service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet0_NullIfZeroLength() throws Exception {
		storageMock.lock();
		expect(storageMock.getLength()).andReturn(0);
		storageMock.unlock();
		control.replay();
		
		assertNull(service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I() throws Exception {
		storageMock.lock();
		expect(storageMock.getIntervalStart(85)).andReturn(T("2017-09-01T03:20:00Z"));
		storageMock.unlock();
		control.replay();
		
		assertEquals(T("2017-09-01T03:20:00Z"), service.get(85));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(storageMock.getLength()).andReturn(1024);
		control.replay();
		
		assertEquals(1024, service.getLength());
		
		control.verify();
	}
	
	@Test
	public void testRegisterSeries() {
		expect(storageMock.registerSeries()).andReturn(8);
		control.replay();
		
		assertEquals(8, service.registerSeries());
		
		control.verify();
	}
	
	@Test
	public void testSetValue3_NewInterval() {
		Interval interval = Interval.of(T("2017-09-01T03:25:00Z"), T("2017-09-01T03:30:00Z"));
		TSeriesUpdate expectedUpdate = new TSeriesUpdateImpl(interval)
				.setNewNode(true)
				.setNodeIndex(12)
				.setOldValue(null)
				.setNewValue(256);
		storageMock.lock();
		expect(storageMock.setValue(T("2017-09-01T03:27:15Z"), 5, 256)).andReturn(expectedUpdate);
		TSeriesUpdate keyUpd = new TSeriesUpdateImpl(interval)
				.setNewNode(true)
				.setNodeIndex(12)
				.setOldValue(null)
				.setNewValue(T("2017-09-01T03:25:00Z"));
		queueMock.enqueue(service.onUpdate(), new TSeriesUpdateEventFactory(keyUpd));
		queueMock.enqueue(service.onLengthUpdate(), new TSeriesUpdateEventFactory(keyUpd));
		storageMock.unlock();
		control.replay();
		
		TSeriesUpdate actualUpdate = service.setValue(T("2017-09-01T03:27:15Z"), 5, 256);
		
		control.verify();
		assertEquals(expectedUpdate, actualUpdate);
	}

	@Test
	public void testSetValue3_ExistingInterval_HasChanged() {
		Interval interval = Interval.of(T("2017-09-01T03:25:00Z"), T("2017-09-01T03:30:00Z"));
		TSeriesUpdate expectedUpdate = new TSeriesUpdateImpl(interval)
				.setNewNode(false)
				.setNodeIndex(85)
				.setOldValue(null)
				.setNewValue(117);
		storageMock.lock();
		expect(storageMock.setValue(T("2017-09-01T03:27:15Z"), 56, 117)).andReturn(expectedUpdate);
		TSeriesUpdate keyUpd = new TSeriesUpdateImpl(interval)
				.setNewNode(false)
				.setNodeIndex(85)
				.setOldValue(T("2017-09-01T03:25:00Z"))
				.setNewValue(T("2017-09-01T03:25:00Z"));
		queueMock.enqueue(service.onUpdate(), new TSeriesUpdateEventFactory(keyUpd));
		storageMock.unlock();
		control.replay();
		
		TSeriesUpdate actualUpdate = service.setValue(T("2017-09-01T03:27:15Z"), 56, 117);
		
		control.verify();
		assertEquals(expectedUpdate, actualUpdate);
	}
	
	@Test
	public void testSetValue3_ExistingInterval_NotChanged() {
		Interval interval = Interval.of(T("2017-09-01T03:25:00Z"), T("2017-09-01T03:30:00Z"));
		TSeriesUpdate expectedUpdate = new TSeriesUpdateImpl(interval)
				.setNewNode(false)
				.setNodeIndex(85)
				.setOldValue(117)
				.setNewValue(117);
		storageMock.lock();
		expect(storageMock.setValue(T("2017-09-01T03:27:15Z"), 56, 117)).andReturn(expectedUpdate);
		storageMock.unlock();
		control.replay();
		
		TSeriesUpdate actualUpdate = service.setValue(T("2017-09-01T03:27:15Z"), 56, 117);
		
		control.verify();
		assertEquals(expectedUpdate, actualUpdate);
	}
	
	@Test
	public void testGetValue2_TI() {
		expect(storageMock.getValue(T("2017-09-01T05:13:27Z"), 6)).andReturn(826);
		control.replay();
		
		assertEquals(826, (int) service.getValue(T("2017-09-01T05:13:27Z"), 6));
		
		control.verify();
	}
	
	@Test
	public void testGetValue2_II() throws Exception {
		expect(storageMock.getValue(100, 6)).andReturn(826);
		control.replay();
		
		assertEquals(826, (int) service.getValue(100, 6));
		
		control.verify();
	}
	
	@Test
	public void testGetValue1_I() {
		expect(storageMock.getValue(6)).andReturn(826);
		control.replay();
		
		assertEquals(826, (int) service.getValue(6));
		
		control.verify();
	}
	
	@Test
	public void testClear() {
		storageMock.clear();
		control.replay();
		
		service.clear();
		
		control.verify();
	}
	
	@Test
	public void testGetIntervalStart() throws Exception {
		expect(storageMock.getIntervalStart(100)).andReturn(T("2017-09-01T05:20:00Z"));
		control.replay();
		
		assertEquals(T("2017-09-01T05:20:00Z"), service.getIntervalStart(100));
		
		control.verify();
	}

	@Test
	public void testIntervalIndex() {
		expect(storageMock.getIntervalIndex(T("2017-09-01T05:21:07Z"))).andReturn(700);
		control.replay();
		
		assertEquals(700, service.getIntervalIndex(T("2017-09-01T05:21:07Z")));
		
		control.verify();
	}

	@Test
	public void testToIndex() {
		expect(storageMock.getIntervalIndex(T("2017-09-01T11:10:33Z"))).andReturn(1700);
		control.replay();
		
		assertEquals(1700, service.toIndex(T("2017-09-01T11:10:33Z")));
		
		control.verify();		
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(storageMock.getIntervalStart(445)).andReturn(T("2018-12-15T12:26:40Z"));
		control.replay();
		
		assertEquals(T("2018-12-15T12:26:40Z"), service.toKey(445));
		
		control.verify();
	}
	
}
