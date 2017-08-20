package ru.prolib.aquila.core.data.ta;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;

public class QEMATest {
	private IMocksControl control;
	private Series<Double> sourceMock;
	private SeriesImpl<Double> source;
	private QEMA series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(Series.class);
		source = new SeriesImpl<>();
		series = new QEMA("foo", source, 4);
		source.add(5d);
		source.add(2d);
		source.add(7d);
		source.add(3d);
		source.add(8d);
		source.add(1d);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo", series.getId());
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals(3.84928d, series.get(), 0.000001d);
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(null, series.get(0));
		assertEquals(null, series.get(1));
		assertEquals(null, series.get(2));
		assertEquals(4.248d,   series.get(3), 0.000001d);
		assertEquals(5.7488d,  series.get(4), 0.000001d);
		assertEquals(3.84928d, series.get(5), 0.000001d);
	}

	@Test
	public void testGetLength() {
		assertEquals(6, series.getLength());
	}
	
	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(sourceMock.getLID()).andReturn(lidStub);
		control.replay();
		
		series = new QEMA("foo", sourceMock, 4);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		sourceMock.lock();
		control.replay();
		
		series = new QEMA("foo", sourceMock, 4);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		sourceMock.unlock();
		control.replay();
		
		series = new QEMA("foo", sourceMock, 4);
		series.unlock();
		
		control.verify();
	}	

}
