package ru.prolib.aquila.ta.ds;


import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetDateTest {
	IMocksControl control;
	DataSet dataSet;
	DataSetDate value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetDate(dataSet, "mytime");
	}
	
	@Test
	public void testConstructor2() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("mytime", value.getName());
		assertEquals(ValueImpl.DEFAULT_ID, value.getId());
	}
	
	@Test
	public void testConstructor3() throws Exception {
		value = new DataSetDate("foobar", dataSet, "mytime");
		assertSame(dataSet, value.getDataSet());
		assertEquals("mytime", value.getName());
		assertEquals("foobar", value.getId());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Date date = new Date();
		expect(dataSet.getDate("mytime")).andReturn(date);
		control.replay();
		
		value.update();
		
		control.verify();
		assertSame(date, value.get());
	}
	
	@Test (expected=ValueUpdateException.class)
	public void testUpdate_Throws() throws Exception {
		expect(dataSet.getDate("mytime")).andThrow(new DataSetException("Yep"));
		control.replay();
		
		value.update();
	}

}
