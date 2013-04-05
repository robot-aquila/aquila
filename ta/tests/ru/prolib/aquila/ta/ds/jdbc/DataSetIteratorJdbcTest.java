package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import ru.prolib.aquila.ta.ds.jdbc.DataSetIteratorJdbc;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetIteratorJdbcTest {
	private ResultSet rs;
	private DataSetIteratorJdbc dataSet;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		rs = createMock(ResultSet.class);
		dataSet = new DataSetIteratorJdbc(rs);
	}
	
	@Test
	public void testAccessors() {
		assertSame(rs, dataSet.getResultSet());
	}
	
	@Test
	public void testGetDouble_Ok() throws Exception {
		expect(rs.getDouble("column_name")).andReturn(0.5d);
		replay(rs);
		assertEquals(0.5d, dataSet.getDouble("column_name"), 0.01d);
		verify(rs);
	}
	
	@Test (expected=DataSetIteratorJdbcException.class)
	public void testGetDouble_ThrowsOnError() throws Exception {
		SQLException oe = new SQLException("test error");
		expect(rs.getDouble("column_name")).andThrow(oe);
		replay(rs);
		dataSet.getDouble("column_name");
	}
	
	@Test
	public void testGetString_Ok() throws Exception {
		expect(rs.getString("foobar")).andReturn("zulu4");
		replay(rs);
		assertEquals("zulu4", dataSet.getString("foobar"));
		verify(rs);
	}
	
	@Test (expected=DataSetIteratorJdbcException.class)
	public void testGetString_ThrowsOnError() throws Exception {
		SQLException oe = new SQLException("test error");
		expect(rs.getString("foobar")).andThrow(oe);
		replay(rs);
		dataSet.getString("foobar");
	}

	@Test
	public void testGetDate_Ok() throws Exception {
		Timestamp ts = new Timestamp(100000);
		expect(rs.getTimestamp("zulu")).andReturn(ts);
		replay(rs);
		assertEquals(new Date(100000), dataSet.getDate("zulu"));
		verify(rs);
	}
	
	@Test (expected=DataSetIteratorJdbcException.class)
	public void testGetDate_ThrowsOnError() throws Exception {
		SQLException oe = new SQLException("hello world");
		expect(rs.getTimestamp("foobar")).andThrow(oe);
		replay(rs);
		dataSet.getDate("foobar");
	}
	
	@Test
	public void testNext_Ok() throws Exception {
		expect(rs.next()).andReturn(true);
		expect(rs.next()).andReturn(false);
		replay(rs);
		
		assertTrue(dataSet.next());
		assertFalse(dataSet.next());
		
		verify(rs);
	}
	
	@Test (expected=DataSetIteratorJdbcException.class)
	public void testNext_Throws() throws Exception {
		expect(rs.next()).andThrow(new SQLException("hello"));
		replay(rs);
		dataSet.next();
	}
	
	@Test
	public void testGetLong_Ok() throws Exception {
		expect(rs.getLong("zulu")).andReturn(190L);
		replay(rs);
		
		assertEquals((Long)190L, dataSet.getLong("zulu"));
		
		verify(rs);
	}
	
	@Test (expected=DataSetIteratorJdbcException.class)
	public void testGetLong_Throws() throws Exception {
		SQLException oe = new SQLException("test error");
		expect(rs.getLong("foobar")).andThrow(oe);
		replay(rs);
		
		dataSet.getLong("foobar");
	}
	
	@Test
	public void testClose_Ok() throws Exception {
		rs.close();
		replay(rs);
		
		dataSet.close();
		
		verify(rs);
	}
	
	@Test
	public void testClose_IgnoreErrors() throws Exception {
		rs.close();
		expectLastCall().andThrow(new SQLException("test error"));
		replay(rs);
		
		dataSet.close();
		
		verify(rs);
	}

}
