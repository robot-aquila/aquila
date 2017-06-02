package ru.prolib.aquila.core.BusinessEntities.osc;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCControllerStub implements OSCController {

	@Override
	public boolean hasMinimalData(ObservableStateContainer container) {
		return true;
	}

	@Override
	public void processUpdate(ObservableStateContainer container) {
		
	}

	@Override
	public void processAvailable(ObservableStateContainer container) {
		
	}
	
}