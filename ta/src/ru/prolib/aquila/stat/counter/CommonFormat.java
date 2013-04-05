package ru.prolib.aquila.stat.counter;

@SuppressWarnings("rawtypes")
public class CommonFormat implements CounterFormat {

	@Override
	public String format(Counter counter) {
		return counter.getValue().toString();
	}

}
