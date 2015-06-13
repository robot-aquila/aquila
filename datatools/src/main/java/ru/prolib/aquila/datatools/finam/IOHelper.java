package ru.prolib.aquila.datatools.finam;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushTickWriter;

public class IOHelper {
	private final IdUtils idUtils;
	private final File root;
	private final Scheduler scheduler;
	private final SmartFlushSetup flushSetup;
	
	public IOHelper(File root) {
		super();
		this.idUtils = new IdUtils();
		this.root = root;
		this.scheduler = new SchedulerLocal();
		this.flushSetup = new SmartFlushSetup();
	}
	
	/**
	 * Get scheduler instance.
	 * <p>
	 * @return scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	/**
	 * Get smart flush setup.
	 * <p>
	 * @return smart flush setup
	 */
	public SmartFlushSetup getFlushSetup() {
		return flushSetup;
	}
	
	/**
	 * Create raw output stream.
	 * <p>
	 * @param file - the output file
	 * @param append - true - append, false - overwrite existing file
	 * @return the output stream
	 * @throws FileNotFoundException - file not found
	 */
	public OutputStream createOutputStream(File file, boolean append)
			throws FileNotFoundException
	{
		return new BufferedOutputStream(new FileOutputStream(file, append));
	}
	
	/**
	 * Create gzipped output stream.
	 * <p>
	 * @param file - the output file
	 * @return the output stream
	 * @throws IOException - file not found or error opening file 
	 */
	public OutputStream createGzipOutputStream(File file)
			throws IOException
	{
		return new GZIPOutputStream(createOutputStream(file, false));
	}
	
	/**
	 * Create raw input stream.
	 * <p>
	 * @param file - the input file
	 * @return the input stream
	 * @throws IOException - file not found or error opening file
	 */
	public InputStream createInputStream(File file)
			throws IOException
	{
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	/**
	 * Create gzipped input stream.
	 * <p>
	 * @param file - the input file
	 * @return the input stream
	 * @throws IOException - file not found ot error opening file
	 */
	public InputStream createGzipInputStream(File file)
			throws IOException
	{
		return new GZIPInputStream(createInputStream(file));
	}
	
	/**
	 * Get data segment filename.
	 * <p> 
	 * @param descr - security descriptor
	 * @param date - the date of segment
	 * @param suffix - file suffix
	 * @return the full path to segment file
	 */
	public File getFile(SecurityDescriptor descr, LocalDate date,
			String suffix)
	{
		File file = getLevel2Dir(descr, date);
		file = new File(file, idUtils.getSafeId(descr, date) + suffix);
		return file;
	}
	
	/**
	 * Get the root directory of the security data.
	 * <p>
	 * @param descr - security descriptor
	 * @return the root directory of the security data storage
	 */
	public File getRootDir(SecurityDescriptor descr) {
		return new File(root, idUtils.getSafeId(descr));
	}
	
	/**
	 * Get Level 1 directory of security data.
	 * <p>
	 * The Level 1 directory contains data of the year. 
	 * <p>
	 * @param descr - security descriptor
	 * @param date - the date to get year of
	 * @return the directory
	 */
	public File getLevel1Dir(SecurityDescriptor descr, LocalDate date) {
		File file = getRootDir(descr);
		file = new File(file, String.format("%04d", date.getYear()));
		return file;
	}
	
	/**
	 * Get Level 2 directory of security data.
	 * <p>
	 * The Level 2 directory contains data of the month of year.
	 * <p>
	 * @param descr - security descriptor
	 * @param date - the date to get month of 
	 * @return the directory
	 */
	public File getLevel2Dir(SecurityDescriptor descr, LocalDate date) {
		File file = getLevel1Dir(descr, date);
		file = new File(file, String.format("%02d", date.getMonthOfYear()));
		return file;
	}
	
	/**
	 * Copy stream.
	 * <p>
	 * @param input - the source stream
	 * @param output - the output stream
	 * @throws IOException - IO error
	 */
	public void copyStream(InputStream input, OutputStream output)
		throws IOException
	{
		IOUtils.copy(input, output);
	}
	
	/**
	 * Create csv-writer for the tick data.
	 * <p>
	 * @param security - security instance
	 * @param output - the output stream
	 * @return the writer
	 */
	public CsvTickWriter
		createCsvTickWriter(Security security, OutputStream output)
	{
		return new CsvTickWriter(security, output);
	}
	
	/**
	 * Decorate tick writer with smart flusher.
	 * <p>
	 * @param writer - the writer to decorate
	 * @param streamId - stream ID (for debug messages)
	 * @return decorated writer
	 */
	public TickWriter addSmartFlush(TickWriter writer, String streamId) {
		return new SmartFlushTickWriter(writer, scheduler, streamId, flushSetup);
	}

}
