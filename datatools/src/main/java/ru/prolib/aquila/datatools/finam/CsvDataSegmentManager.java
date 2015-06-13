package ru.prolib.aquila.datatools.finam;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentManager;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriterImpl;

public class CsvDataSegmentManager implements DataSegmentManager {
	private static final String PART_FILE_EXT = ".csv.part";
	private static final String TEMP_FILE_EXT = ".csv.gz.part";
	private static final String ARCH_FILE_EXT = ".csv.gz";
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
	public DataSegmentWriter
		openWriter(SecurityDescriptor descr, LocalDate date)
			throws GeneralException
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
			String streamId = "[" + descr + "#" + date + "]";
			return new DataSegmentWriterImpl(descr, date,
					helper.addSmartFlush(writer, streamId));
		} catch ( Exception e ) {
			throw new GeneralException(e);
		}
	}

	@Override
	public void close(DataSegmentWriter writer) throws GeneralException {
		try {
			SecurityDescriptor descr = writer.getSecurityDescriptor();
			LocalDate date = writer.getDate();
			writer.close();
			File part = helper.getFile(descr, date, PART_FILE_EXT),
					temp = helper.getFile(descr, date, TEMP_FILE_EXT);
			InputStream input = helper.createInputStream(part);
			OutputStream output = helper.createGzipOutputStream(temp);
			helper.copyStream(input, output);
			input.close();
			output.close();
			temp.renameTo(helper.getFile(descr, date, ARCH_FILE_EXT));
			part.delete();
		} catch ( java.io.IOException e ) {
			throw new ru.prolib.aquila.datatools.IOException(e);
		}
	}
	
	@Override
	public Aqiterator<Tick> openReader(SecurityDescriptor descr, LocalDate date)
			throws GeneralException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close(Aqiterator<Tick> reader) throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr)
			throws GeneralException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDataAvailable(SecurityDescriptor descr, LocalDate date)
			throws GeneralException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalDate getFirstSegment(SecurityDescriptor descr)
			throws GeneralException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getLastSegment(SecurityDescriptor descr)
			throws GeneralException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
