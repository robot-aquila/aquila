package ru.prolib.aquila.core.utils;

import java.util.LinkedList;
import java.util.List;

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
 * Variant<Long> id = new Variant<Long>(new Long[] {1L, 2L});
 * Variant<Integer> qty = new Variant<Integer>(id).add(100).add(200);
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
 * @param <T>
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
		this.values = new LinkedList<T>();
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
	
}