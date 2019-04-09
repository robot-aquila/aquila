package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OnTimeoutAction implements SMInputAction {
	
	public interface Handler {
		SMExit onTimeout(Object data);
	}
	
	private final Handler handler;
	
	public OnTimeoutAction(Handler handler) {
		this.handler = handler;
	}

	@Override
	public SMExit input(Object data) {
		return handler.onTimeout(data);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OnTimeoutAction.class ) {
			return false;
		}
		OnTimeoutAction o = (OnTimeoutAction) other;
		return new EqualsBuilder()
				.append(o.handler, handler)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(93648107, 981)
				.append(handler)
				.build();
	}

}
