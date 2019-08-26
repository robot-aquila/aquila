package ru.prolib.aquila.data.storage.hsql;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.data.Sequence;

public class HSQLSequenceRegistryTest {
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
	private IMocksControl control;
	private HSQLSequence seqMock1, seqMock2, seqMock3;
	private Map<String, HSQLSequence> registry_stub;
	private HSQLSequenceRegistry service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		seqMock1 = control.createMock(HSQLSequence.class);
		seqMock2 = control.createMock(HSQLSequence.class);
		seqMock3 = control.createMock(HSQLSequence.class);
		registry_stub = new HashMap<>();
		service = new HSQLSequenceRegistry(connection, "buz_", registry_stub);
	}
	
	@Test
	public void testGet_Existing() throws Exception {
		registry_stub.put("cucumber", seqMock1);
		registry_stub.put("bombarda", seqMock2);
		registry_stub.put("tutumbr5", seqMock3);
		
		assertSame(seqMock1, service.get("cucumber"));
		assertSame(seqMock2, service.get("bombarda"));
		assertSame(seqMock3, service.get("tutumbr5"));
	}
	
	@Test
	public void testGet_CreateNew() throws Exception {
		registry_stub.put("cucumber", seqMock1);
		registry_stub.put("bombarda", seqMock2);
		registry_stub.put("tutumbr5", seqMock3);

		Sequence x = service.get("bambarbiya");
		
		assertSame(x, registry_stub.get("bambarbiya"));
		assertSame(x, service.get("bambarbiya"));
		assertEquals(1L, x.next());
		assertEquals(2L, x.next());
		assertEquals(3L, x.next());
		assertEquals(4L, x.next());
		assertEquals(5L, x.next());
		
		HSQLSequenceRegistry service2 = new HSQLSequenceRegistry(connection, "buz_");
		Sequence x2 = service2.get("bambarbiya");
		assertEquals(6L, x2.next());
		assertEquals(7L, x2.next());
		assertEquals(8L, x2.next());
	}

}
