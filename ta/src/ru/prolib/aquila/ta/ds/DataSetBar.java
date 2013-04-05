package ru.prolib.aquila.ta.ds;

import java.util.Date;

import ru.prolib.aquila.core.data.Candle;

/**
 * Объект доступа к данным на основе бара.
 * Предусматривает работу с фиксированным набором именованных данных:
 * {@link MarketData#TIME} - только для {@link DataSet#getDate(String)};
 * {@link MarketData#OPEN}, {@link MarketData#HIGH}, {@link MarketData#LOW},
 * {@link MarketData#CLOSE} и {@link MarketData#VOL} - только для
 * {@link DataSet#getDouble(String)}. Все прочие вызовы приведут к выбросу
 * исключения.  
 *
 */
public class DataSetBar implements DataSet {
	private Candle bar;
	
	public DataSetBar() {
		super();
	}
	
	public void setBar(Candle bar) {
		this.bar = bar;
	}
	
	public Candle getBar() {
		return bar;
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		if ( bar == null ) {
			throw new DataSetBarNotSpecifiedException();
		}
		
		if ( name.equals(MarketData.OPEN) ) {
			return bar.getOpen();
		} else if ( name.equals(MarketData.HIGH) ) {
			return bar.getHigh();
		} else if ( name.equals(MarketData.LOW) ) {
			return bar.getLow();
		} else if ( name.equals(MarketData.CLOSE) ) {
			return bar.getClose();
		} else if ( name.equals(MarketData.VOL) ) {
			return (double) bar.getVolume();
		}
		throw new DataSetValueNotExistsException(name);
	}

	@Override
	public String getString(String name) throws DataSetException {
		throw new DataSetValueNotExistsException(name);
	}

	@Override
	public Date getDate(String name) throws DataSetException {
		if ( ! name.equals(MarketData.TIME) ) {
			throw new DataSetValueNotExistsException(name);
		}
		if ( bar == null ) {
			throw new DataSetBarNotSpecifiedException();
		}
		return bar.getTime();
	}

	@Override
	public Long getLong(String name) throws DataSetException {
		throw new DataSetValueNotExistsException(name);
	}

}
