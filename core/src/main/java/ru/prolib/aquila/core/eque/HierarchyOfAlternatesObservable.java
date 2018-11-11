package ru.prolib.aquila.core.eque;

import java.util.Set;

import ru.prolib.aquila.core.EventType;

public interface HierarchyOfAlternatesObservable {
	Set<EventType> getFullListOfRelatedTypes();
	void addListener(HierarchyOfAlternatesListener listener);
	void removeListener(HierarchyOfAlternatesListener listener);
}
