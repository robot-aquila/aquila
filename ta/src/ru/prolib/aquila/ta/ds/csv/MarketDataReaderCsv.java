package ru.prolib.aquila.ta.ds.csv;

import java.io.FileNotFoundException;

import ru.prolib.aquila.ta.ds.DataSetIterator;
import ru.prolib.aquila.ta.ds.DataSetIteratorEmpty;
import ru.prolib.aquila.ta.ds.DataSetIteratorLimit;
import ru.prolib.aquila.ta.ds.MarketDataException;
import ru.prolib.aquila.ta.ds.MarketDataReader;

import com.csvreader.CsvReader;

public class MarketDataReaderCsv implements MarketDataReader {
	private final CsvReader reader;
	private DataSetIteratorCsv iterator;
	
	public MarketDataReaderCsv(String file) throws FileNotFoundException {
		this(new CsvReader(file));
	}
	
	public MarketDataReaderCsv(CsvReader reader) {
		super();
		this.reader = reader;
	}
	
	public CsvReader getCsvReader() {
		return reader;
	}

	@Override
	public DataSetIterator prepare() throws MarketDataException {
		try {
			reader.readHeaders();
			iterator = new DataSetIteratorCsv(reader);
		} catch ( Exception e ) {
			throw new MarketDataException(e.getMessage(), e);
		}
		return new DataSetIteratorEmpty();
	}

	@Override
	public DataSetIterator update() throws MarketDataException {
		try {
			return new DataSetIteratorLimit(iterator, 1);
		} catch ( Exception e ) {
			throw new MarketDataException(e.getMessage(), e);
		}
	}

}
