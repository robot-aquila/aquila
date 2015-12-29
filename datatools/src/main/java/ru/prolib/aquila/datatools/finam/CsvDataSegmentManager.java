package ru.prolib.aquila.datatools.finam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegment;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentManager;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentImpl;

public class CsvDataSegmentManager implements DataSegmentManager {
	private static final Logger logger;
	static {
		logger = LoggerFactory.getLogger(CsvDataSegmentManager.class);
	}
	
	private final IOHelper helper;
	
	public CsvDataSegmentManager(File dbpath) throws FinamException {
		this(new IOHelper(dbpath));
		if ( ! dbpath.isDirectory() ) {
			throw new FinamException("Directory not exists: " + dbpath);
		}	
	}
	
	public CsvDataSegmentManager(String dbpath) throws FinamException {
		this(new File(dbpath));
	}
	
	public CsvDataSegmentManager(IOHelper helper) {
		super();
		this.helper = helper;
	}
	
	public void setSmartFlushExecutionPeriod(long period) {
		helper.getFlushSetup().setExecutionPeriod(period);
	}
	
	public void setFlushPeriod(long period) {
		helper.getFlushSetup().setFlushPeriod(period);
	}

	@Override
	public DataSegment openSegment(Symbol symbol, LocalDate date) throws IOException {
		File file = helper.getFile(symbol, date, IOHelper.PART_FILE_EXT);
		File parent = file.getParentFile();
		if ( ! parent.exists() ) {
			parent.mkdirs();
		}
								// TODO: Just better for QUIK. 
								// Need improvements for universal approach.
		boolean append = false; // file.exists() && file.length() > 0;
		try {
			OutputStream stream = helper.createOutputStream(file, append);
			CsvTickWriter writer = helper.createCsvTickWriter(stream);
			if ( ! append ) writer.writeHeader();
			return new DataSegmentImpl(symbol, date,
					helper.addSmartFlush(writer, getStreamId(symbol, date)));
		} catch ( IOException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new IOException(e);
		}
	}
	
	private String getStreamId(Symbol symbol, LocalDate date) {
		 return "[" + symbol + "#" + date + "]";
	}

	@Override
	public void closeSegment(DataSegment segment) throws IOException {
		Symbol symbol = segment.getSymbol();
		LocalDate date = segment.getDate();
		segment.close();
		File part = helper.getFile(symbol, date, IOHelper.PART_FILE_EXT),
			temp = helper.getFile(symbol, date, IOHelper.TEMP_FILE_EXT),
			dest = helper.getFile(symbol, date, IOHelper.ARCH_FILE_EXT);
		InputStream input = helper.createInputStream(part);
		OutputStream output = helper.createGzipOutputStream(temp);
		helper.copyStream(input, output);
		input.close();
		output.close();
		dest.delete();
		temp.renameTo(dest);
		part.delete();
		logger.debug("Data segment closed: {}", getStreamId(symbol, date));
	}
	
	@Override
	public Aqiterator<Tick> openReader(Symbol symbol, LocalDate date) throws IOException {
		InputStream input = null;
		File zip = helper.getFile(symbol, date, IOHelper.ARCH_FILE_EXT),
			raw = helper.getFile(symbol, date, IOHelper.DATA_FILE_EXT);
		if ( zip.exists() ) {
			input = helper.createGzipInputStream(zip);
		} else if ( raw.exists() ) {
			input = helper.createInputStream(raw);
		} else {
			throw new IOException("No segment: " + getStreamId(symbol, date));
		}
		CsvTickReader reader = helper.createCsvTickReader(input, date);
		reader.readHeader();
		return reader;
	}

	@Override
	public void closeReader(Aqiterator<Tick> reader) throws IOException {
		reader.close();
	}

	@Override
	public boolean isDataAvailable(Symbol symbol) throws IOException {
		List<LocalDate> list = helper.getAvailableDataSegments(symbol);
		return list.size() > 0;
	}

	@Override
	public boolean isDataAvailable(Symbol symbol, LocalDate date) throws IOException {
		List<LocalDate> list = helper.getAvailableDataSegments(symbol);
		return list.contains(date);
	}

	@Override
	public LocalDate getDateOfFirstSegment(Symbol symbol) throws IOException {
		List<LocalDate> list = helper.getAvailableDataSegments(symbol);
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public LocalDate getDateOfLastSegment(Symbol symbol) throws IOException {
		List<LocalDate> list = helper.getAvailableDataSegments(symbol);
		int index = list.size() - 1;
		return index < 0 ? null : list.get(index);
	}

	@Override
	public LocalDate getDateOfNextSegment(Symbol symbol, LocalDate date) throws IOException {
		List<LocalDate> list = helper.getAvailableDataSegments(symbol);
		for ( LocalDate x : list ) {
			if ( x.isAfter(date) ) {
				return x;
			}
		}
		return null;
	}

	@Override
	public List<LocalDate> getSegmentList(Symbol symbol) throws IOException {
		return helper.getAvailableDataSegments(symbol);
	}

}
