package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

public class CountTest {
	IMocksControl control;
	TestCounter<Double> counter;
	Validator validator;
	Count value;
	Observer observer;

	@Before
	public void setUp() throws Exception {
		counter = new TestCounter<Double>();
		control = createStrictControl();
		validator = control.createMock(Validator.class);
		observer = control.createMock(Observer.class);
		value = new Count(counter, validator);
	}
	
	@Test
	public void testCounstruct2() throws Exception {
		assertSame(counter, value.getCounter());
		assertSame(validator, value.getValidator());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(counter, value.getCounter());
		value = new Count(counter);
		ValidatorAll validator = (ValidatorAll) value.getValidator();
		assertNotNull(validator);
	}
	
	@Test
	public void testGetValue_ZeroBeforeUpdate() throws Exception {
		assertEquals(0d, value.getValue(), 0.01d);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartService_Ok() throws Exception {
		Counter<Double> counter = control.createMock(Counter.class);
		value = new Count(counter, validator);
		counter.addObserver(value);
		control.replay();
		
		value.startService(null);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStopService_Ok() throws Exception {
		Counter<Double> counter = control.createMock(Counter.class);
		value = new Count(counter, validator);
		counter.deleteObserver(value);
		control.replay();
		
		value.stopService();
		
		control.verify();
	}

	@Test
	public void testUpdate_Ok() throws Exception {
		Object fix[][] = {
			// counter value, validator result, expected result
			{ 100d, true,  1.0d },
			{ 200d, false, 1.0d },
			{ 220d, false, 1.0d },
			{ 220d, true,  2.0d },
			{ 240d, false, 2.0d },
			{ 240d, true,  3.0d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			expect(validator.shouldCounted((Double)fix[i][0]))
				.andReturn((Boolean)fix[i][1]);
			observer.update(value, null);
			expectLastCall()
			  .andDelegateTo(new TestCounterValue<Double>((Double)fix[i][2]));
		}
		control.replay();
		
		value.addObserver(observer);
		value.startService(null);
		for ( int i = 0; i < fix.length; i ++ ) {
			counter.setValueAndNotifyObservers((Double)fix[i][0]);
			assertEquals("At #" + i, (Double)fix[i][2], value.getValue());
		}
		
		control.verify();
	}

}
