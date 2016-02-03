package ru.prolib.aquila.core.data;

import java.util.Map;

public interface EditableContainer extends Container {
	
	public void update(Map<Integer, Object> tokens);
	
	public void resetChanges();

}
