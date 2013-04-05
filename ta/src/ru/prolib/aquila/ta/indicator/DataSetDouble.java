package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;

/**
 * Источник значения типа {@link java.lang.Double} из набора именованных данных.
 * 
 * 2012-04-07
 * $Id: DataSetDouble.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetDouble extends BaseIndicatorDSV<Double> {

	/**
	 * Конструктор
	 * @param data набор данных
	 * @param name наименование элемента
	 */
	public DataSetDouble(DataSet data, String name) {
		super(data, name);
	}

	@Override
	public Double calculate() throws ValueException {
		try {
			return data.getDouble(name);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}

}
