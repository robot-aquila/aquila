package ru.prolib.aquila.ipc;

public interface IPrimitive {
	
	public void waitFor() throws IpcException;
	
	public void reset() throws IpcException;
	
	public Thread createInterruptableThread(ThreadGroup group,
			IStateMonitor monitor, int i) throws IpcException;
	
	public void close();
	
}