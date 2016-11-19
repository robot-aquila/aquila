package ru.prolib.aquila.core.utils;

import java.util.*;

/**
 * Объект, позволяющий перебирать все возможные комбинации значений.
 * Каждый экземпляр данного класса представляет собой набор вариантов
 * отдельной интересующей единицы данных. Наборы связываются между собой,
 * посредством ссылок на соседние наборы в результате чего появляется
 * возможность обойти граф вариантов.
 * <p>
 * Пример
 * <pre>
 * 
 * Variant&lt;Long&gt; id = new Variant&lt;Long&gt;(new Long[] {1L, 2L});
 * Variant&lt;Integer&gt; qty = new Variant&lt;Integer&gt;(id).add(100).add(200);
 * do {
 *    System.out.println("id=" + id.get() + " qty=" + qty.get());
 * } while ( qty.next() );
 *
 *</pre>
 *
 * Результат:
 * <pre>
 * id=1 qty=100
 * id=2 qty=100
 * id=1 qty=200
 * id=2 qty=200
 * </pre>
 *
 * @param <T> - Type of data.
 */
public class Variant<T> {
	final Variant<?> child;
	final List<T> values;
	int index = 0;
	
	public Variant(T[] values) {
		this(values, null);
	}
	
	public Variant(T[] values, Variant<?> childNode) {
		super();
		child = childNode;
		this.values = new Vector<T>();
		if ( values != null ) {
			for ( int i = 0; i < values.length; i ++ ) {
				this.values.add(values[i]);
			}
		}
	}
	
	public Variant() {
		this((T[]) null);
	}
	
	public Variant(Variant<?> childNode) {
		this(null, childNode);
	}
	
	public Variant(T v1) {
		this((T[]) null);
		add(v1);
	}
	
	public Variant(T v1, T v2) {
		this(v1);
		add(v2);
	}
	
	public Variant(T v1, T v2, T v3) {
		this(v1, v2);
		add(v3);
	}
	
	public Variant(Variant<?> childNode, T v1) {
		this(childNode);
		add(v1);
	}
	
	public Variant(Variant<?> childNode, T v1, T v2) {
		this(childNode, v1);
		add(v2);
	}

	public Variant(Variant<?> childNode, T v1, T v2, T v3) {
		this(childNode, v1, v2);
		add(v3);
	}

	public Variant<T> add(T variantValue) {
		values.add(variantValue);
		return this;
	}
	
	public boolean next() {
		if ( child != null ) {
			if ( child.next() ) return true;
			child.rewind();
		}
		index ++;
		return index < values.size();
	}
	
	public void rewind() {
		index = 0;
		if ( child != null ) child.rewind();
	}
	
	public T get() {
		return values.get(index);
	}
	
	public int count() {
		if ( child == null ) {
			return values.size();
		} else {
			return values.size() * child.count();
		}
	}
	
	public Variant<T> clear() {
		values.clear();
		return this;
	}
	
}