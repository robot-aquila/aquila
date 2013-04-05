package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.util.ObservableComplex;


public class SubTest {
	IMocksControl control;
	TestCounter<Double> c1,c2;
	ObservableComplex observable;
	Observer observer;
	Sub value;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		c1 = new TestCounter<Double>();
		c2 = new TestCounter<Double>();
		observable = control.createMock(ObservableComplex.class);
		observer = control.createMock(Observer.class);
		value = new Sub(c1, c2);
	}
	
	@Test
	public void testGetCounter() throws Exception {
		assertSame(c1, value.getCounter1());
		assertSame(c2, value.getCounter2());
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Double fix[][] = {
			// c1, c2, expected value
			{ 10.00d, 20.00d, -10.00d },
			{ 15.19d, 16.78d, - 1.59d },
			{ 25.44d, 11.29d,  14.15d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			observer.update(value, null);
			expectLastCall()
				.andDelegateTo(new TestCounterValue<Double>(fix[i][2], 0.01d));
		}
		control.replay();
		
		value.addObserver(observer);
		value.startService(null);
		for ( int i = 0; i < fix.length; i ++ ) {
			c1.setValueAndNotifyObservers(fix[i][0]);
			c2.setValueAndNotifyObservers(fix[i][1]);
			assertEquals("At #" + i, fix[i][2], value.getValue(), 0.01d);
		}
		
		control.verify();
	}
	
	@Test
	public void testStartService() throws Exception {
		value = new Sub(c1, c2, observable);
		observable.addObserver(value);
		observable.addObservable(c1);
		observable.addObservable(c2);
		control.replay();
		
		value.startService(null);
		
		control.verify();
	}
	
	@Test
	public void testStopService() throws Exception {
		value = new Sub(c1, c2, observable);
		observable.addObserver(value);
		observable.addObservable(c1);
		observable.addObservable(c2);
		// stop-service sequence
		observable.deleteObservable(c2);
		observable.deleteObservable(c1);
		observable.deleteObserver(value);
		control.replay();
		
		value.startService(null);
		value.stopService();
		
		control.verify();
	}

}
