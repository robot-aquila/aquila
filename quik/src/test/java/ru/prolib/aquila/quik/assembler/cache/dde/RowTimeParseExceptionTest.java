package ru.prolib.aquila.quik.assembler.cache.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.dde.RowTimeParseException;

public class RowTimeParseExceptionTest {
	private RowTimeParseException exception;

	@Before
	public void setUp() throws Exception {
		exception = new RowTimeParseException("DATE", "TIME",
				"12.01.2013", "23:45", "yyyy-MM-dd", "HH:mm:ss");
	}
	
	@Test
	public void testGetMessage() throws Exception {
		String expected = "Cannot parse [DATE=12.01.2013,TIME=23:45] "
			+ "according to the [yyyy-MM-dd HH:mm:ss] format";
		assertEquals(expected, exception.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vDateId = new Variant<String>()
			.add("DATE")
			.add("zulu");
		Variant<String> vTimeId = new Variant<String>(vDateId)
			.add("TIME")
			.add("moon");
		Variant<String> vDateVal = new Variant<String>(vTimeId)
			.add("12.01.2013")
			.add("2013-01-12");
		Variant<String> vTimeVal = new Variant<String>(vDateVal)
			.add("23:45")
			.add("23:45:19");
		Variant<String> vDateFmt = new Variant<String>(vTimeVal)
			.add("yyyy-MM-dd")
			.add("dd.MM.yy");
		Variant<String> vTimeFmt = new Variant<String>(vDateFmt)
			.add("HH:mm:ss")
			.add("H:m");
		Variant<?> iterator = vTimeFmt;
		int foundCnt = 0;
		RowTimeParseException x = null, found = null;
		do {
			x = new RowTimeParseException(vDateId.get(), vTimeId.get(),
					vDateVal.get(), vTimeVal.get(),
					vDateFmt.get(), vTimeFmt.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("DATE", found.getDateElementId());
		assertEquals("TIME", found.getTimeElementId());
		assertEquals("12.01.2013", found.getDateValue());
		assertEquals("23:45", found.getTimeValue());
		assertEquals("yyyy-MM-dd", found.getDateFormat());
		assertEquals("HH:mm:ss", found.getTimeFormat());
	}

}
