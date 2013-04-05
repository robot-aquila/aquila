package ru.prolib.aquila.ipc;

public class IpcException extends Exception {
	private static final long serialVersionUID = 3462001254115661777L;

	public IpcException() {
		super();
	}

	public IpcException(String msg) {
		super(msg);
	}
	
	public IpcException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
