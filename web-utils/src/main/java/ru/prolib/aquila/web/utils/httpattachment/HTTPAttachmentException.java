package ru.prolib.aquila.web.utils.httpattachment;

public class HTTPAttachmentException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public HTTPAttachmentException() {
		
	}
	
	public HTTPAttachmentException(String msg) {
		super(msg);
	}
	
	public HTTPAttachmentException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public HTTPAttachmentException(Throwable t) {
		super(t);
	}

}
