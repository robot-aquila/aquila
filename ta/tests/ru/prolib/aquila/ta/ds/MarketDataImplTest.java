package ru.prolib.aquila.ta.ds;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ta.ValueList;
import ru.prolib.aquila.ta.ValueListImpl;

public class MarketDataImplTest {
	IMocksControl control;
	MarketDataReader reader;
	ValueList values;
	DataSetIterator iterator;
	MarketDataImpl md,mdr;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		reader = control.createMock(MarketDataReader.class);
		iterator = control.createMock(DataSetIterator.class);
		values = control.createMock(ValueList.class);
		md = new MarketDataImpl(reader, values);
		mdr = new MarketDataImpl(reader);
	}
	
	@Test
	public void testInterface() throws Exception {
		assertTrue(mdr instanceof MarketDataCommon);
	}
	
	@Test
	public void testConstructor2() throws Exception {
		assertSame(reader, md.getMarketDataReader());
		assertSame(values, md.getValueList());
	}
	
	@Test
	public void testConstructor1() throws Exception {
		assertSame(reader, mdr.getMarketDataReader());
		ValueListImpl list = (ValueListImpl) mdr.getValueList();
		assertNotNull(list);
	}
	
	@Test
	public void testGetLevel() throws Exception {
		assertEquals(0, md.getLevel());
	}
	
	@Test (expected=MarketDataException.class)
	public void testGetSource() throws Exception {
		md.getSource();
	}
	
	@Test
	public void testPrepare_Ok() throws Exception {
		Observer observer = control.createMock(Observer.class);
		expect(reader.prepare()).andReturn(iterator);
		expect(iterator.next()).andReturn(true);
		values.update();
		observer.update(md, null);
		expect(iterator.next()).andReturn(true);
		values.update();
		observer.update(md, null);
		expect(iterator.next()).andReturn(false);
		iterator.close();
		control.replay();
		
		md.addObserver(observer);
		md.prepare();
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Observer observer = control.createMock(Observer.class);
		expect(reader.update()).andReturn(iterator);
		expect(iterator.next()).andReturn(true);
		values.update();
		observer.update(md, null);
		expect(iterator.next()).andReturn(true);
		values.update();
		observer.update(md, null);
		expect(iterator.next()).andReturn(false);
		iterator.close();
		control.replay();
		
		md.addObserver(observer);
		md.update();
		
		control.verify();
	}

}
