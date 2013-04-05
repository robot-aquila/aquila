package ru.prolib.aquila.ipc.ltam;

import ru.prolib.aquila.ipc.*;

class StateMonitor implements IStateMonitor {
	private int index = -1;
	
	public StateMonitor() {
		super();
	}
	
	public synchronized void signalIndex(int index) {
		this.index = index;
		notifyAll();
	}
	
	public synchronized int waitIndex() throws IpcInterruptedException {
		while ( index == -1 ) {
			try {
				wait();
			} catch ( InterruptedException e ) {
				Thread.currentThread().interrupt();
				throw new IpcInterruptedException(e.getMessage(), e);
			}
		}
		return index;
	}
	
}
