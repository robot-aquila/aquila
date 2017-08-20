package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.TestEventQueueImpl;
import ru.prolib.aquila.core.concurrency.LID;

public class ObservableSeriesImplTest {
	private IMocksControl control;
	private EventQueue queue;
	private EventListenerStub listenerStub;
	private SeriesImpl<Double> source;
	private EditableSeries<Double> sourceMock;
	private ObservableSeriesImpl<Double> series; 

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(EditableSeries.class);
		queue = new TestEventQueueImpl();
		listenerStub = new EventListenerStub();
		source = new SeriesImpl<>("foo");
		series = new ObservableSeriesImpl<>(queue, source);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", series.getId());
		assertEquals("foo.SET", series.onSet().getId());
		assertEquals("foo.ADD", series.onAdd().getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		
		assertEquals(17.02d, series.get(), 0.001d);
	}
	
	@Test
	public void testGet1() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		
		assertEquals(25.04d, series.get(0), 0.001d);
		assertEquals(17.02d, series.get(1), 0.001d);
		assertEquals(95.12d, series.get(2), 0.001d);

		assertEquals(17.02d, series.get(-1), 0.001d);
		assertEquals(25.04d, series.get(-2), 0.001d);
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(0, series.getLength());
		source.add(25.04d);
		assertEquals(1, series.getLength());
		source.add(17.02d);
		assertEquals(2, series.getLength());
		source.add(95.12d);
		assertEquals(3, series.getLength());
	}
	
	@Test
	public void testSet() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		series.onAdd().addListener(listenerStub);
		series.onSet().addListener(listenerStub);
		
		series.set(86.19d);
		
		assertEquals(86.19d, series.get(), 0.001d);
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new SeriesEvent<Double>(series.onSet(), 2, 86.19d), listenerStub.getEvent(0));
	}
	
	@Test
	public void testAdd() throws Exception {
		source.add(25.04d);
		series.onAdd().addListener(listenerStub);
		series.onSet().addListener(listenerStub);

		series.add(42.14d);
		
		assertEquals(42.14d, series.get(), 0.001d);
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new SeriesEvent<Double>(series.onAdd(), 1, 42.14d), listenerStub.getEvent(0));
	}
	
	@Test
	public void testClear() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		
		series.clear();
		
		assertEquals(0, source.getLength());
	}
	
	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(sourceMock.getId()).andStubReturn("foo");
		expect(sourceMock.getLID()).andReturn(lidStub);
		control.replay();

		series = new ObservableSeriesImpl<>(queue, sourceMock);		
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		expect(sourceMock.getId()).andStubReturn("bar");
		sourceMock.lock();
		control.replay();

		series = new ObservableSeriesImpl<>(queue, sourceMock);		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		expect(sourceMock.getId()).andStubReturn("buz");
		sourceMock.unlock();
		control.replay();

		series = new ObservableSeriesImpl<>(queue, sourceMock);		
		series.unlock();
		
		control.verify();
	}

}
