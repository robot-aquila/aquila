package ru.prolib.aquila.ipc;

public interface IStateMonitor {
	
	public void signalIndex(int index);
	
	public int waitIndex() throws IpcInterruptedException;
	
}