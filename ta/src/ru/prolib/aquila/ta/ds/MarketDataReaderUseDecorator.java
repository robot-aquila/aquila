package ru.prolib.aquila.ta.ds;

/**
 * Обертка источника данных о торгах.
 * Использует заданный декоратор для обертки итератора, полученного в результате
 * вызова реального источника данных. Такой подход используется, для образования
 * постоянной связи значения с источником данных, который в силу специфики
 * реализации после каждого вызова возвращает разные экземпляры {@link DataSet}.
 * В результате, объект-значение использует в качестве набора данных экземпляр
 * декоратора, а объект данного класса заменяет в декораторе актуальный итератор
 * и всегда возвращает декоратор. 
 */
public class MarketDataReaderUseDecorator implements MarketDataReader {
	private final MarketDataReader reader;
	private final DataSetIteratorDecorator decorator;
	
	public MarketDataReaderUseDecorator(MarketDataReader reader) {
		this(reader, new DataSetIteratorDecorator());
	}
	
	public MarketDataReaderUseDecorator(MarketDataReader reader,
										DataSetIteratorDecorator decorator)
	{
		this.reader = reader;
		this.decorator = decorator;
	}
	
	public MarketDataReader getReader() {
		return reader;
	}
	
	public DataSetIteratorDecorator getDecorator() {
		return decorator;
	}

	@Override
	public DataSetIterator prepare() throws MarketDataException {
		decorator.setDataSetIterator(reader.prepare());
		return decorator;
	}

	@Override
	public DataSetIterator update() throws MarketDataException {
		decorator.setDataSetIterator(reader.update());
		return decorator;
	}

}
