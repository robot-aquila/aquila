package ru.prolib.aquila.core.eque;

import java.util.List;

public class DispatchingMethodRightHere implements DispatchingMethod {

	@Override
	public void dispatch(List<DeliveryEventTask> tasks) {
		for ( DeliveryEventTask t : tasks ) {
			t.call();
		}
	}
	
	@Override
	public void shutdown() {
		
	}
	
}