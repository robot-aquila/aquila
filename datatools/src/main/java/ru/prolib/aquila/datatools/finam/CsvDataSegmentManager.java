package ru.prolib.aquila.datatools.finam;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentManager;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriterImpl;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushTickWriter;

public class CsvDataSegmentManager implements DataSegmentManager {
	private static final String PART_FILE_EXT = ".csv.part";
	private static final String TEMP_FILE_EXT = ".csv.gz.part";
	private static final String ARCH_FILE_EXT = ".csv.gz";
	private final Scheduler scheduler;
	private final IdUtils idUtils;
	private final Terminal terminal;
	private final File root;
	private final SmartFlushSetup flushSetup;
	
	public CsvDataSegmentManager(Terminal terminal, File dbpath,
			Scheduler scheduler, SmartFlushSetup flushSetup)
					throws FinamException
	{
		super();
		this.idUtils = new IdUtils();
		this.terminal = terminal;
		this.root = dbpath;
		if ( ! root.isDirectory() ) {
			throw new FinamException("Directory not exists: " + root);
		}
		this.scheduler = scheduler;
		this.flushSetup = flushSetup;
	}

	@Override
	public DataSegmentWriter open(SecurityDescriptor descr, LocalDate date)
			throws GeneralException
	{
		File file = getFile(descr, date, PART_FILE_EXT);
								// TODO: Just better for QUIK. 
								// Need improvements for universal approach.
		boolean append = false; // file.exists() && file.length() > 0;
		try {
			Security security = terminal.getSecurity(descr);
			OutputStream stream = createDataStream(file, append);
			CsvTickWriter writer = new CsvTickWriter(security, stream);
			if ( ! append ) writer.writeHeader();
			String streamId = "[" + descr + "#" + date + "]";
			SmartFlushTickWriter smartWriter = new SmartFlushTickWriter(writer,
					scheduler, streamId, flushSetup);
			return new DataSegmentWriterImpl(descr, date, smartWriter);
		} catch ( Exception e ) {
			throw new GeneralException(e);
		}
	}

	@Override
	public void close(DataSegmentWriter writer) throws GeneralException {
		try {
			writer.close();
			makeArchive(writer.getSecurityDescriptor(), writer.getDate());
		
			
			
		} catch ( java.io.IOException e ) {
			throw new ru.prolib.aquila.datatools.IOException(e);
		}
	}
	
	private void makeArchive(SecurityDescriptor descr, LocalDate date)
			throws java.io.IOException
	{
		File part = getFile(descr, date, PART_FILE_EXT),
			temp = getFile(descr, date, TEMP_FILE_EXT);
		InputStream input = new BufferedInputStream(new FileInputStream(part));
		OutputStream output = createGzipStream(temp);
		IOUtils.copy(input, output);
		input.close();
		output.close();
		temp.renameTo(getFile(descr, date, ARCH_FILE_EXT));
		part.delete();
	}
	
	protected File getFile(SecurityDescriptor descr, LocalDate date,
			String suffix)
	{
		File file = new File(root, idUtils.getSafeId(descr));
		file = new File(file, String.format("%04d", date.getYear()));
		file = new File(file, String.format("%02d", date.getMonthOfYear()));
		if ( ! file.exists() ) {
			file.mkdirs();
		}
		file = new File(file, idUtils.getSafeId(descr, date) + suffix);
		return file;
	}
	
	protected OutputStream createDataStream(File file, boolean append)
		throws java.io.IOException
	{
		return new BufferedOutputStream(new FileOutputStream(file, append));
	}
	
	protected OutputStream createGzipStream(File file)
		throws java.io.IOException
	{
		return new GZIPOutputStream(createDataStream(file, true));
	}

}
