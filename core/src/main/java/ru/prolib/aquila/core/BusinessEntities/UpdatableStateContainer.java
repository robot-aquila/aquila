package ru.prolib.aquila.core.BusinessEntities;

import java.util.Map;

public interface UpdatableStateContainer extends StateContainer {
	
	public void update(Map<Integer, Object> tokens);
	
	public void resetChanges();

}
