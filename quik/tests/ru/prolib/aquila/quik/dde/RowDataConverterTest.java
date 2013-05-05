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
	public void testGetTime_PermitNull() throws Exception {
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
	public void testGetTime_RestrictNull() throws Exception {
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
		int foundCnt = 0;
		Date found = null;
		do {
			data.put("date", vDate.get());
			data.put("time", vTime.get());
			try {
				Date x = converter.getTime(row, "date", "time", false);
				found = x;
				foundCnt ++;
			} catch ( Exception e ) { }
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(format.parse("2013-06-01 23:45:30"), found);
	}

	@Test
	public void testGetDouble() throws Exception {
		data.put("Number", 12.34d);
		assertEquals(12.34d, converter.getDouble(row, "Number"), 0.01d);
	}
	
	@Test (expected=RowNullValueException.class)
	public void testGetDouble_ThrowsIfNull() throws Exception {
		data.put("NullNumber", null);
		converter.getDouble(row, "NullNumber");
	}
	
	@Test (expected=RowDataTypeMismatchException.class)
	public void testGetDouble_ThrowsIfTypeMismatch() throws Exception {
		data.put("BadNumber", "zulu4");
		converter.getDouble(row, "BadNumber");
	}
	
	@Test
	public void testGetDoubleOrNull_Normal() throws Exception {
		data.put("Number", 12.34d);
		assertEquals(12.34d, converter.getDoubleOrNull(row, "Number"), 0.01d);
	}
	
	@Test
	public void testGetDoubleOrNull_NullIfNotExists() throws Exception {
		assertNull(converter.getDoubleOrNull(row, "Number"));
	}
	
	@Test
	public void testGetDoubleOrNull_NullIfNull() throws Exception {
		data.put("NullNumber", null);
		assertNull(converter.getDoubleOrNull(row, "NullNumber"));
	}
	
	@Test
	public void testGetDoubleOrNull_NullIfTypeMismatch() throws Exception {
		data.put("BadNumber", "foobar");
		assertNull(converter.getDoubleOrNull(row, "BadNumber"));
	}
	
	@Test
	public void testGetDoubleOrNull_NullIfZero() throws Exception {
		data.put("ZeroNumber", 0.0d);
		assertNull(converter.getDoubleOrNull(row, "ZeroNumber"));
	}

	@Test
	public void testGetLong() throws Exception {
		data.put("Number", 15.0d);
		assertEquals(new Long(15L), converter.getLong(row, "Number"));
	}
	
	@Test (expected=RowNullValueException.class)
	public void testGetLong_ThrowsIfNull() throws Exception {
		data.put("NullNumber", null);
		converter.getLong(row, "NullNumber");
	}

	@Test (expected=RowDataTypeMismatchException.class)
	public void testGetLong_ThrowsIfTypeMismatch() throws Exception {
		data.put("BadNumber", this);
		converter.getLong(row, "BadNumber");
	}
	
	@Test
	public void testGetInteger() throws Exception {
		data.put("Number",19.0d);
		assertEquals(new Integer(19), converter.getInteger(row, "Number"));
	}
	
	@Test (expected=RowNullValueException.class)
	public void testGetInteger_ThrowsIfNull() throws Exception {
		data.put("NullNumber", null);
		converter.getInteger(row, "NullNumber");
	}
	
	@Test (expected=RowDataTypeMismatchException.class)
	public void testGetInteger_ThrowsIfTypeMismatch() throws Exception {
		data.put("BadNumber", "zekakka");
		converter.getInteger(row, "BadNumber");
	}
	
	@Test
	public void testGetStringMappedTo() throws Exception {
		data.put("bakhta", "yes");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yes", 15);
		map.put("no", 24);
		assertEquals(15, converter.getStringMappedTo(row, "bakhta", map));
	}
	
	@Test (expected=RowNullValueException.class)
	public void testGetStringMappedTo_ThrowsIfNullValue() throws Exception {
		data.put("bakhta", null);
		converter.getStringMappedTo(row,"bakhta",new HashMap<String, Object>());
	}
	
	@Test (expected=RowDataTypeMismatchException.class)
	public void testGetStringMappedTo_ThrowsIfTypeMismatch() throws Exception {
		data.put("bakhta", new Integer(12));
		converter.getStringMappedTo(row,"bakhta",new HashMap<String, Object>());
	}
	
	@Test
	public void testGetStringMappedTo_PermitMappingToNull() throws Exception {
		data.put("bakhta", "yes");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yes", null);
		map.put("no", 24);
		assertNull(converter.getStringMappedTo(row, "bakhta", map));
	}

}
