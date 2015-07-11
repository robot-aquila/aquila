package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;
import ru.prolib.aquila.core.data.Tick;

class InMemoryManager implements DataSegmentManager {
	private HashMap<SecurityDescriptor, HashMap<LocalDate, List<Tick>>> data;
	
	public InMemoryManager() {
		data = new HashMap<SecurityDescriptor, HashMap<LocalDate, List<Tick>>>();
	}
	
	public void setSegmentData(SecurityDescriptor descr, LocalDate date, List<Tick> list) {
		HashMap<LocalDate, List<Tick>> x1 = data.get(descr);
		if ( x1 == null ) {
			x1 = new HashMap<LocalDate, List<Tick>>();
			data.put(descr, x1);
		}
		x1.put(date, new Vector<Tick>(list));
	}

	@Override
	public DataSegment openSegment(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		throw new IOException("Not implemented");
	}

	@Override
	public void closeSegment(DataSegment segment) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public Aqiterator<Tick>
		openReader(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		return new SimpleIterator<Tick>(data.get(descr).get(date));
	}

	@Override
	public void closeReader(Aqiterator<Tick> reader) throws IOException {
		throw new IOException("Not implemented");			
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr)
			throws IOException
	{
		return data.containsKey(descr);
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		return isDataAvailable(descr) && data.get(descr).containsKey(date);
	}

	@Override
	public LocalDate getDateOfFirstSegment(SecurityDescriptor descr)
			throws IOException
	{
		if ( ! isDataAvailable(descr) ) {
			return null;
		}
		List<LocalDate> dates = new Vector<LocalDate>(data.get(descr).keySet());
		Collections.sort(dates);
		return dates.size() > 0 ? dates.get(0) : null;
	}

	@Override
	public LocalDate getDateOfNextSegment(SecurityDescriptor descr,
			LocalDate date) throws IOException
	{
		if ( ! isDataAvailable(descr) ) {
			return null;
		}
		List<LocalDate> dates = new Vector<LocalDate>(data.get(descr).keySet());
		Collections.sort(dates);
		for ( LocalDate x : dates ) {
			if ( x.isAfter(date) ) {
				return x;
			}
		}
		return null;
	}

	@Override
	public LocalDate getDateOfLastSegment(SecurityDescriptor descr)
			throws IOException
	{
		throw new IOException("Not implemented");
	}

	@Override
	public List<LocalDate> getSegmentList(SecurityDescriptor descr)
			throws IOException
	{
		throw new IOException("Not implemented");
	}
	
}