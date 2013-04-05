package ru.prolib.aquila.ta.ds.quik;

/**
 * Исключение, возбуждаемое в случае ошибок ввода-вывода.
 */
public class Tr2QuikIoException extends Tr2QuikException {
	private static final long serialVersionUID = -3376005613080573633L;

	public Tr2QuikIoException(Throwable e) {
		super(e);
	}
	
	public Tr2QuikIoException(String msg, Throwable t) {
		super(msg, t);
	}
	
}