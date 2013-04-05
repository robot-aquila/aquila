package ru.prolib.aquila.ta.indicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;

/**
 * Источник значения типа {@link java.util.Date}.   
 * Предназначен для извлечения времени из CSV-файлов котировок, полученных с
 * сайта ФИНАМа. Абсолютное значение времени котировки рассчитывается исходя из
 * комбинации двух полей: даты в формате yyyyMMdd и времени в формате HHmmss.  
 */
public class DataSetFinamCsvDate extends BaseIndicatorDSV<Date> {
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	protected final String timeName;

	/**
	 * Консоруктор
	 * @param data набор данных
	 * @param dateName наименование элемента со строкой даты
	 * @param timeName наименование элемента со строкой времени
	 */
	public DataSetFinamCsvDate(DataSet data, String dateName, String timeName) {
		super(data, dateName);
		if ( timeName == null ) {
			throw new NullPointerException("Time element name cannpt be null");
		}
		this.timeName = timeName;
	}
	
	/**
	 * Получить наименование элемента со строкой даты
	 * @return
	 */
	public String getDateName() {
		return getName();
	}
	
	/**
	 * Получить наименование элемента со строкой времени
	 * @return
	 */
	public String getTimeName() {
		return timeName;
	}

	@Override
	public Date calculate() throws ValueException {
		try {
			return df.parse(data.getString(name) + data.getString(timeName));
		} catch ( ParseException e ) {
			throw new ValueException(e.getMessage(), e);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}

}
