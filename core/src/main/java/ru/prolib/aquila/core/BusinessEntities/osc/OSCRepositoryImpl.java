package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.concurrency.LID;

public class OSCRepositoryImpl<KeyType, EntityType extends ObservableStateContainer>
	implements OSCRepository<KeyType, EntityType>
{
	protected final Map<KeyType, EntityType> entityMap;
	protected final LID lid;
	protected final Lock lock;
	protected final OSCFactory<KeyType, EntityType> factory;
	protected final String repoID;
	protected final EventType onEntityUpdate, onEntityAvailable, onEntityClose;
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(String.format("%s.%s", repoID, suffix));
	}
	
	protected OSCRepositoryImpl(
			Map<KeyType, EntityType> entity_map,
			LID lid,
			Lock lock,
			OSCFactory<KeyType, EntityType> factory,
			String repoID
		)
	{
		this.entityMap = entity_map;
		this.lid = lid;
		this.lock = lock;
		this.factory = factory;
		this.repoID = repoID;
		this.onEntityUpdate = newEventType("ENTITY_UPDATE");
		this.onEntityAvailable = newEventType("ENTITY_AVAILABLE");
		this.onEntityClose = newEventType("ENTITY_CLOSE");
	}
	
	public OSCRepositoryImpl(OSCFactory<KeyType, EntityType> factory, String repoID) {
		this(new LinkedHashMap<>(), LID.createInstance(), new ReentrantLock(), factory, repoID);
	}
	
	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public boolean contains(KeyType key) {
		lock();
		try {
			return entityMap.containsKey(key);
		} finally {
			unlock();
		}
	}
	
	@Override
	public EntityType getOrThrow(KeyType key) throws IllegalArgumentException {
		lock();
		try {
			EntityType entity = entityMap.get(key);
			if ( entity == null ) {
				throw new IllegalArgumentException("Entity not exists: " + key);
			}
			return entity;
		} finally {
			unlock();
		}
	}
	
	@Override
	public EntityType getOrCreate(KeyType key) {
		lock();
		try {
			EntityType entity = entityMap.get(key);
			if ( entity == null ) {
				entity = factory.produce(this, key);
				entity.onUpdate().addAlternateType(onEntityUpdate);
				entity.onAvailable().addAlternateType(onEntityAvailable);
				entity.onClose().addAlternateType(onEntityClose);
				entityMap.put(key, entity);
			}
			return entity;	
		} finally {
			unlock();
		}
	}
	
	@Override
	public Collection<EntityType> getEntities() {
		lock();
		try {
			return new ArrayList<>(entityMap.values());
		} finally {
			unlock();
		}
	}
	
	@Override
	public boolean remove(KeyType key) {
		EntityType entity;
		lock();
		try {
			entity = entityMap.remove(key);
		} finally {
			unlock();
		}
		if ( entity == null ) {
			return false;
		} else {
			close(entity);
			return true;
		}
	}
	
	@Override
	public void close() {
		List<EntityType> to_remove;
		lock();
		try {
			to_remove = new ArrayList<>(entityMap.values());
			entityMap.clear();
		} finally {
			unlock();
		}
		for ( EntityType entity : to_remove ) {
			close(entity);
		}
	}

	@Override
	public EventType onEntityUpdate() {
		return onEntityUpdate;
	}

	@Override
	public EventType onEntityAvailable() {
		return onEntityAvailable;
	}

	@Override
	public EventType onEntityClose() {
		return onEntityClose;
	}
	
	private void close(EntityType entity) {
		entity.close();
		entity.onUpdate().removeAlternatesAndListeners();
		entity.onAvailable().removeAlternatesAndListeners();
		entity.onClose().removeAlternatesAndListeners();		
	}

}
