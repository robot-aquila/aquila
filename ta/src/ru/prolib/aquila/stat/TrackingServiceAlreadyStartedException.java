package ru.prolib.aquila.stat;

/**
 * 2012-02-02
 * $Id: TrackingServiceAlreadyStartedException.java 196 2012-02-02 20:24:38Z whirlwind $
 */
public class TrackingServiceAlreadyStartedException extends TrackingException {
	private static final long serialVersionUID = 1L;

	public TrackingServiceAlreadyStartedException(String msg) {
		super(msg);
	}
	
	public TrackingServiceAlreadyStartedException() {
		super();
	}

}
