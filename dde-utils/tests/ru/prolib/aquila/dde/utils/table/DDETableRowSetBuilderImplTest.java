package ru.prolib.aquila.dde.utils.table;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.ValidatorStub;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.DDETableImpl;

/**
 * 2012-08-10<br>
 * $Id: DDETableRowSetBuilderImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class DDETableRowSetBuilderImplTest {
	private IMocksControl control;
	private Validator validator;
	private DDETableRowSetBuilderImpl handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validator = control.createMock(Validator.class);
		handler = new DDETableRowSetBuilderImpl(1, 1, validator);
	}
	
	@Test
	public void testCounstruct0() throws Exception {
		handler = new DDETableRowSetBuilderImpl();
		assertEquals(1, handler.getFirstRow());
		assertEquals(1, handler.getFirstCol());
		assertEquals(new ValidatorStub(true), handler.getValidator());
		assertEquals(new HashSet<String>(), handler.getHeaders());
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<Integer> vCol = new Variant<Integer>()
			.add(null)
			.add(1)
			.add(-2);
		Variant<Integer> vRow = new Variant<Integer>(vCol)
			.add(0)
			.add(null)
			.add(-1);
		Variant<Validator> vVal = new Variant<Validator>(vRow)
			.add(null)
			.add(validator);
		Variant<?> iterator = vVal;
		int exceptionCnt = 0;
		DDETableRowSetBuilderImpl found = null;
		do {
			try {
				found = new DDETableRowSetBuilderImpl(vCol.get(),
						vRow.get(), vVal.get());
			} catch ( Exception e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertEquals(1, found.getFirstCol());
		assertEquals(0, found.getFirstRow());
		assertSame(validator, found.getValidator());
		assertEquals(new HashSet<String>(), handler.getHeaders());
	}
	
	@Test
	public void testCreateRowSet_FirstTimeIfValid() throws Exception {
		DDETable table = createTable(new Object[][] {
				{ "#", "name", "price" },
				{ 1,   "GAZP",  30.12d }
		}, "R1C1:R2C3");
		expect(validator.validate(notNull())).andDelegateTo(new Validator() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean validate(Object object) {
				Set<String> header = (Set<String>) object;
				assertEquals(3, header.size());
				assertTrue(header.contains("#"));
				assertTrue(header.contains("name"));
				assertTrue(header.contains("price"));
				return true;
			}
		});
		control.replay();
		
		RowSet rs = handler.createRowSet(table);
		
		control.verify();
		Set<String> expected = new HashSet<String>();
		expected.add("#");
		expected.add("name");
		expected.add("price");
		assertEquals(expected, handler.getHeaders());
		assertRowSet(new String[] { "#", "name", "price" },
				new Object[][] {{ 1, "GAZP", 30.12d }}, rs);
	}
	
	@Test
	public void testCreateRowSet_FirstTimeIfNotValid() throws Exception {
		DDETable table = createTable(new Object[][] {
				{ "#", "name", "price" },
				{ 1,   "GAZP",  30.12d }
		}, "R1C1:R2C3");
		expect(validator.validate(notNull())).andDelegateTo(new Validator() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean validate(Object object) {
				Set<String> header = (Set<String>) object;
				assertEquals(3, header.size());
				assertTrue(header.contains("#"));
				assertTrue(header.contains("name"));
				assertTrue(header.contains("price"));
				return false;
			}
		});
		control.replay();
		
		RowSet rs = handler.createRowSet(table);
		assertSame(DDETableRowSetBuilderImpl.EMPTY_SET, rs);
		assertEquals(new HashSet<String>(), handler.getHeaders());
		
		control.verify();
		assertEquals(new HashSet<String>(), handler.getHeaders());
	}
	
	@Test
	public void testCreateRowSet_ThrowsIfValidatorThrows() throws Exception {
		ValidatorException expected = new ValidatorException("test");
		DDETable table = createTable(new Object[][] {
				{ "#", "name", "price" }, }, "R1C1:R1C3");
		expect(validator.validate(notNull())).andThrow(expected);
		control.replay();
		
		try {
			handler.createRowSet(table);
			fail("Expected: " + DDEException.class.getSimpleName());
		} catch ( DDEException e ) {
			assertSame(expected, e.getCause());
			assertEquals(new HashSet<String>(), handler.getHeaders());
			control.verify();
		}
	}

	@Test
	public void testCreateRowSet_ShiftedRight() throws Exception {
		control.resetToNice();
		expect(validator.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(createTable(new Object[][]
		        {{ "#", "name", "price" }}, "R1C1:R10C10"));
		
		DDETable table = createTable(new Object[][] {
				{ "SBER", 97.156d },
				{ "MTSS", 12.340d },
		}, "R2C2:R3C3");
		
		RowSet rs = handler.createRowSet(table);
		
		Set<String> expected = new HashSet<String>();
		expected.add("#");
		expected.add("name");
		expected.add("price");
		assertEquals(expected, handler.getHeaders());
		assertRowSet(new String[] { "#", "name", "price" }, new Object[][] {
				{ null, "SBER", 97.156d },
				{ null, "MTSS", 12.340d }}, rs);
		
	}
	
	@Test
	public void testCreateRowSet_ChangeColumnOrder() throws Exception {
		control.resetToNice();
		expect(validator.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(createTable(new Object[][]
   		        {{ "#", "name", "price" }}, "R1C1:R1C3"));
		
		DDETable table = createTable(new Object[][] {
				{ "price", "name" },
				{ 18.34d,  "GAZP" },
		}, "R1C2:R2C3");
		
		RowSet rs = handler.createRowSet(table);
		
		Set<String> expected = new HashSet<String>();
		expected.add("name");
		expected.add("price");
		assertEquals(expected, handler.getHeaders());
		assertRowSet(new String[] { "#", "price", "name" },
				new Object[][] {{ null, 18.34d, "GAZP" }}, rs);
	}
	
	@Test
	public void testCreateRowSet_AddColumnIfValid() throws Exception {
		control.resetToNice();
		expect(validator.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(createTable(new Object[][]
   		        {{ "#", "name", "price" }}, "R1C1:R1C3"));
		
		DDETable table = createTable(new Object[][] {
				{ "price", "update" },
				{ 18.34d,  true     },
		}, "R1C3:R2C4");
		control.resetToStrict();
		expect(validator.validate(notNull())).andDelegateTo(new Validator() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean validate(Object object) {
				Set<String> header = (Set<String>) object;
				assertEquals(2, header.size());
				assertTrue(header.contains("price"));
				assertTrue(header.contains("update"));
				return true;
			}
		});
		control.replay();

		RowSet rs = handler.createRowSet(table);
		
		control.verify();
		Set<String> expected = new HashSet<String>();
		expected.add("price");
		expected.add("update");
		assertEquals(expected, handler.getHeaders());
		assertRowSet(new String[] { "price", "update" },
				new Object[][] {{ 18.34d, true }}, rs);
	}
	
	@Test
	public void testCreateRowSet_AddColumnIfNotValid() throws Exception {
		control.resetToNice();
		expect(validator.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(createTable(new Object[][]
   		        {{ "#", "name", "price" }}, "R1C1:R2C2"));
		
		DDETable table = createTable(new Object[][] {
				{ "price", "update" },
				{ 18.34d,  true     },
		}, "R1C3:R2C4");
		control.resetToStrict();
		expect(validator.validate(notNull())).andDelegateTo(new Validator() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean validate(Object object) {
				Set<String> header = (Set<String>) object;
				assertEquals(2, header.size());
				assertTrue(header.contains("price"));
				assertTrue(header.contains("update"));
				return false;
			}
		});
		control.replay();

		/*RowSet rs = */handler.createRowSet(table);
		
		control.verify();
		assertEquals(new HashSet<String>(), handler.getHeaders());
	}

	@Test (expected=XltItemFormatException.class)
	public void testCreateRowSet_ThrowsIfUnknownItemFormat() throws Exception {
		handler.createRowSet(createTable(new Object[][]
   		        {{ "#", "name", "price" }}, "foobar:any"));
	}
	
	/**
	 * Создать тестовую таблицу.
	 * <p>
	 * @param cells ячейки таблицы
	 * @param item строка субъекта
	 * @return таблица
	 */
	private DDETable createTable(Object cells[][], String item) {
		Object list[] = new Object[cells.length * cells[0].length];
		int cols = cells[0].length;
		for ( int r = 0; r < cells.length; r ++ ) {
			for ( int c = 0; c < cols; c ++ ) {
				list[r * cols + c] = cells[r][c];
			}
		}
		return new DDETableImpl(list, "unused", item, cols);
	}
	
	/**
	 * Проверить соответствие набора записей.
	 * <p>
	 * @param header имя колонки -> индекс в наборе
	 * @param expected ожидаемые значения (индекс в строке -> имя колонки)
	 * @param rs набор записей 
	 */
	private void assertRowSet(String header[],
							  Object expected[][],
							  RowSet rs) throws Exception
	{
		for ( int row = 0; row < expected.length; row ++ ) {
			String msg = "At R" + row;
			assertTrue(msg, rs.next());
			for ( int col = 0; col < expected[0].length; col ++ ) {
				msg = "At R" + row + "C" + col;
				assertEquals(msg, expected[row][col], rs.get(header[col]));
			}
		}
		assertFalse(rs.next());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		DDETable t1 = createTable(new Object[][]{{ "#", "name", }},"R1C1:R1C1");
		DDETable t2 = createTable(new Object[][]{{ "one", "two" }},"R1C1:R1C1");
		Validator validator2 = control.createMock(Validator.class);
		expect(validator.validate(notNull())).andStubReturn(true);
		expect(validator2.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(t1);

		Variant<Integer> vCol = new Variant<Integer>()
			.add(1)
			.add(0);
		Variant<Integer> vRow = new Variant<Integer>(vCol)
			.add(5)
			.add(1);
		Variant<Validator> vVal = new Variant<Validator>(vRow)
			.add(validator)
			.add(validator2);
		Variant<DDETable> vTable = new Variant<DDETable>(vVal)
			.add(t1)
			.add(t2);
		Variant<?> iterator = vTable;
		int foundCnt = 0;
		DDETableRowSetBuilderImpl x = null, found = null;
		do {
			x = new DDETableRowSetBuilderImpl(vCol.get(),vRow.get(),vVal.get());
			x.createRowSet(vTable.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1, found.getFirstCol());
		assertEquals(1, found.getFirstRow());
		assertSame(validator, found.getValidator());
		Set<String> expected = new HashSet<String>();
		expected.add("#");
		expected.add("name");
		assertEquals(expected, found.getHeaders());
	}
	
	@Test
	public void testHashCode() throws Exception {
		DDETable t1 = createTable(new Object[][]{{ "#","name"}},"R20C10:R2C1");
		Map<String, Integer> header = new HashMap<String, Integer>();
		header.put("#", 0);
		header.put("name", 1);
		handler = new DDETableRowSetBuilderImpl(10, 20, validator);
		expect(validator.validate(notNull())).andStubReturn(true);
		control.replay();
		handler.createRowSet(t1);
		assertEquals(header.keySet(), handler.getHeaders());
		
		int hashCode = new HashCodeBuilder(20121107, /*0*/63533)
			.append(10)
			.append(20)
			.append(validator)
			.append(header)
			.toHashCode();
		assertEquals(hashCode, handler.hashCode());
	}

}
