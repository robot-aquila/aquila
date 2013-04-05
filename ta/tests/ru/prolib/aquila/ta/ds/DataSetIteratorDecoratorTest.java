package ru.prolib.aquila.ta.ds;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class DataSetIteratorDecoratorTest {
	IMocksControl control;
	DataSetIterator iterator1,iterator2;
	DataSetIteratorDecorator decorator;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		iterator1 = control.createMock(DataSetIterator.class);
		iterator2 = control.createMock(DataSetIterator.class);
		decorator = new DataSetIteratorDecorator(iterator1);
	}
	
	@Test
	public void testAccessorsMutators() throws Exception {
		assertSame(iterator1, decorator.getDataSetIterator());
		decorator.setDataSetIterator(iterator2);
		assertSame(iterator2, decorator.getDataSetIterator());
	}
	
	@Test
	public void getDouble_Delegate() throws Exception {
		expect(iterator1.getDouble("zulu")).andReturn(123.45d);
		control.replay();
		
		assertEquals(123.45d, decorator.getDouble("zulu"), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testGetString_Delegate() throws Exception {
		expect(iterator1.getString("alpha")).andReturn("gamma");
		control.replay();
		
		assertEquals("gamma", decorator.getString("alpha"));
		
		control.verify();
	}
	
	@Test
	public void testGetDate_Delegate() throws Exception {
		Date date = new Date();
		expect(iterator1.getDate("foo")).andReturn(date);
		control.replay();
		
		assertSame(date, decorator.getDate("foo"));
		
		control.verify();
	}
	
	@Test
	public void testNext_Delegate() throws Exception {
		expect(iterator1.next()).andReturn(true);
		expect(iterator1.next()).andReturn(false);
		control.replay();
		
		assertTrue(decorator.next());
		assertFalse(decorator.next());
		
		control.verify();
	}
	
	@Test
	public void testGetLong_Delegate() throws Exception {
		expect(iterator1.getLong("foobar")).andReturn(111L);
		control.replay();
		
		assertEquals((Long)111L, decorator.getLong("foobar"));
		
		control.verify();
	}
	
	@Test
	public void testClose_Delegate() throws Exception {
		iterator1.close();
		control.replay();
		
		decorator.close();
		
		control.verify();
	}

}
