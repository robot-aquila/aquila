package ru.prolib.aquila.ta.ds.csv;

import java.util.Calendar;
import org.junit.*;

import ru.prolib.aquila.ta.ValueUpdateException;
import ru.prolib.aquila.ta.ds.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetDateFinamTest {
	private DataSet set;
	private DataSetDateFinam value;

	@Before
	public void setUp() throws Exception {
		set = createMock(DataSet.class);
		value = new DataSetDateFinam("hello", set, "foo", "bar");
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertEquals("hello", value.getId());
		assertSame(set, value.getDataSet());
		assertEquals("foo", value.getDateName());
		assertEquals("bar", value.getTimeName());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		expect(set.getString(eq("foo"))).andReturn("20100615");
		expect(set.getString(eq("bar"))).andReturn("183045");
		replay(set);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(2010, 5, 15, 18, 30, 45);
		value.update();
		assertEquals(c.getTime(), value.get());
		
		verify(set);
	}
	
	@Test (expected=ValueUpdateException.class)
	public void testUpdate_ThrowsFormatException() throws Exception {
		expect(set.getString(eq("foo"))).andReturn("20100615");
		expect(set.getString(eq("bar"))).andReturn("foo");
		replay(set);
		value.update();
	}

}
