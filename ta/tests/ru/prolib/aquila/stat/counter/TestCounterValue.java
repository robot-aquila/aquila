package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;
import static org.junit.Assert.*;

public class TestCounterValue<T> implements Observer {
	private final Double doubleAccuracy;
	private final T expectedValue;
	
	/**
	 * Конструктор для тестирования значений простых типов.
	 * @param expectedValue
	 */
	public TestCounterValue(T expectedValue) {
		this(expectedValue, null);
	}
	
	/**
	 * Конструктор для тестирования значений вещественных типов.
	 * @param expectedValue
	 * @param doubleAccuracy
	 */
	public TestCounterValue(T expectedValue, Double doubleAccuracy) {
		this.expectedValue = expectedValue;
		this.doubleAccuracy = doubleAccuracy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		Counter<T> counter = (Counter<T>) o;
		if ( doubleAccuracy != null ) {
			assertEquals((Double)expectedValue,
					(Double)counter.getValue(), (Double)doubleAccuracy);
		} else {
			assertEquals(expectedValue, counter.getValue());
		}
	}

}
