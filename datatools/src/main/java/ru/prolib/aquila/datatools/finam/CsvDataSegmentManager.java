package ru.prolib.aquila.datatools.finam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentManager;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriter;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataSegmentWriterImpl;

public class CsvDataSegmentManager implements DataSegmentManager {
	private static final String FILE_EXT = ".csv.gz"; 
	private final IdUtils idUtils;
	private final Terminal terminal;
	private final File root;
	
	public CsvDataSegmentManager(Terminal terminal, File dbpath)
			throws FinamException
	{
		super();
		this.idUtils = new IdUtils();
		this.terminal = terminal;
		this.root = dbpath;
		if ( ! root.isDirectory() ) {
			throw new FinamException("Directory not exists: " + root);
		}
	}

	@Override
	public DataSegmentWriter open(SecurityDescriptor descr, LocalDate date)
			throws GeneralException
	{
		File file = getFile(descr, date);
		CsvTickWriter writer = null;
								// TODO: Just better for QUIK. 
								// Need improvements for universal approach.
		boolean append = false; // file.exists() && file.length() > 0;
		try {
			writer = new CsvTickWriter(terminal.getSecurity(descr),
					createStream(file, append));
			if ( ! append ) writer.writeHeader();
			return new DataSegmentWriterImpl(descr, date, writer);
		} catch ( Exception e ) {
			throw new GeneralException(e);
		}
	}

	@Override
	public void close(DataSegmentWriter writer) throws GeneralException {
		try {
			writer.close();
		} catch ( java.io.IOException e ) {
			throw new ru.prolib.aquila.datatools.IOException(e);
		}
	}
	
	protected File getFile(SecurityDescriptor descr, LocalDate date) {
		File file = new File(root, idUtils.getSafeId(descr));
		file = new File(file, String.format("%04d", date.getYear()));
		file = new File(file, String.format("%02d", date.getMonthOfYear()));
		if ( ! file.exists() ) {
			file.mkdirs();
		}
		file = new File(file, idUtils.getSafeId(descr, date) + FILE_EXT);
		return file;
	}
	
	protected OutputStream createStream(File file, boolean append)
		throws java.io.IOException
	{
		return new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file, append)));
	}

}
