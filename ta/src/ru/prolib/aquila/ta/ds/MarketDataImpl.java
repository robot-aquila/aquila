package ru.prolib.aquila.ta.ds;


import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueList;
import ru.prolib.aquila.ta.ValueListImpl;

/**
 * Источник данных о торгах.
 * Работает на основе считывающего устройства, возвращающего наборы данных.
 * Является наблюдаемым объектом. Уведомление наблюдателей выполняется
 * каждый раз после добавления нового "бара" (фактически после пересчета
 * всех текущих значений).
 */
public class MarketDataImpl extends MarketDataCommon implements MarketData {
	private final MarketDataReader reader;
	
	public MarketDataImpl(MarketDataReader reader) {
		this(reader, new ValueListImpl());
	}

	public MarketDataImpl(MarketDataReader reader, ValueList valueList) {
		super(valueList);
		this.reader = reader;
	}

	/**
	 * Получить используемый источник данных
	 * @return
	 */
	public MarketDataReader getMarketDataReader() {
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ValueList#update()
	 */
	@Override
	public synchronized void update() throws ValueException {
		try {
			DataSetIterator iterator = reader.update();
			while ( iterator.next() ) {
				values.update();
				setChanged();
				notifyObservers();
			}
			iterator.close();
		} catch ( MarketDataException e ) {
			throw new ValueException(e.getMessage(), e);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.MarketData#prepare()
	 */
	@Override
	public synchronized void prepare() throws ValueException {
		try {
			DataSetIterator iterator = reader.prepare();
			while ( iterator.next() ) {
				values.update();
				setChanged();
				notifyObservers();
			}
			iterator.close();
		} catch ( MarketDataException e ) {
			throw new ValueException(e.getMessage(), e);
		} catch ( DataSetException e ) {
			throw new ValueException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.MarketData#getLevel()
	 */
	@Override
	public int getLevel() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.MarketData#getSource()
	 */
	@Override
	public MarketData getSource() throws MarketDataException {
		throw new MarketDataException("Direct datasource");
	}

}
