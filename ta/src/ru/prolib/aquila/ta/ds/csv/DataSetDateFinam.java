package ru.prolib.aquila.ta.ds.csv;

import java.util.Date;

import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;
import ru.prolib.aquila.ta.ds.*;
import ru.prolib.aquila.ta.indicator.DataSetFinamCsvDate;

/**
 * Тип значений дата.   
 * Предназначен для извлечения дат из CSV-файлов котировок, полученных с сайта
 * ФИНАМа. Абсолютное значение времени котировки рассчитывается исходя из
 * комбинации двух полей: даты в формате yyyyMMdd и времени в формате HHmmss.  
 */
@Deprecated
public class DataSetDateFinam extends ValueImpl<Date> {
	private final DataSetFinamCsvDate value;

	public DataSetDateFinam(String valueId, DataSet dataSet,
							String dateName, String timeName)
	{
		super(valueId);
		value = new DataSetFinamCsvDate(dataSet, dateName, timeName);
	}
	
	public DataSet getDataSet() {
		return value.getDataSet();
	}
	
	public String getDateName() {
		return value.getDateName();
	}
	
	public String getTimeName() {
		return value.getTimeName();
	}

	@Override
	public void update() throws ValueUpdateException {
		try {
			add(value.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
