package ru.prolib.aquila.core.data.ta;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;

public class QEMATest {
	private IMocksControl control;
	private Series<CDecimal> sourceMock;
	private SeriesImpl<CDecimal> source;
	private QEMA series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(Series.class);
		source = new SeriesImpl<>();
		series = new QEMA("foo", source, 4);
		source.add(CDecimalBD.of("5"));
		source.add(CDecimalBD.of("2"));
		source.add(CDecimalBD.of("7"));
		source.add(CDecimalBD.of("3"));
		source.add(CDecimalBD.of("8"));
		source.add(CDecimalBD.of("1"));
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo", series.getId());
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals(CDecimalBD.of("3.8492800"), series.get());
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(null, series.get(0));
		assertEquals(null, series.get(1));
		assertEquals(null, series.get(2));
		assertEquals(CDecimalBD.of("4.2480000"), series.get(3));
		assertEquals(CDecimalBD.of("5.7488000"), series.get(4));
		assertEquals(CDecimalBD.of("3.8492800"), series.get(5));
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
