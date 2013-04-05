package ru.prolib.aquila.util;

/**
 *	Объект, позволяющий перебирать все возможные комбинации значений.
 *	Каждый экземпляр данного класса представляет собой набор вариантов
 *	отдельной интересующей единицы данных. Наборы связываются между собой,
 *	посредством ссылок на соседние наборы в результате чего появляется
 *	возможность обойти граф вариантов.
 *
 *	Пример
 * <code>
 * 
 *	Variant<Long> id = new Variant<Long>(new Long[] {1L, 2L});
 *	Variant<Integer> qty = new Variant<Integer>(new Integer[]{100, 200}, id);
 *	do {
 *		System.out.println("id=" + id.get() + " qty=" + qty.get());
 *	} while ( qty.next() );
 *
 *</code>
 *
 *	Результат:
 *
 *	id=1 qty=100
 *	id=2 qty=100
 *	id=1 qty=200
 *	id=2 qty=200
 *
 * @param <T>
 */
public class Variant<T> {
	final Variant<?> child;
	final T[] values;
	int index = 0;
	
	public Variant(T[] values) {
		super();
		this.values = values;
		child = null;
	}
	
	public Variant(T[] values, Variant<?> childNode) {
		super();
		this.values = values;
		child = childNode;
	}
	
	public boolean next() {
		if ( child != null ) {
			if ( child.next() ) return true;
			child.rewind();
		}
		index ++;
		return index < values.length;
	}
	
	public void rewind() {
		index = 0;
		if ( child != null ) child.rewind();
	}
	
	public T get() {
		return values[index];
	}
	
}