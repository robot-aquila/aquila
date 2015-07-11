package ru.prolib.aquila.datatools.finam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
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
	private final Terminal terminal;
	
	public CsvDataSegmentManager(Terminal terminal, File dbpath)
		throws FinamException
	{
		this(terminal, new IOHelper(dbpath));
		if ( ! dbpath.isDirectory() ) {
			throw new FinamException("Directory not exists: " + dbpath);
		}	
	}
	
	public CsvDataSegmentManager(Terminal terminal, IOHelper helper) {
		super();
		this.helper = helper;
		this.terminal = terminal;
	}
	
	public void setSmartFlushExecutionPeriod(long period) {
		helper.getFlushSetup().setExecutionPeriod(period);
	}
	
	public void setFlushPeriod(long period) {
		helper.getFlushSetup().setFlushPeriod(period);
	}

	@Override
	public DataSegment
		openSegment(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		File file = helper.getFile(descr, date, IOHelper.PART_FILE_EXT);
		File parent = file.getParentFile();
		if ( ! parent.exists() ) {
			parent.mkdirs();
		}
								// TODO: Just better for QUIK. 
								// Need improvements for universal approach.
		boolean append = false; // file.exists() && file.length() > 0;
		try {
			Security security = terminal.getSecurity(descr);
			OutputStream stream = helper.createOutputStream(file, append);
			CsvTickWriter writer = helper.createCsvTickWriter(security, stream);
			if ( ! append ) writer.writeHeader();
			return new DataSegmentImpl(descr, date,
					helper.addSmartFlush(writer, getStreamId(descr, date)));
		} catch ( IOException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new IOException(e);
		}
	}
	
	private String getStreamId(SecurityDescriptor descr, LocalDate date) {
		 return "[" + descr + "#" + date + "]";
	}

	@Override
	public void closeSegment(DataSegment segment) throws IOException {
		SecurityDescriptor descr = segment.getSecurityDescriptor();
		LocalDate date = segment.getDate();
		segment.close();
		File part = helper.getFile(descr, date, IOHelper.PART_FILE_EXT),
			temp = helper.getFile(descr, date, IOHelper.TEMP_FILE_EXT),
			dest = helper.getFile(descr, date, IOHelper.ARCH_FILE_EXT);
		InputStream input = helper.createInputStream(part);
		OutputStream output = helper.createGzipOutputStream(temp);
		helper.copyStream(input, output);
		input.close();
		output.close();
		temp.renameTo(dest);
		part.delete();
		logger.debug("Data segment closed: {}", getStreamId(descr, date));
	}
	
	@Override
	public Aqiterator<Tick> openReader(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		InputStream input = null;
		File zip = helper.getFile(descr, date, IOHelper.ARCH_FILE_EXT),
			raw = helper.getFile(descr, date, IOHelper.DATA_FILE_EXT);
		if ( zip.exists() ) {
			input = helper.createGzipInputStream(zip);
		} else if ( raw.exists() ) {
			input = helper.createInputStream(raw);
		} else {
			throw new IOException("No segment: " + getStreamId(descr, date));
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
	public boolean isDataAvailable(SecurityDescriptor descr)
			throws IOException
	{
		List<LocalDate> list = helper.getAvailableDataSegments(descr);
		return list.size() > 0;
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		List<LocalDate> list = helper.getAvailableDataSegments(descr);
		return list.contains(date);
	}

	@Override
	public LocalDate getDateOfFirstSegment(SecurityDescriptor descr)
			throws IOException
	{
		List<LocalDate> list = helper.getAvailableDataSegments(descr);
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public LocalDate getDateOfLastSegment(SecurityDescriptor descr)
			throws IOException
	{
		List<LocalDate> list = helper.getAvailableDataSegments(descr);
		int index = list.size() - 1;
		return index < 0 ? null : list.get(index);
	}

	@Override
	public LocalDate
		getDateOfNextSegment(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		List<LocalDate> list = helper.getAvailableDataSegments(descr);
		for ( LocalDate x : list ) {
			if ( x.isAfter(date) ) {
				return x;
			}
		}
		return null;
	}

	@Override
	public List<LocalDate> getSegmentList(SecurityDescriptor descr)
			throws IOException
	{
		return helper.getAvailableDataSegments(descr);
	}

}
