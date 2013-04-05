package ru.prolib.aquila.ipc;

public interface IEvent extends IPrimitive {
	
	public void pulse() throws IpcException;
	
	public void signal() throws IpcException;
	
	public boolean isSignaled() throws IpcException;
	
}
