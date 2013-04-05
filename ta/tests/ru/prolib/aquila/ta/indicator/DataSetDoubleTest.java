package ru.prolib.aquila.ta.indicator;


import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * 2012-04-07
 * $Id: DataSetDoubleTest.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetDoubleTest {
	private IMocksControl control;
	private DataSet dataSet;
	private DataSetDouble value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetDouble(dataSet, "myval");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("myval", value.getName());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfDateSetIsNull() throws Exception {
		new DataSetDouble(null, "myval");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfNameIsNull() throws Exception {
		new DataSetDouble(dataSet, null);
	}
	
	@Test
	public void testCalculate_Ok() throws Exception {
		Double val = new Double(123.456d);
		expect(dataSet.getDouble("myval")).andReturn(val);
		control.replay();
		
		assertSame(val, value.calculate());
		
		control.verify();
	}
	
	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfDataSetThrows() throws Exception {
		expect(dataSet.getDouble("myval"))
			.andThrow(new DataSetException("Yep"));
		control.replay();
		
		value.calculate();
	}

}
