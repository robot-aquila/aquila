package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.Collection;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.concurrency.Lockable;

public interface OSCRepository<KeyType, EntityType extends ObservableStateContainer> extends Lockable {
	
	/**
	 * Test that entity exists.
	 * <p>
	 * @param key - entity key
	 * @return true if exists, false - otherwise
	 */
	boolean contains(KeyType key);
	
	/**
	 * Get entity or throw exception if entity not exists.
	 * <p>
	 * @param key - entity key
	 * @return entity instance
	 * @throws IllegalArgumentException - entity not exists
	 */
	EntityType getOrThrow(KeyType key) throws IllegalArgumentException;
	
	/**
	 * Get entity or create new one if entity not exists.
	 * <p>
	 * @param key - entity key
	 * @return entity instance
	 */
	EntityType getOrCreate(KeyType key);
	
	/**
	 * Remove entity.
	 * <p>
	 * @param key - entity key
	 * @return true if entity was removed, false - otherwise
	 */
	boolean remove(KeyType key);
	
	/**
	 * Close repository and remove all entities.
	 */
	void close();
	
	/**
	 * Get collection of entities.
	 * <p>
	 * @return entities
	 */
	Collection<EntityType> getEntities();
	
	EventType onEntityUpdate();
	EventType onEntityAvailable();
	EventType onEntityClose();

}
