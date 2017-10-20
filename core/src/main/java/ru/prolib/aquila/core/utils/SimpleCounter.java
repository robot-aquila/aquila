package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Простой счетчик.
 * <p>
 * 2012-11-16<br>
 * $Id: SimpleCounter.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class SimpleCounter implements Counter {
	private int counter;
	
	/**
	 * Создать счетчик.
	 */
	public SimpleCounter() {
		this(0);
	}
	
	/**
	 * Создать счетчик.
	 * <p>
	 * @param initial начальное значение
	 */
	public SimpleCounter(int initial) {
		super();
		counter = initial;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ib.Counter#getValue()
	 */
	@Override
	public synchronized int get() {
		return counter;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ib.Counter#increment()
	 */
	@Override
	public synchronized void increment() {
		counter ++;
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121117, 130807)
			.append(counter)
			.toHashCode();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other instanceof SimpleCounter ) {
			SimpleCounter o = (SimpleCounter) other;
			return new EqualsBuilder()
				.append(counter, o.counter)
				.isEquals();
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#set(int)
	 */
	@Override
	public synchronized void set(int value) {
		counter = value;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#getAndIncrement()
	 */
	@Override
	public synchronized int getAndIncrement() {
		return counter ++;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#incrementAndGet()
	 */
	@Override
	public synchronized int incrementAndGet() {
		return ++ counter;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#getAndDecrement()
	 */
	@Override
	public synchronized int getAndDecrement() {
		return counter --;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#decrementAndGet()
	 */
	@Override
	public synchronized int decrementAndGet() {
		return -- counter;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.core.utils.Counter#decrement()
	 */
	@Override
	public synchronized void decrement() {
		counter --;
	}

}
