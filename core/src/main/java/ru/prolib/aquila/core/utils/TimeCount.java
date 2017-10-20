package ru.prolib.aquila.core.utils;

import java.io.PrintStream;

public class TimeCount {
	private final String prefix;
	private final long started;
	
	public TimeCount(String prefix) {
		super();
		this.prefix = prefix;
		started = System.currentTimeMillis();
	}
	
	public TimeCount() {
		this("[TimeCount] ");
	}
	
	public void print(PrintStream stream) {
		stream.println(prefix + (System.currentTimeMillis() - started));
	}

}
