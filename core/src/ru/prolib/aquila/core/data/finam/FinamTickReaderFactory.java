package ru.prolib.aquila.core.data.finam;

import java.io.IOException;

import ru.prolib.aquila.core.data.*;

/**
 * Фабрика потока тиков на базе файлов FINAM.
 */
public class FinamTickReaderFactory implements TickReaderFactory {
	private final Finam facade;
	
	public FinamTickReaderFactory(Finam facade) {
		super();
		this.facade = facade;
	}
	
	public FinamTickReaderFactory() {
		this(new Finam());
	}

	@Override
	public Aqiterator<Tick> createTickReader(String param)
			throws DataException
	{
		try {
			return facade.createTickReader(param);
		} catch ( IOException e ) {
			throw new DataException(e);
		}
	}

}
