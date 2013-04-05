package ru.prolib.aquila.stat.counter;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.apache.log4j.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.util.ObservableComplex;

public class RatioTest {
	IMocksControl control;
	ObservableComplex observable;
	TestCounter<Double> c1,c2;
	Observer observer;
	Ratio value;
	
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
		observer = control.createMock(Observer.class);
		observable = control.createMock(ObservableComplex.class);
		value = new Ratio(c1, c2);
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
	public void testStartService() throws Exception {
		value = new Ratio(c1, c2, observable);
		observable.addObserver(value);
		observable.addObservable(c1);
		observable.addObservable(c2);
		control.replay();

		value.startService(null);
		
		control.verify();
	}
	
	@Test
	public void testStopService() throws Exception {
		value = new Ratio(c1, c2, observable);
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
	
	@Test
	public void testUpdate_Ok() throws Exception {
		observer.update(value, null);
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(0.2d));
		observer.update(value, null);
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(0.5d));
		observer.update(value, null);
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(0.0d));
		control.replay();
		
		value.addObserver(observer);
		value.startService(null);
		c1.setValueAndNotifyObservers(100d);
		c2.setValueAndNotifyObservers( 20d);
		assertEquals(0.2d, value.getValue(), 0.0001d);
		
		c1.setValueAndNotifyObservers(200d);
		c1.setValueAndNotifyObservers(400d);
		c1.setValueAndNotifyObservers(80d);
		c2.setValueAndNotifyObservers(40d);
		assertEquals(0.5d, value.getValue(), 0.0001d);
		
		// divizion by zero
		c1.setValueAndNotifyObservers(0d);
		c2.setValueAndNotifyObservers(12345d);
		assertEquals(0.0d, value.getValue(), 0.001d);
		
		control.verify();
	}

}
