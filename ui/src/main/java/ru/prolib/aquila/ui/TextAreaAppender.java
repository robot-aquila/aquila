package ru.prolib.aquila.ui;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Аппендер журнала в UI.
 * <p>
 * 2013-01-13<br>
 * $Id: TextAreaAppender.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class TextAreaAppender extends WriterAppender {
	
	public static interface Appender { public void append(String str); }
	
	private Appender target;
	
	public TextAreaAppender() {
		super();
	}

	@Override
	public void append(final LoggingEvent event) {
		if(target == null) {
			return;
		}
		target.append(layout.format(event));
		if(layout.ignoresThrowable() && event.getThrowableStrRep() != null) {
			for(String str : event.getThrowableStrRep()) {
				target.append(str+"\n");
			}
		}
	}
	
	public void setTarget(Appender output) {
		target = output;
	}
}
