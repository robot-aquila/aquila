package ru.prolib.aquila.ipc.ltam;

import ru.prolib.aquila.ipc.*;

public class Event extends Primitive implements IEvent {
	private boolean signaled;
	
	public Event() {
		super();
	}

	@Override
	public synchronized void pulse() throws IpcException {
		signaled = false;
		notifyAll();
	}

	@Override
	public synchronized boolean isSignaled() {
		return signaled;
	}

	@Override
	public synchronized void signal() throws IpcException {
		signaled = true;
		notifyAll();
	}

	@Override
	public synchronized void reset() throws IpcException {
		signaled = false;
	}

	@Override
	public void close() {
		
	}
	
}
