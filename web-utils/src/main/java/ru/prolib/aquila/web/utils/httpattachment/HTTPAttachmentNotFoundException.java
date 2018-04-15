package ru.prolib.aquila.web.utils.httpattachment;

public class HTTPAttachmentNotFoundException extends HTTPAttachmentException {
	private static final long serialVersionUID = 1L;
	private final HTTPAttachmentCriteria criteria;
	
	public HTTPAttachmentNotFoundException(HTTPAttachmentCriteria criteria) {
		this.criteria = criteria;
	}
	
	public HTTPAttachmentNotFoundException(HTTPAttachmentCriteria criteria, String msg) {
		super(msg);
		this.criteria = criteria;
	}
	
	public HTTPAttachmentNotFoundException(HTTPAttachmentCriteria criteria, String msg, Throwable t) {
		super(msg, t);
		this.criteria = criteria;
	}
	
	public HTTPAttachmentNotFoundException(HTTPAttachmentCriteria criteria, Throwable t) {
		super(t);
		this.criteria = criteria;
	}
	
	public HTTPAttachmentCriteria getCriteria() {
		return criteria;
	}

}
