package ru.prolib.aquila.core.BusinessEntities.osc;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public interface OSCFactory<KeyType, EntityType extends ObservableStateContainer> {
	
	EntityType produce(OSCRepository<KeyType, EntityType> owner, KeyType key);

}
