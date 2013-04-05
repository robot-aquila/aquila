package ru.prolib.aquila.ipc.ltam;

import java.util.*;
import ru.prolib.aquila.ipc.*;

public class Session implements ISession {
	private final ThreadGroup threads;
	private final Hashtable<String, IPrimitive> namedPrimitives;
	private final Set<IPrimitive> allPrimitives;
	
	public Session() {
		super();
		threads = new ThreadGroup("IpcSession");
		namedPrimitives = new Hashtable<String, IPrimitive>();
		allPrimitives = new HashSet<IPrimitive>();
	}

	@Override
	public synchronized void close() {
		threads.interrupt();
		threads.destroy();
		Iterator<IPrimitive> i = allPrimitives.iterator();
		while ( i.hasNext() ) {
			i.next().close();
		}
		allPrimitives.clear();
		namedPrimitives.clear();
	}
	
	private synchronized IPrimitive addPrimitive(IPrimitive obj)
		throws IpcException
	{
		allPrimitives.add(obj);
		return obj;
	}
	
	private synchronized
		IPrimitive addNamedPrimitive(String name, IPrimitive obj)
			throws IpcException
	{
		addPrimitive(obj);
		namedPrimitives.put(name, obj);
		return obj;
	}

	@Override
	public IEvent createEvent(String name) throws IpcException {
		IEvent event = (IEvent) namedPrimitives.get(name);
		if ( event == null ) {
			event = (IEvent) addNamedPrimitive(name, new Event());
		}
		return event;
	}
	
	@Override
	public ISocketAccept createSocketAccept(int port) throws IpcException {
		return (ISocketAccept) addPrimitive(new SocketAccept(port));
	}
	
	@Override
	public ISelector createSelector() throws IpcException {
		return (ISelector) addPrimitive(new Selector());
	}

	@Override
	public ISelector wrapSelector(java.nio.channels.Selector selector)
			throws IpcException
	{
		return (ISelector) addPrimitive(new Selector(selector));
	}

	@Override
	public int waitForMultiple(IPrimitive[] list) throws IpcException {
		ThreadGroup tg = new ThreadGroup(threads, "wfm");
		Thread[] wt = new Thread[list.length];
		StateMonitor monitor = new StateMonitor();
		for ( int i = 0; i < list.length; i ++ ) {
			wt[i] = list[i].createInterruptableThread(tg, monitor, i);
			wt[i].start();
		}
		int primitiveIndex = -1;
		try {
			primitiveIndex = monitor.waitIndex();
		} finally {
			tg.interrupt();
			for ( int i = 0; i < wt.length; i ++ ) {
				try {
					wt[i].join();
				} catch ( InterruptedException e ) {
					throw new IpcException("Interrupted", e);
				}
			}
			tg.destroy();
		}
		return primitiveIndex;
	}

}
