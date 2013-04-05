package ru.prolib.aquila.ta.indicator;

import java.util.Date;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;

/**
 * Источник значения типа {@link java.util.Date} из набора именованных данных.
 *
 * 2012-04-07
 * $Id: DataSetDate.java 206 2012-04-07 14:06:09Z whirlwind $
 */
public class DataSetDate extends BaseIndicatorDSV<Date> {

	/**
	 * Конструктор
	 * @param data набор данных
	 * @param name наименование элемента
	 */
	public DataSetDate(DataSet data, String name) {
		super(data, name);
	}

	@Override
	public Date calculate() throws ValueException {
		try {
			return data.getDate(name);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}

}
