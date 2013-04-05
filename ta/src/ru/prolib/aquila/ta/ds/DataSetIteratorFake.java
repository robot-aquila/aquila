package ru.prolib.aquila.ta.ds;

import java.util.Date;

/**
 * Заглушка, всегда возвращающая true на запрос next. Все прочие запросы, кроме
 * {@link #close}, приведут к выбросу исключения. Предназначен для совместного
 * использования с {@link DataSetIteratorLimit}, с целью эмуляции цикла
 * обновления информации о торгах.  
 */
public class DataSetIteratorFake implements DataSetIterator {
	
	public DataSetIteratorFake() {
		super();
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		throw new DataSetException("Restricted");
	}

	@Override
	public String getString(String name) throws DataSetException {
		throw new DataSetException("Restricted");
	}

	@Override
	public Date getDate(String name) throws DataSetException {
		throw new DataSetException("Restricted");
	}

	@Override
	public Long getLong(String name) throws DataSetException {
		throw new DataSetException("Restricted");
	}

	@Override
	public boolean next() throws DataSetException {
		return true;
	}

	@Override
	public void close() {

	}

}
