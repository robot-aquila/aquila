package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;
import org.easymock.IMocksControl;
import org.junit.*;

public class SumTest {
	IMocksControl control;
	Observer observer;
	TestCounter<Double> counter;
	Validator validator;
	Sum value;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		validator = new ValidatorGtZero();
		counter = new TestCounter<Double>();
		value = new Sum(counter, validator);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(validator, value.getValidator());
		assertSame(counter, value.getCounter());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		value = new Sum(counter);
		assertSame(counter, value.getCounter());
		ValidatorAll validator = (ValidatorAll) value.getValidator();
		assertNotNull(validator);
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertEquals(0.0d, value.getValue(), 0.01d);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartService() throws Exception {
		Counter<Double> counter = control.createMock(Counter.class);
		value = new Sum(counter);
		counter.addObserver(value);
		control.replay();
		
		value.startService(null);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStopService() throws Exception {
		Counter<Double> counter = control.createMock(Counter.class);
		value = new Sum(counter);
		counter.deleteObserver(value);
		control.replay();
		
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testUpdate() throws Exception {
		Double fix[][] = {
			// counter, expected sum
			{ 125d, 125d },
			{  0d,  125d },
			{ -20d, 125d },
			{  10d, 135d },
			{  -1d, 135d },
			{  65d, 200d }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			observer.update(value, null);
			expectLastCall()
				.andDelegateTo(new TestCounterValue<Double>(fix[i][1]));
		}
		control.replay();
		
		value.addObserver(observer);
		value.startService(null);
		for ( int i = 0; i < fix.length; i ++ ) {
			counter.setValueAndNotifyObservers(fix[i][0]);
			assertEquals("At #" + i, fix[i][1], value.getValue(), 0.01d);
		}
		
		control.verify();
	}

}
