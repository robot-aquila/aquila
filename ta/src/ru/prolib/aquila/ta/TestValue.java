package ru.prolib.aquila.ta;

import java.util.LinkedList;

/**
 * Тестовый индикатора.
 * Используется в тестах или для грязных хаков. Не TDD но протестирован 100500
 * за счет использования другими тестами.
 *
 * @param <T>
 */
public class TestValue<T> extends ValueImpl<T> {
	private final LinkedList<T> stack = new LinkedList<T>();
	
	public TestValue(T[] array) {
		this(ValueImpl.DEFAULT_ID, array);
	}
	
	public TestValue() {
		this(ValueImpl.DEFAULT_ID, (T[])null);
	}
	
	public TestValue(String valueId) {
		this(valueId, (T[])null);
	}
	
	public TestValue(String valueId, T[] array) {
		super(valueId);
		if ( array != null ) {
			for ( int i = 0; i < array.length; i ++ ) {
				addToStack(array[i]);
			}
		}
	}
	
	public TestValue(String valueId, T initialValue) throws ValueException {
		super(valueId);
		addToStackAndUpdate(initialValue);
	}
	
	public void addToStack(T value) {
		stack.add(value);
	}
	
	public void addToStackAndUpdate(T value) throws ValueException {
		addToStack(value);
		update();
	}

	@Override
	public void update() throws ValueUpdateException {
		try {
			add(stack.removeFirst());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
