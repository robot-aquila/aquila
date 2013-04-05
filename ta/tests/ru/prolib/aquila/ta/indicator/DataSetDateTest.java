package ru.prolib.aquila.ta.indicator;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * 2012-04-07
 * $Id: DataSetDateTest.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetDateTest {
	private IMocksControl control;
	private DataSet dataSet;
	private DataSetDate value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetDate(dataSet, "mytime");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("mytime", value.getName());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfDateSetIsNull() throws Exception {
		new DataSetDate(null, "mytime");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfNameIsNull() throws Exception {
		new DataSetDate(dataSet, null);
	}
	
	@Test
	public void testCalculate_Ok() throws Exception {
		Date date = new Date();
		expect(dataSet.getDate("mytime")).andReturn(date);
		control.replay();
		
		assertSame(date, value.calculate());
		
		control.verify();
	}
	
	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfDataSetThrows() throws Exception {
		expect(dataSet.getDate("mytime")).andThrow(new DataSetException("Yep"));
		control.replay();
		
		value.calculate();
	}

}
