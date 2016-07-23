package ru.prolib.aquila.finam.tools.web;

public class UnexpectedResponse extends DataExportException {
	private static final long serialVersionUID = 1L;
	
	public UnexpectedResponse(String msg) {
		super(msg);
	}
	
	public UnexpectedResponse(Throwable t) {
		super(t);
	}
	
	public UnexpectedResponse() {
		super();
	}
	
	public UnexpectedResponse(String msg, Throwable t) {
		super(msg, t);
	}

}
