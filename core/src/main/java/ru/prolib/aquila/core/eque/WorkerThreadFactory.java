package ru.prolib.aquila.core.eque;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {
	private int threadLastIndex = 0;
	private final String namePrefix;
	
	public WorkerThreadFactory(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	@Override
	public synchronized Thread newThread(Runnable r) {
		threadLastIndex ++;
		Thread t = new Thread(r, namePrefix + ".WORKER-" + threadLastIndex);
		t.setDaemon(true);
		return t;
	}
	
}