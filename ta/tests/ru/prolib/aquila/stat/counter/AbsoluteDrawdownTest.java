package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;
import org.apache.log4j.*;
import org.easymock.*;
import org.junit.*;

public class AbsoluteDrawdownTest {
	IMocksControl control;
	Observer observer;
	AbsoluteDrawdown value;
	TestCounter<Double> counter;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		counter = new TestCounter<Double>();
		value = new AbsoluteDrawdown(counter);
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Double fix[][] = {
			// counter value, expected drawdown
			{ 120d,   0d },
			{ 105d,  15d },
			{ 110d,  10d },
			{ 100d,  20d },
			{ 140d,   0d },
			{ 130d,  10d },
			{ 125d,  15d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			observer.update(same(value), eq(null));
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
		value.stopService();
		control.verify();
	}

}
