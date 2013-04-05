package ru.prolib.aquila.ta.ds;

public class MarketDataReaderFake implements MarketDataReader {
	private final int count;
	
	/**
	 * Конструктор
	 * @param count количество эмулируемых элементов при каждом запросе
	 */
	public MarketDataReaderFake(int count) {
		super();
		this.count = count;
	}

	@Override
	public DataSetIterator prepare() throws MarketDataException {
		return new DataSetIteratorLimit(new DataSetIteratorFake(), count);
	}

	@Override
	public DataSetIterator update() throws MarketDataException {
		return new DataSetIteratorLimit(new DataSetIteratorFake(), count);
	}

}
