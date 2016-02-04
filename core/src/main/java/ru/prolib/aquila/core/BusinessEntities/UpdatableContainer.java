package ru.prolib.aquila.core.BusinessEntities;

import java.util.Map;

public interface UpdatableContainer extends Container {
	
	public void update(Map<Integer, Object> tokens);
	
	public void resetChanges();

}
