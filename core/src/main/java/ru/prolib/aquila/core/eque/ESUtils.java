package ru.prolib.aquila.core.eque;

import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.EventType;

public class ESUtils {

	public static Set<EventType> getAllUniqueTypes(Set<EventType> allTypes, EventType startType) {
		allTypes.add(startType);
		for ( EventType alternate : startType.getAlternateTypes() ) {
			if ( ! allTypes.contains(alternate) ) {
				getAllUniqueTypes(allTypes, alternate);
			}
		}
		return allTypes;
	}
	
	public static Set<EventType> getAllUniqueTypes(EventType startType) {
		return getAllUniqueTypes(new HashSet<>(), startType);
	}

}
