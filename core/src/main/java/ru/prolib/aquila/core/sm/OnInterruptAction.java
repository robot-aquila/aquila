package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OnInterruptAction implements SMInputAction {
	
	public interface Handler {
		SMExit onInterrupt(Object data);
	}
	
	private final Handler handler;
	
	public OnInterruptAction(Handler handler) {
		this.handler = handler;
	}

	@Override
	public SMExit input(Object data) {
		return handler.onInterrupt(data);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(437809167, 61)
				.append(handler)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OnInterruptAction.class ) {
			return false;
		}
		OnInterruptAction o = (OnInterruptAction) other;
		return new EqualsBuilder()
				.append(o.handler, handler)
				.build();
	}

}
