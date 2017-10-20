package ru.prolib.aquila.dde.utils.table;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.DDETableImpl;

/**
 * Тест различных вариантов сдвига области. Всего возможных вариантов 8:
 * 
 * 		1 2 3
 *      4 X 5
 *      6 7 8
 * 
 * где X - область таблица, а цифры - варианты расположения левого-верхнего
 * угла оригинальной таблицы.
 *
 */
public class DDETableShiftTest {
	private DDETable source;
	private DDETableShift shift;

	@Before
	public void setUp() throws Exception {
		source = new DDETableImpl(new Object[] {
			"#",	"name",		"age",	"city", 
			  1,	"vasya",	25,		"new york",
			  2,	"petya",	80,		"abakan",
			  3,	"masha",	18,		"chicago",
			}, "foo", "bar", 4);
		shift = new DDETableShift(source, 4, 1);
	}
	
	@Test
	public void testShift_V8() throws Exception {
		Object expected[][] = {
				{ 25,		"new york" },
				{ 80,		"abakan" },
				{ 18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, -2, -1));
	}

	@Test
	public void testShift_V7() throws Exception {
		Object expected[][] = {
				{ 1,	"vasya", 	25,		"new york" },
				{ 2, 	"petya",	80,		"abakan" },
				{ 3,	"masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, 0, -1));
	}

	@Test
	public void testShift_V6() throws Exception {
		Object expected[][] = {
				{ null, null, 1,	"vasya", 	25,		"new york" },
				{ null, null, 2, 	"petya",	80,		"abakan" },
				{ null, null, 3,	"masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, 2, -1));
	}
	
	@Test
	public void testShift_V5() throws Exception {
		Object expected[][] = {
				{ "name",	"age",	"city" },
				{ "vasya", 	25,		"new york" },
				{ "petya",	80,		"abakan" },
				{ "masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, -1, 0));
	}
	
	@Test
	public void testShift_V4() throws Exception {
		Object expected[][] = {
				{ null, null, "#",	"name",		"age",	"city" },
				{ null, null, 1,	"vasya", 	25,		"new york" },
				{ null, null, 2, 	"petya",	80,		"abakan" },
				{ null, null, 3,	"masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, 2, 0));
	}
	
	@Test
	public void testShift_V3() throws Exception {
		Object expected[][] = {
				{ null,		null },
				{ "age",	"city" },
				{ 25,		"new york" },
				{ 80,		"abakan" },
				{ 18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, -2, 1));
		// additional tests
		assertEquals("age", new DDETableShift(source, -2, 1).getCell(1, 0));
	}
	
	@Test
	public void testShift_V2() throws Exception {
		Object expected[][] = {
				{ null,	null,		null,	null },
				{ "#",	"name",		"age",	"city" },
				{ 1,	"vasya", 	25,		"new york" },
				{ 2, 	"petya",	80,		"abakan" },
				{ 3,	"masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, 0, 1));
	}
	
	@Test
	public void testShift_V1() throws Exception {
		Object expected[][] = {
				{ null, null, null,	null,		null,	null },
				{ null, null, "#",	"name",		"age",	"city" },
				{ null, null, 1,	"vasya", 	25,		"new york" },
				{ null, null, 2, 	"petya",	80,		"abakan" },
				{ null, null, 3,	"masha",	18,		"chicago" },
		};
		assertTable(expected, new DDETableShift(source, 2, 1));
	}
	
	/**
	 * Проверить содержимое таблицы.
	 * <p>
	 * @param cells ожидаемые значения ячеек
	 * @param table таблица для проверки
	 * @throws Exception
	 */
	private final void assertTable(Object[][] cells, DDETable table)
		throws Exception
	{
		assertEquals("foo", table.getTopic());
		assertEquals("bar", table.getItem());
		assertEquals(cells.length, table.getRows());
		assertEquals(cells[0].length, table.getCols());
		for ( int row = 0; row < cells.length; row ++ ) {
			for ( int col = 0; col < cells[0].length; col ++ ) {
				String msg = "At R" + row + "C" + col;
				assertEquals(msg, cells[row][col], table.getCell(row, col));
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<DDETable> vSrc = new Variant<DDETable>()
			.add(null)
			.add(source)
			.add(new DDETableImpl(new Object[] {"#", "name"}, "ubs", "ems", 2));
		Variant<Integer> vScol = new Variant<Integer>(vSrc)
			.add(4)
			.add(0);
		Variant<Integer> vSrow = new Variant<Integer>(vScol)
			.add(1)
			.add(2);
		int foundCnt = 0;
		DDETableShift found = null, x = null;
		do {
			x = new DDETableShift(vSrc.get(), vScol.get(), vSrow.get());
			if ( shift.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vSrow.next() );
		assertEquals(1, foundCnt);
		assertEquals(source, found.getTable());
		assertEquals(4, found.getShiftCols());
		assertEquals(1, found.getShiftRows());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(shift.equals(shift));
		assertFalse(shift.equals(null));
		assertFalse(shift.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, 131133)
			.append(source)
			.append(4)
			.append(1)
			.toHashCode();
		assertEquals(hashCode, shift.hashCode());
	}

}
