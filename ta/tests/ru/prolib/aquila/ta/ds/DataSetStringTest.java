package ru.prolib.aquila.ta.ds;


import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetStringTest {
	IMocksControl control;
	DataSet dataSet;
	DataSetString value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetString(dataSet, "zulu5");
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("zulu5", value.getName());
		assertEquals(ValueImpl.DEFAULT_ID, value.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		value = new DataSetString("helo", dataSet, "zulu5");
		assertSame(dataSet, value.getDataSet());
		assertEquals("zulu5", value.getName());
		assertEquals("helo", value.getId());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		expect(dataSet.getString("zulu5")).andReturn("charlie9");
		control.replay();
		
		value.update();
		
		control.verify();
		assertEquals("charlie9", value.get());
	}
	
	@Test (expected=ValueUpdateException.class)
	public void testUpdate_Throws() throws Exception {
		expect(dataSet.getString("zulu5"))
			.andThrow(new DataSetException("ups"));
		control.replay();
		
		value.update();
	}

}
