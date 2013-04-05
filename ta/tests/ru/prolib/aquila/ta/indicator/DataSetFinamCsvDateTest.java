package ru.prolib.aquila.ta.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Calendar;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;

/**
 * 2012-04-07
 * $Id: DataSetFinamCsvDateTest.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetFinamCsvDateTest {
	private IMocksControl control;
	private DataSet dataSet;
	private DataSetFinamCsvDate value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataSet = control.createMock(DataSet.class);
		value = new DataSetFinamCsvDate(dataSet, "date", "time");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(dataSet, value.getDataSet());
		assertEquals("date", value.getDateName());
		assertEquals("time", value.getTimeName());
		assertSame(value.getName(), value.getDateName());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfDateSetIsNull() throws Exception {
		new DataSetFinamCsvDate(null, "date", "time");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfDateNameIsNull() throws Exception {
		new DataSetFinamCsvDate(dataSet, null, "time");
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTimeNameIsNull() throws Exception {
		new DataSetFinamCsvDate(dataSet, "date", null);
	}

	@Test
	public void testCalculate_Ok() throws Exception {
		expect(dataSet.getString(eq("date"))).andReturn("20100615");
		expect(dataSet.getString(eq("time"))).andReturn("183045");
		replay(dataSet);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(2010, 5, 15, 18, 30, 45);
		assertEquals(c.getTime(), value.calculate());
		
		verify(dataSet);
	}
	
	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfTimeFormatException() throws Exception {
		expect(dataSet.getString(eq("date"))).andReturn("20100615");
		expect(dataSet.getString(eq("time"))).andReturn("foo");
		replay(dataSet);
		value.calculate();
	}
	
	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfDateFormatException() throws Exception {
		expect(dataSet.getString(eq("date"))).andReturn("foo");
		expect(dataSet.getString(eq("time"))).andReturn("183045");
		replay(dataSet);
		value.calculate();
	}
	
	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfGetDateException() throws Exception {
		expect(dataSet.getString(eq("date")))
			.andThrow(new DataSetException("test error"));
		replay(dataSet);
		value.calculate();
	}

	@Test (expected=ValueException.class)
	public void testCalculate_ThrowsIfGetTimeException() throws Exception {
		expect(dataSet.getString(eq("date"))).andReturn("20100615");
		expect(dataSet.getString(eq("time")))
			.andThrow(new DataSetException("test error"));
		replay(dataSet);
		value.calculate();
	}

}
