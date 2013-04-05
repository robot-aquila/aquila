package ru.prolib.aquila.ta.ds.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;

public class BarWriterJdbcTest {
	static private final String tableName = "test_quote_5m";
	static private final SimpleDateFormat df =
			new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private BarWriterJdbc writer;
	static private DbAccessor dba;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dba = new DbAccessorImpl(System.getProperty("test.database"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dba.get().close();
	}

	@Before
	public void setUp() throws Exception {
		writer = new BarWriterJdbc(dba, tableName);
	}

	@After
	public void tearDown() throws Exception {
		dba.get().createStatement().executeUpdate("TRUNCATE TABLE " + tableName);
	}
	
	@Test
	public void testAccessors() {
		assertSame(dba, writer.getConnectionAccessor());
		assertEquals(tableName, writer.getTableName());
	}
	
	@Test
	public void testFlush() throws Exception {
		assertFalse(writer.flush());
	}
	
	@Test
	public void testAddBar() throws Exception {
		Candle bar = new Candle(df.parse("2012-01-09 14:02:05"),
				100d, 200d, 50d, 80d, 100L);
		assertTrue(writer.addBar(bar));
		
		ResultSet rs = dba.get().createStatement()
			.executeQuery("SELECT * FROM "+ tableName +" ORDER BY period_time");
		assertTrue(rs.next());
		assertEquals(df.parse("2012-01-09 14:02:05"),
				rs.getTimestamp("period_time"));
		assertEquals(100d, rs.getDouble("open"), 0.001d);
		assertEquals(200d, rs.getDouble("high"), 0.001d);
		assertEquals( 50d, rs.getDouble("low"), 0.001d);
		assertEquals( 80d, rs.getDouble("close"), 0.001d);
		assertEquals(100L, rs.getLong("volume"));
		assertFalse(rs.next());
	}

}
