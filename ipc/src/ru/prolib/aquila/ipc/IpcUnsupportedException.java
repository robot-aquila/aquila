package ru.prolib.aquila.ipc;

public class IpcUnsupportedException extends IpcException {
	private static final long serialVersionUID = 8211845014295901135L;

	public IpcUnsupportedException() {
		super();
	}

	public IpcUnsupportedException(String msg) {
		super(msg);
	}
	
	public IpcUnsupportedException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
