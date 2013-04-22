package ru.prolib.aquila.quik.dde;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.junit.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;

public class RowDataConverterTest {
	private SimpleDateFormat format;
	private Row row;
	private Map<String, Object> data;
	private RowDataConverter converter;
	
	@Before
	public void setUp() throws Exception {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		data = new HashMap<String, Object>();
		row = new SimpleRow(data);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
	}
	
	@Test
	public void testGetString() throws Exception {
		data.put("Name", "Vasya");
		assertEquals("Vasya", converter.getString(row, "Name"));
	}
	
	@Test (expected=RowNullValueException.class)
	public void testGetString_ThrowsIfNull() throws Exception {
		data.put("NullName", null);
		converter.getString(row, "NullName");
	}
	
	@Test (expected=RowDataTypeMismatchException.class)
	public void testGetString_ThrowsIfTypeMismatch() throws Exception {
		data.put("BadName", new Double(12.34));
		converter.getString(row, "BadName");
	}
	
	@Test
	public void testGetTime_PermNull() throws Exception {
		Variant<Object> vDate = new Variant<Object>()
			.add("")
			.add("2013-06-01")
			.add("01.06.2013")
			.add(null)
			.add(new Double(18.92d));
			Variant<Object> vTime = new Variant<Object>(vDate)
				.add("")
				.add("23:45:30")
				.add("23 hrs. 45 mins.")
				.add(null)
				.add(new Double(32.48d));
			Variant<?> iterator = vTime;
			Set<Date> found = new HashSet<Date>();
			do {
				data.put("date", vDate.get());
				data.put("time", vTime.get());
				try {
					Date x = converter.getTime(row, "date", "time", true);
					found.add(x);
				} catch ( Exception e ) { }
			} while ( iterator.next() );
			assertEquals(2, found.size());
			assertTrue(found.contains(null));
			assertTrue(found.contains(format.parse("2013-06-01 23:45:30")));
		}
		
	@Test
	public void testGetTime_RestNull() throws Exception {
		Variant<Object> vDate = new Variant<Object>()
			.add("")
			.add("2013-06-01")
			.add("01.06.2013")
			.add(null)
			.add(new Double(18.92d));
		Variant<Object> vTime = new Variant<Object>(vDate)
			.add("")
			.add("23:45:30")
			.add("23 hrs. 45 mins.")
			.add(null)
			.add(new Double(32.48d));
		Variant<?> iterator = vTime;
		Set<Date> found = new HashSet<Date>();
		do {
			data.put("date", vDate.get());
			data.put("time", vTime.get());
			try {
				Date x = converter.getTime(row, "date", "time", false);
				found.add(x);
			} catch ( Exception e ) { }
		} while ( iterator.next() );
		assertEquals(1, found.size());
		assertTrue(found.contains(format.parse("2013-06-01 23:45:30")));
	}

}
