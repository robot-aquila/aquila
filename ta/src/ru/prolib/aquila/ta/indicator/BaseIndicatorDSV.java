package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.DataSet;

/**
 * Заготовка типового индикатора.
 * Используется для индикаторов с объектом типа
 * {@link ru.prolib.aquila.ta.ds.DataSet} в качестве источника значений.
 *
 * @param <T> тип возвращаемого значения
 */
abstract public class BaseIndicatorDSV<T> implements ValueSource<T> {
	protected final DataSet data;
	protected final String name;
	
	/**
	 * Конструктор
	 * @param data набор данных
	 * @param name наименование элемента в наборе
	 */
	public BaseIndicatorDSV(DataSet data, String name) {
		super();
		if ( data == null ) {
			throw new NullPointerException("Data cannot be null");
		}
		if ( name == null ) {
			throw new NullPointerException("Name cannot be null");
		}
		this.data = data;
		this.name = name;
	}
	
	/**
	 * Получить набор данных
	 * @return
	 */
	public DataSet getDataSet() {
		return data;
	}
	
	/**
	 * Получить наименование элемента
	 * @return
	 */
	public String getName() {
		return name;
	}

}
