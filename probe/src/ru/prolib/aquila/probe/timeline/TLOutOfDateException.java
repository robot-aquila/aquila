package ru.prolib.aquila.probe.timeline;

public class TLOutOfDateException extends TLException {
	private static final long serialVersionUID = 6699935138921733276L;
	
	public TLOutOfDateException() {
		super();
	}
	
	public TLOutOfDateException(String msg) {
		super(msg);
	}

}
