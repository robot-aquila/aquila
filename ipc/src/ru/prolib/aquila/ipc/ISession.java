package ru.prolib.aquila.ipc;

import java.nio.channels.Selector;

public interface ISession {
	
	public void close();
	
	public IEvent createEvent(String name) throws IpcException;
	
	public ISocketAccept createSocketAccept(int port) throws IpcException;
	
	public ISelector createSelector() throws IpcException;
	
	public ISelector wrapSelector(Selector selector) throws IpcException;
	
	public int waitForMultiple(IPrimitive[] list) throws IpcException;
	
}
