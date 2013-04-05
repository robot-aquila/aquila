package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.Connection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class DbAccessorImplTest {
	IMocksControl control;
	DbAccessorImpl dba;
	Connection conn1,conn2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		conn1 = control.createMock(Connection.class);
		conn2 = control.createMock(Connection.class);
	}
	
	@Test
	public void testGet_ReconnectFirstTime() throws Exception {
		dba = createMockBuilder(DbAccessorImpl.class)
			.withConstructor("foobar")
			.addMockedMethod("reconnect")
			.createMock(control);
		dba.conn = null;
		expect(dba.reconnect()).andReturn(conn1);
		control.replay();
		
		assertSame(conn1, dba.get());
		
		control.verify();
	}
	
	@Test
	public void testGet_ReconnectIfNotValid() throws Exception {
		dba = createMockBuilder(DbAccessorImpl.class)
			.withConstructor("foobar")
			.addMockedMethod("reconnect")
			.createMock(control);
		dba.conn = conn1;
		expect(conn1.isValid(0)).andReturn(false);
		expect(dba.reconnect()).andReturn(conn2);
		control.replay();
		
		assertSame(conn2, dba.get());
		
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		dba = createMockBuilder(DbAccessorImpl.class)
			.withConstructor("foobar")
			.addMockedMethod("reconnect")
			.createMock(control);
		dba.conn = conn1;
		expect(conn1.isValid(0)).andReturn(true);
		control.replay();
		
		assertSame(conn1, dba.get());
		
		control.verify();
 	}

}
