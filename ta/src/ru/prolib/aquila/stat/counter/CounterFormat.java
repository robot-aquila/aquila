package ru.prolib.aquila.stat.counter;

public interface CounterFormat<T> {
	
	public String format(Counter<T> counter);

}
