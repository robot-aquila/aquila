package ru.prolib.aquila.ipc.ltam;

import ru.prolib.aquila.ipc.*;

abstract public class Primitive implements IPrimitive {
	
	public Primitive() {
		super();
	}
	
	public Thread createInterruptableThread(ThreadGroup group,
			IStateMonitor monitor, int i) throws IpcException
	{
		return new Thread(group, new Interruptable(monitor, i, this));
	}
	
	@Override
	public synchronized void waitFor() throws IpcException {
		try {
			wait();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new IpcInterruptedException(e.getMessage(), e);
		}
	}
	
	protected static class Interruptable implements Runnable {
		private final IStateMonitor monitor;
		private final int index;
		private final IPrimitive object;
		
		public Interruptable(IStateMonitor monitor, int index, IPrimitive obj) {
			this.monitor = monitor;
			this.index = index;
			this.object = obj;
		}

		@Override
		public void run() {
			try {
				object.waitFor();
			} catch ( IpcInterruptedException e ) {
				return;
			} catch ( IpcException e ) {
				e.printStackTrace();
			}
			monitor.signalIndex(index);
		}
		
	}
	
}
