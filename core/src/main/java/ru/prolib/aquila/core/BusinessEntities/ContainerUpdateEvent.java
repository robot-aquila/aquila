package ru.prolib.aquila.core.BusinessEntities;

import java.util.Map;
import java.util.Set;

public interface ContainerUpdateEvent extends ContainerEvent {

	public boolean hasChanged(int token);
	public Set<Integer> getUpdatedTokens();
	public Map<Integer, Object> getOldValues();
	public Map<Integer, Object> getNewValues();

}
