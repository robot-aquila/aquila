package ru.prolib.aquila.core.BusinessEntities;

/**
 * Базовое исключение редактируемых объектов.
 */
public class EditableObjectException extends Exception {
	private static final long serialVersionUID = -5415852538204882248L;
	
	public EditableObjectException() {
		super();
	}
	
	public EditableObjectException(String msg) {
		super(msg);
	}
	
	public EditableObjectException(Throwable t) {
		super(t);
	}
	
	public EditableObjectException(String msg, Throwable t) {
		super(msg, t);
	}

}
