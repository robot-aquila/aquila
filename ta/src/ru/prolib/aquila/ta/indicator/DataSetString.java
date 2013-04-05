package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;

/**
 * Источник значения типа {@link java.lang.String} из набора именованных данных.
 * 
 * 2012-04-07
 * $Id: DataSetString.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetString extends BaseIndicatorDSV<String> {

	/**
	 * Конструктор
	 * @param data набор данных
	 * @param name наименование элемента
	 */
	public DataSetString(DataSet data, String name) {
		super(data, name);
	}

	@Override
	public String calculate() throws ValueException {
		try {
			return data.getString(name);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}

}
