package ru.prolib.aquila.t2q.jqt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.t2q.*;

/**
 * 2013-01-31<br>
 * $Id: JQTServiceFactoryTest.java 493 2013-02-06 05:37:55Z whirlwind $
 */
public class JQTServiceFactoryTest {
	private static IMocksControl control;
	private static T2QHandler handler;
	private static JQTServiceFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		handler = control.createMock(T2QHandler.class);
		factory = new JQTServiceFactory();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testCreateService() throws Exception {
		T2QService s1,s2;
		s1 = factory.createService(handler);
		s2 = factory.createService(handler);
		assertNotNull(s1);
		assertNotNull(s2);
		assertNotSame(s1, s2);
	}

}
