package ru.prolib.aquila.ta.ds;


import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetDoubleTest {
	IMocksControl control;
	DataSet dataSet;
	DataSetDouble value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetDouble(dataSet, "foobar");
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("foobar", value.getName());
		assertEquals(ValueImpl.DEFAULT_ID, value.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		value = new DataSetDouble("zulu", dataSet, "foobar");
		assertSame(dataSet, value.getDataSet());
		assertEquals("foobar", value.getName());
		assertEquals("zulu", value.getId());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		expect(dataSet.getDouble("foobar")).andReturn(100.35d);
		control.replay();
		
		value.update();
		
		control.verify();
		assertEquals(100.35d, value.get(), 0.01d);
	}
	
	@Test (expected=ValueUpdateException.class)
	public void testUpdate_Throws() throws Exception {
		expect(dataSet.getDouble("foobar"))
			.andThrow(new DataSetException("test"));
		control.replay();
		
		value.update();
	}

}
