package ru.prolib.aquila.ipc.ltam;

import java.io.*;
import java.net.*;
import ru.prolib.aquila.ipc.*;

public class SocketAccept implements ISocketAccept {
	
	private final int port;
	private ServerSocket socket = null;
	private Socket accepted = null;
	
	public SocketAccept(int port) {
		super();
		this.port = port;
	}

	@Override
	public synchronized void reset() throws IpcException {
		accepted = null;
	}

	@Override
	public synchronized void waitFor() throws IpcException {
		prepareServerSocket();
		try {
			accepted = socket.accept();
		} catch ( SocketException e ) {
			if ( e.getMessage().equals("Socket closed") ) {
				socket = null;
				throw new IpcInterruptedException();
			}
			throw new IpcException(e.getMessage(), e);
		} catch ( IOException e ) {
			throw new IpcException(e.getMessage(), e);
		}
	}
	
	private void prepareServerSocket() throws IpcException {
		if ( socket != null ) return;
		try {
			socket = new ServerSocket(port);
			socket.setSoTimeout(5000);
		} catch ( IOException e ) {
			throw new IpcInterruptedException(e.getMessage(), e);
		}
	}

	@Override
	public Socket getLastAccepted() {
		Socket last = accepted;
		accepted = null;
		return last;
	}

	@Override
	public void close() {
		if ( accepted != null && ! accepted.isClosed() ) {
			try {
				accepted.close();
			} catch ( IOException e ) {
				// TODO: хз
				//e.printStackTrace();
			}
			accepted = null;
		}
		if ( socket != null && ! socket.isClosed() ) {
			try {
				socket.close();
			} catch ( IOException e ) {
				// TODO: хз
				//e.printStackTrace();
			}
			socket = null;
		}
		
	}
	
	@Override
	public Thread createInterruptableThread(ThreadGroup group,
			IStateMonitor monitor, int i) throws IpcException
	{
		prepareServerSocket();
		return new InterruptableThread(group, monitor, i, this, socket);
	}

	private static class InterruptableThread extends Thread {
		private final ServerSocket socket;
		
		public InterruptableThread(ThreadGroup group, IStateMonitor monitor,
				int index, IPrimitive obj, ServerSocket socket)
		{
			super(group, new Primitive.Interruptable(monitor, index, obj));
			this.socket = socket;
		}
		
		@Override
		public void interrupt() {
			try {
				if ( ! socket.isClosed() ) {
					socket.close();
				}
			} catch ( IOException e ) {
				// TODO: Хз че тут делать. Вроде так не оч красиво.
				//e.printStackTrace();
			}
			super.interrupt();
		}
		
	}
	
}