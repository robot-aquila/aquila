package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;
import ru.prolib.aquila.core.data.Tick;

class InMemoryManager implements DataSegmentManager {
	private HashMap<Symbol, HashMap<LocalDate, List<Tick>>> data;
	
	public InMemoryManager() {
		data = new HashMap<Symbol, HashMap<LocalDate, List<Tick>>>();
	}
	
	public void setSegmentData(Symbol symbol, LocalDate date, List<Tick> list) {
		HashMap<LocalDate, List<Tick>> x1 = data.get(symbol);
		if ( x1 == null ) {
			x1 = new HashMap<LocalDate, List<Tick>>();
			data.put(symbol, x1);
		}
		x1.put(date, new Vector<Tick>(list));
	}

	@Override
	public DataSegment openSegment(Symbol symbol, LocalDate date)
			throws IOException
	{
		throw new IOException("Not implemented");
	}

	@Override
	public void closeSegment(DataSegment segment) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public Aqiterator<Tick> openReader(Symbol symbol, LocalDate date) throws IOException {
		return new SimpleIterator<Tick>(data.get(symbol).get(date));
	}

	@Override
	public void closeReader(Aqiterator<Tick> reader) throws IOException {
		throw new IOException("Not implemented");			
	}

	@Override
	public boolean isDataAvailable(Symbol symbol) throws IOException {
		return data.containsKey(symbol);
	}

	@Override
	public boolean isDataAvailable(Symbol symbol, LocalDate date) throws IOException {
		return isDataAvailable(symbol) && data.get(symbol).containsKey(date);
	}

	@Override
	public LocalDate getDateOfFirstSegment(Symbol symbol) throws IOException {
		if ( ! isDataAvailable(symbol) ) {
			return null;
		}
		List<LocalDate> dates = new Vector<LocalDate>(data.get(symbol).keySet());
		Collections.sort(dates);
		return dates.size() > 0 ? dates.get(0) : null;
	}

	@Override
	public LocalDate getDateOfNextSegment(Symbol symbol, LocalDate date) throws IOException {
		if ( ! isDataAvailable(symbol) ) {
			return null;
		}
		List<LocalDate> dates = new Vector<LocalDate>(data.get(symbol).keySet());
		Collections.sort(dates);
		for ( LocalDate x : dates ) {
			if ( x.isAfter(date) ) {
				return x;
			}
		}
		return null;
	}

	@Override
	public LocalDate getDateOfLastSegment(Symbol symbol) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public List<LocalDate> getSegmentList(Symbol symbol) throws IOException {
		throw new IOException("Not implemented");
	}
	
}