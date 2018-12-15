package ru.prolib.aquila.core.data;

public interface KSeries<TKey, TValue> extends Series<TValue> {
	
	TValue get(TKey key);
	
	int toIndex(TKey key);
	
	TKey toKey(int index) throws ValueException;

}
