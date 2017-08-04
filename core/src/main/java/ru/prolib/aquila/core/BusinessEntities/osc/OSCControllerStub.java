package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Clock;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCControllerStub implements OSCController {
	private final Clock clock;
	
	public OSCControllerStub(Clock clock) {
		this.clock = clock;
	}
	
	public OSCControllerStub() {
		this(Clock.systemUTC());
	}

	@Override
	public Instant getCurrentTime(ObservableStateContainer container) {
		return clock.instant();
	}

	@Override
	public boolean hasMinimalData(ObservableStateContainer container, Instant time) {
		return true;
	}

	@Override
	public void processUpdate(ObservableStateContainer container, Instant time) {
		
	}

	@Override
	public void processAvailable(ObservableStateContainer container, Instant time) {
		
	}
	
}