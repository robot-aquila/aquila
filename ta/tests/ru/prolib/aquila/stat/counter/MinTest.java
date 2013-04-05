package ru.prolib.aquila.stat.counter;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;
import org.apache.log4j.*;
import org.easymock.*;
import org.junit.*;

/**
 * 2012-02-02
 * $Id: MinTest.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class MinTest {
	IMocksControl control;
	Counter<Double> counter;
	Observer observer;
	Min value;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		counter = control.createMock(Counter.class);
		observer = control.createMock(Observer.class);
		value = new Min(counter);
	}
	
	@Test
	public void testGetCounter() throws Exception {
		assertSame(counter, value.getCounter());
	}

	@Test
	public void testStartService() throws Exception {
		counter.addObserver(value);
		control.replay();
		
		value.startService(null);
		
		control.verify();
	}
	
	@Test
	public void testStopService() throws Exception {
		counter.deleteObserver(value);
		control.replay();
		
		value.stopService();
		
		control.verify();
	}

	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Object fix[][] = {
			// source counter, expected max, expected update
			{ 100d, 100d, true },
			{ 120d, 100d, true },
			{ 105d, 100d, true },
			{ 110d, 100d, true },
			{  85d,  85d, true },
			{  70d,  70d, true },
			{ 110d,  70d, true },
			{ 120d,  70d, true },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			expect(counter.getValue()).andReturn((Double)fix[i][0]);
			if ( (Boolean)fix[i][2] == true ) {
				observer.update(value, null);
				expectLastCall().andDelegateTo(
						new TestCounterValue<Double>((Double) fix[i][1]));
			}
			
		}
		control.replay();

		value.addObserver(observer);
		for ( int i = 0; i < fix.length; i ++ ) {
			value.update(null, null);
			assertEquals("At #" + i, (Double)fix[i][1], value.getValue(), 0.1d);
		}
			
		control.verify();
	}

}
