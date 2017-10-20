package ru.prolib.aquila.t2q;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-01-30<br>
 * $Id: T2QServiceStarterTest.java 461 2013-01-30 17:07:04Z whirlwind $
 */
public class T2QServiceStarterTest {
	private static IMocksControl control;
	private static T2QService service;
	private static T2QServiceStarter starter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		service = control.createMock(T2QService.class);
		starter = new T2QServiceStarter(service, "helolo");
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<T2QService> vService = new Variant<T2QService>()
			.add(service)
			.add(null);
		Variant<String> vParam = new Variant<String>(vService)
			.add(null)
			.add("params");
		Variant<?> iterator = vParam;
		int exceptionCnt = 0;
		T2QServiceStarter found = null;
		do {
			try {
				found = new T2QServiceStarter(vService.get(), vParam.get());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(service, found.getService());
		assertEquals("params", found.getConnectionParam());
	}
	
	@Test
	public void testStart() throws Exception {
		service.connect("helolo");
		control.replay();
		starter.start();
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		service.disconnect();
		control.replay();
		starter.stop();
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(starter.equals(starter));
		assertFalse(starter.equals(null));
		assertFalse(starter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<T2QService> vSvc = new Variant<T2QService>()
			.add(service)
			.add(control.createMock(T2QService.class));
		Variant<String> vPar = new Variant<String>(vSvc)
			.add("helolo")
			.add("unknown");
		Variant<?> iterator = vPar;
		int foundCnt = 0;
		T2QServiceStarter x = null, found = null;
		do {
			x = new T2QServiceStarter(vSvc.get(), vPar.get());
			if ( starter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(service, found.getService());
		assertEquals("helolo", found.getConnectionParam());
	}

}
