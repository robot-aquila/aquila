package ru.prolib.aquila.data.storage.hsql;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HSQLSequenceTest {
	private static Connection connection;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
		connection = DriverManager.getConnection("jdbc:hsqldb:mem:test-db", "SA", "");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if ( connection != null ) {
			connection.close();
		}
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private HSQLSequence service;

	@Before
	public void setUp() throws Exception {
		service = new HSQLSequence(connection, "foo_bar");
	}
	
	@After
	public void tearDown() throws Exception {
		connection.prepareStatement("DROP SEQUENCE \"foo_bar\" IF EXISTS").execute();
	}
	
	@Test
	public void testGetSequenceID() {
		assertEquals("foo_bar", service.getSequenceID());
	}
	
	@Test
	public void testNext_ThrowsIfSQLException() throws Exception {
		eex.expect(RuntimeException.class);
		eex.expectMessage("Query failed");
		
		service.next();
	}

	@Test
	public void testNext() throws Exception {
		connection.prepareStatement("CREATE SEQUENCE \"foo_bar\" AS BIGINT START WITH 1 INCREMENT BY 1").execute();
		
		assertEquals(1L, service.next());
		assertEquals(2L, service.next());
		assertEquals(3L, service.next());
	}

}
