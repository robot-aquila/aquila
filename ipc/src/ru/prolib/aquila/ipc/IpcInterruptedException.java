package ru.prolib.aquila.ipc;

public class IpcInterruptedException extends IpcException {
	private static final long serialVersionUID = -6185294121388226002L;
	
	public IpcInterruptedException() {
		super();
	}

	public IpcInterruptedException(String msg) {
		super(msg);
	}
	
	public IpcInterruptedException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
