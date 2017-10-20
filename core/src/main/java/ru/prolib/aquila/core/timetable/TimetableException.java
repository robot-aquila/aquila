package ru.prolib.aquila.core.timetable;

public class TimetableException extends Exception {
	private static final long serialVersionUID = 58073033692207105L;
	
	public TimetableException() {
		super();
	}
	
	public TimetableException(String msg) {
		super(msg);
	}
	
	public TimetableException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TimetableException(Throwable t) {
		super(t);
	}

}
