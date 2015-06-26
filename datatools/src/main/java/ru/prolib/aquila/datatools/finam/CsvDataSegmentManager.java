package ru.prolib.aquila.datatools.finam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private static final String PART_FILE_EXT = ".csv.part";
	private static final String TEMP_FILE_EXT = ".csv.gz.part";
	private static final String ARCH_FILE_EXT = ".csv.gz";
	
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
		File file = helper.getFile(descr, date, PART_FILE_EXT);
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
		File part = helper.getFile(descr, date, PART_FILE_EXT),
			temp = helper.getFile(descr, date, TEMP_FILE_EXT),
			dest = helper.getFile(descr, date, ARCH_FILE_EXT);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeReader(Aqiterator<Tick> reader) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr)
			throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr, LocalDate date)
			throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalDate getDateOfFirstSegment(SecurityDescriptor descr)
			throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getDateOfLastSegment(SecurityDescriptor descr)
			throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
