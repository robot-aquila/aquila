package ru.prolib.aquila.web.utils.finam.datasim;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DatedSymbol;
import ru.prolib.aquila.data.TimeLimitedL1UpdateIterator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;

public class FinamSeamlessL1UpdateReader implements CloseableIterator<L1Update> {
	private final SymbolFileStorage storage;
	private final Symbol symbol;
	private final Instant startTime;
	private LocalDate lastDate;
	private CloseableIterator<L1Update> reader;
	private boolean closed = false;
	
	public FinamSeamlessL1UpdateReader(SymbolFileStorage storage, Symbol symbol, Instant startTime) {
		this.storage = storage;
		this.symbol = symbol;
		this.startTime = startTime;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			closed = true;
			closeReader();
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Already closed");
		}
		if ( reader != null ) {
			if ( reader.next() ) {
				return true;
			}
		}
		while ( openReader() ) {
			if ( reader.next() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public L1Update item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Already closed");
		}
		if ( reader == null ) {
			throw new NoSuchElementException();
		}
		return reader.item();
	}
	
	private void closeReader() {
		IOUtils.closeQuietly(reader);
		reader = null;
	}
	
	private boolean openReader() throws IOException {
		closeReader();
		LocalDate requestDate = lastDate;
		boolean useLimit = false;
		if ( requestDate == null ) {
			requestDate = startTime.atZone(ZoneOffset.UTC).toLocalDate();
			useLimit = true;
		} else {
			requestDate = requestDate.plusDays(1);
		}
		List<LocalDate> list = null;
		try {
			list = storage.listExistingSegments(symbol, requestDate, 1);
		} catch ( DataStorageException e ) {
			throw new IOException(e);
		}
		if ( list.size() == 0 ) {
			return false;
		}
		lastDate = list.get(0);
		reader = new FinamCsvL1UpdateReader(symbol,
				storage.getSegmentFile(new DatedSymbol(symbol, lastDate)));
		if ( useLimit ) {
			reader = new TimeLimitedL1UpdateIterator(reader, startTime);
		}
		return true;
	}

}
