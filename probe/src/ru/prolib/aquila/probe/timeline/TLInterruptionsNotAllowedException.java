package ru.prolib.aquila.probe.timeline;

/**
 * Исключение, выбрасываемое в случае отлова прерывания потока. 
 */
public class TLInterruptionsNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 821504859348547918L;
	
	public TLInterruptionsNotAllowedException(InterruptedException t) {
		super(t);
	}

}
