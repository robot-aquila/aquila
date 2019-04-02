package ru.prolib.aquila.ui;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;

/**
 * Just a wrapper to pass aquila events for processing by SWING UI components.
 */
public class SwingEvent implements EventListener {
	private final EventListener listener;
	
	public SwingEvent(EventListener listener) {
		this.listener = listener;
	}

	@Override
	public void onEvent(Event event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listener.onEvent(event);
			}
		});
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SwingEvent.class ) {
			return false;
		}
		SwingEvent o = (SwingEvent) other;
		return new EqualsBuilder()
				.append(o.listener, listener)
				.build();
	}

}
