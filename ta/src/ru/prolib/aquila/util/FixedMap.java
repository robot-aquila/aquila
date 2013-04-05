package ru.prolib.aquila.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Карта ограниченного размера.
 * По достижении определенного размера удаляет пары, добавленные ранее.
 * Во всем остальном повторяет поведение класса {@link java.util.LinkedHashMap}.
 * <p>
 * @param <K> тип ключа
 * @param <V> тип значения
 * <p>
 * 2012-03-01<br>
 * $Id: FixedMap.java 216 2012-05-14 16:13:13Z whirlwind $
 */
public class FixedMap<K,V> extends LinkedHashMap<K,V> {
	private static final long serialVersionUID = 1L;
	private final int max;
	
	public FixedMap(int maximalCapacity) {
		super();
		max = maximalCapacity;
	}
	
	public FixedMap(int initialCapacity, int maximalCapacity) {
		super(initialCapacity);
		max = maximalCapacity;
	}
	
	public FixedMap(int initialCapacity, float loadFactor,
			int maximalCapacity)
	{
		super(initialCapacity, loadFactor);
		max = maximalCapacity;
	}
	
	public FixedMap(int initialCapacity, float loadFactor,
					boolean accessOrder, int maximalCapacity)
	{
		super(initialCapacity, loadFactor, accessOrder);
		max = maximalCapacity;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FixedMap(Map m, int maximalCapacity) {
		super(m);
		max = maximalCapacity;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected boolean removeEldestEntry(Map.Entry eldest) {
		return size() > max;
	}

}
