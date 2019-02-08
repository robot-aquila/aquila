package ru.prolib.aquila.core.utils;

public class RunnableStub implements Runnable {
	private static final RunnableStub instance = new RunnableStub();
	
	public static Runnable getInstance() {
		return instance;
	}

	@Override
	public void run() {
		
	}

}
