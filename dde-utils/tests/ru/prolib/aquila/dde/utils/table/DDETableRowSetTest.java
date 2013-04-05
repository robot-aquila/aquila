package ru.prolib.aquila.dde.utils.table;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.DDETableImpl;

/**
 * 2012-08-10<br>
 * $Id: DDETableRowSetTest.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDETableRowSetTest {
	private DDETable table;
	private Map<String, Integer> hdr;
	private DDETableRowSet rs;

	@Before
	public void setUp() throws Exception {
		hdr = new HashMap<String, Integer>();
		hdr.put("age", 5);
		hdr.put("art", 4);
		hdr.put("name", 3);
		hdr.put("code", 2);
		hdr.put("unk2", 1);
		hdr.put("unk1", 0);
		
		DDETable source = new DDETableImpl(new Object[] {
				"one",	"two",		"three", // headers
				"001",	"vasya",	123,
				"007",	"bond",		777,
				"911",	"twins",	911,
				"999",	"gold",		987
			}, "foo", "bar", 3); 
		table = new DDETableShift(source, 2, -1);
		rs = new DDETableRowSet(table, hdr);
	}
	
	@Test
	public void testIterate() throws Exception {
		String header[] = { "unk1", "unk2", "code", "name", "art", "age" };
		Object expected[][] = {
				{ null, null, "001", "vasya", 123 },
				{ null, null, "007", "bond",  777 },
				{ null, null, "911", "twins", 911 },
				{ null, null, "999", "gold",  987 }
		};
		
		assertRowSet(header, expected);
		assertFalse(rs.next());
		assertFalse(rs.next());
		rs.reset();
		assertRowSet(header, expected);
	}
	
	/**
	 * Проверить соответствие набора записей.
	 * <p>
	 * @param header имя колонки -> индекс в наборе
	 * @param expected ожидаемые значения (индекс в строке -> имя колонки) 
	 */
	private void assertRowSet(String header[], Object expected[][]) {
		for ( int row = 0; row < expected.length; row ++ ) {
			String msg = "At R" + row;
			assertTrue(msg, rs.next());
			for ( int col = 0; col < expected[0].length; col ++ ) {
				msg = "At R" + row + "C" + col;
				assertEquals(msg, expected[row][col], rs.get(header[col]));
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Map<String, Integer>> vHdr = new Variant<Map<String, Integer>>()
			.add(null)
			.add(hdr)
			.add(new HashMap<String, Integer>());
		Variant<DDETable> vTab = new Variant<DDETable>(vHdr)
			.add(null)
			.add(new DDETableImpl(new Object[] {"one","two"}, "cho", "ppa", 2))
			.add(table);
		int foundCnt = 0;
		DDETableRowSet found = null, x = null;
		do {
			x = new DDETableRowSet(vTab.get(), vHdr.get());
			if ( rs.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( vTab.next() );
		assertEquals(1, foundCnt);
		assertSame(table, found.getTable());
		assertSame(hdr, found.getHeaders());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(rs.equals(rs));
		assertFalse(rs.equals(null));
		assertFalse(rs.equals(this));
		DDETableRowSet rs2 = new DDETableRowSet(table, hdr);
		assertTrue(rs.equals(rs2));
		rs2.next(); // при несовпадении указателей - объекты несоответствующие
		assertFalse(rs.equals(rs2));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int expectedHashCode = new HashCodeBuilder(20121107, 144411)
			.append(table)
			.append(hdr)
			.append(2) // значение указателя
			.toHashCode();
		int initialHashCode = rs.hashCode();
		rs.next(); rs.next(); rs.next();
		assertEquals(expectedHashCode, rs.hashCode());
		assertFalse(initialHashCode == rs.hashCode());
	}

}
