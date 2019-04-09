package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OnFinishAction implements SMInputAction {

	public interface Handler {
		SMExit onFinish(Object data);
	}
	
	private final Handler handler;
	
	public OnFinishAction(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public SMExit input(Object data) {
		return handler.onFinish(data);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OnFinishAction.class ) {
			return false;
		}
		OnFinishAction o = (OnFinishAction) other;
		return new EqualsBuilder()
				.append(o.handler, handler)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7761527, 1785)
				.append(handler)
				.build();
	}
	
}
