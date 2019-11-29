package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.Collection;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.concurrency.LID;

/**
 * Read-Only decorator of Observable State Container repository.
 * <p>
 * @param <KeyType> - key type
 * @param <EntityType> - entity type
 */
public class OSCRepositoryDecoratorRO<KeyType, EntityType extends ObservableStateContainer>
	implements OSCRepository<KeyType, EntityType>
{
	private final OSCRepository<KeyType, EntityType> repository;
	
	public OSCRepositoryDecoratorRO(OSCRepository<KeyType, EntityType> repository) {
		this.repository = repository;
	}

	@Override
	public LID getLID() {
		return repository.getLID();
	}

	@Override
	public void lock() {
		repository.lock();
	}

	@Override
	public void unlock() {
		repository.unlock();
	}

	@Override
	public boolean contains(KeyType key) {
		return repository.contains(key);
	}

	@Override
	public EntityType getOrThrow(KeyType key) throws IllegalArgumentException {
		return repository.getOrThrow(key);
	}

	@Override
	public EntityType getOrCreate(KeyType key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(KeyType key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<EntityType> getEntities() {
		return repository.getEntities();
	}

	@Override
	public EventType onEntityUpdate() {
		return repository.onEntityUpdate();
	}

	@Override
	public EventType onEntityAvailable() {
		return repository.onEntityAvailable();
	}

	@Override
	public EventType onEntityClose() {
		return repository.onEntityClose();
	}

}
