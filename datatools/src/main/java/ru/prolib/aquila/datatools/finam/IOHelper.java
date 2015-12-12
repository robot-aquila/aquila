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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushTickWriter;

public class IOHelper {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IOHelper.class);
	}

	
	private final Map<File, Integer> mapInvalidFileCount;
	private final IdUtils idUtils;
	private final File root;
	private final Scheduler scheduler;
	private final SmartFlushSetup flushSetup;
	public static final String DATA_FILE_EXT = ".csv";
	public static final String ARCH_FILE_EXT = ".csv.gz";
	public static final String TEMP_FILE_EXT = ".csv.gz.part";
	public static final String PART_FILE_EXT = ".csv.part";
	
	public IOHelper(File root) {
		super();
		this.mapInvalidFileCount = new Hashtable<File, Integer>();
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
	 * @param symbol - symbol info
	 * @param date - the date of segment
	 * @param suffix - file suffix
	 * @return the full path to segment file
	 */
	public File getFile(Symbol symbol, LocalDate date, String suffix) {
		File file = getLevel2Dir(symbol, date);
		file = new File(file, idUtils.getSafeId(symbol, date) + suffix);
		return file;
	}
	
	/**
	 * Get the root directory of the security data.
	 * <p>
	 * @param symbol - symbol info
	 * @return the root directory of the security data storage
	 */
	public File getRootDir(Symbol symbol) {
		return new File(root, idUtils.getSafeId(symbol));
	}
	
	/**
	 * Get Level 1 directory of security data.
	 * <p>
	 * The Level 1 directory contains data of the year. 
	 * <p>
	 * @param symbol - symbol info
	 * @param date - the date to get year of
	 * @return the directory
	 */
	public File getLevel1Dir(Symbol symbol, LocalDate date) {
		File file = getRootDir(symbol);
		file = new File(file, String.format("%04d", date.getYear()));
		return file;
	}
	
	/**
	 * Get Level 2 directory of security data.
	 * <p>
	 * The Level 2 directory contains data of the month of year.
	 * <p>
	 * @param symbol - symbol info
	 * @param date - the date to get month of 
	 * @return the directory
	 */
	public File getLevel2Dir(Symbol symbol, LocalDate date) {
		File file = getLevel1Dir(symbol, date);
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
	 * @param output - the output stream
	 * @return the writer
	 */
	public CsvTickWriter createCsvTickWriter(OutputStream output) {
		return new CsvTickWriter(output);
	}
	
	/**
	 * Create csv-reader for the data segment.
	 * <p>
	 * @param inputStream - the input stream
	 * @param date - date
	 * @return tick-data reader
	 * @throws IOException - IO error
	 */
	public CsvTickReader
		createCsvTickReader(InputStream inputStream, LocalDate date)
			throws IOException
	{
		return new CsvTickReader(inputStream, date);
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
	
	/**
	 * Get list of dates of available segments.
	 * <p>
	 * @param symbol - symbol info
	 * @return list of dates
	 * @throws IOException - IO error
	 */
	public List<LocalDate> getAvailableDataSegments(Symbol symbol)
			throws IOException
	{
		List<LocalDate> result = new Vector<LocalDate>();
		DateTimeFormatter df = DateTimeFormat.forPattern("yyyyMMdd");
		String prefix = idUtils.getSafeId(symbol);
		int dateStart = prefix.length() + 1, dateLength = 8;
		Iterator<File> it = FileUtils.iterateFiles(getRootDir(symbol), null, true);
		while ( it.hasNext() ) {
			File x = it.next();
			if ( x.isDirectory() ) {
				continue;
			}
			File p = x.getParentFile();
			if ( p == null || ! StringUtils.isNumeric(p.getName())) {
				invalidFilename(x);
				continue;
			}
			p = p.getParentFile();
			if ( p == null || ! StringUtils.isNumeric(p.getName())) {
				invalidFilename(x);
				continue;
			}
			if ( ! p.getParentFile().getName().equals(prefix)) {
				invalidFilename(x);
				continue;
			}
			
			String filename =  x.getName();
			if ( ! filename.startsWith(prefix) ) {
				invalidFilename(x);
				continue;
			}
			int end = dateStart + dateLength;
			if ( end >= filename.length()) {
				invalidFilename(x);
				continue;
			}
			String datestr = filename.substring(dateStart, end);
			String ext = filename.substring(end);
			if ( ! ext.equals(ARCH_FILE_EXT) && ! ext.equals(DATA_FILE_EXT) ) {
				invalidFilename(x);
				continue;
			}
			try {
				LocalDate date = df.parseLocalDate(datestr);
				if ( ! result.contains(date) ) {
					result.add(date);
				}
			} catch ( Exception e ) {
				invalidFilename(x);
			}
			
		}
		Collections.sort(result);
		return result;
	}
	
	private synchronized void invalidFilename(File x) {
		Integer count = mapInvalidFileCount.get(x); 
		if ( count == null ) {
			count = 1;
			logger.warn("Invalid filename detected: {}", x);
		} else {
			count ++;
		}
		mapInvalidFileCount.put(x, count);
	}

}
