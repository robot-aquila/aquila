package ru.prolib.aquila.ipc.ltam;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;

import ru.prolib.aquila.ipc.*;

public class Selector implements ISelector {
	private final java.nio.channels.Selector selector; 
	
	public Selector(java.nio.channels.Selector selector) {
		super();
		this.selector = selector;
	}
	
	public Selector() throws IpcException {
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch ( IOException e ) {
			throw new IpcException(e.getMessage(), e);
		}
	}

	@Override
	public java.nio.channels.Selector getSelector() {
		return selector;
	}

	@Override
	public void close() {
		try {
			selector.close();
		} catch ( IOException e ) {
			// TODO: ???
			//e.printStackTrace();
		}
	}

	@Override
	public void reset() throws IpcException {
		
	}

	@Override
	public void waitFor() throws IpcException {
		try {
			selector.select();
		} catch ( IOException e ) {
			throw new IpcException(e.getMessage(), e);
		}
	}

	@Override
	public Thread createInterruptableThread(ThreadGroup group,
											IStateMonitor monitor, int i)
		throws IpcException
	{
		return new InterruptableThread(group, monitor, i, this, selector);
	}
	
	private static class InterruptableThread extends Thread {
		private final java.nio.channels.Selector selector;
		
		public InterruptableThread(ThreadGroup group, IStateMonitor monitor,
				int index, IPrimitive obj, java.nio.channels.Selector selector)
		{
			super(group, new Primitive.Interruptable(monitor, index, obj));
			this.selector = selector;
		}
		
		@Override
		public void interrupt() {
			if ( selector.isOpen() ) {
				selector.wakeup();
			}
			super.interrupt();
		}
		
	}

}
