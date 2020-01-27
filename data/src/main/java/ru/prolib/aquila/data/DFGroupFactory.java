package ru.prolib.aquila.data;

public interface DFGroupFactory<KeyType, FeedType> {
	DFGroup<KeyType, FeedType> produce(KeyType key);
}
