package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CloseableIteratorOfSeries;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaDataImpl;

public class CacheUtils {
	public static final String FIRST_LINE = "OHLCVv1";
	private static final String UNK_FORMAT_MSG = "Unknown file format: ";
	private static final String TEMP_SUFFIX = ".temp";
	private static final String LS = System.lineSeparator();
	private static final String FS = ",";
	private static final CacheUtils instance;
	
	static {
		instance = new CacheUtils();
	}
	
	public static CacheUtils getInstance() {
		return instance;
	}

	/**
	 * Create segment writer.
	 * <p>
	 * This method creates a writer to save data to temporary file.
	 * The temporary file commit when writer closing.
	 * Then the temporary file delete. 
	 * Do not forget to close the writer!
	 * <p>
	 * @param file - file to write
	 * @return data writer
	 * @throws IOException an error occurred
	 */
	public Writer createWriter(File file) throws IOException {
		file.getParentFile().mkdirs();
		return new CacheSegmentWriter(new File(file.getPath() + TEMP_SUFFIX), file);
	}
	
	/**
	 * Create segment reader.
	 * <p>
	 * @param file - file to read
	 * @return data reader
	 * @throws IOException an error occurred
	 */
	public BufferedReader createReader(File file) throws IOException {
		return new BufferedReader(new FileReader(file));
	}

	/**
	 * Read cache header from file.
	 * <p>
	 * @param file - file to read
	 * @return cache header
	 * @throws IOException an error occurred
	 */
	public CacheHeader readHeader(File file) throws IOException {
		try ( BufferedReader reader = createReader(file) ) {
			return readHeader(reader);
		}
	}

	/**
	 * Read cache header using the reader.
	 * <p>
	 * @param reader - data reader
	 * @return cache header
	 * @throws IOException an error occurred
	 */
	public CacheHeader readHeader(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if ( line == null ) {
			throw new IOException(UNK_FORMAT_MSG + "format ID not found");
		}
		if ( ! FIRST_LINE.equals(line) ) {
			throw new IOException(UNK_FORMAT_MSG + "unknown version: " + line);
		}
		CacheHeaderImpl header = new CacheHeaderImpl();
		line = reader.readLine();
		if ( line == null ) {
			throw new IOException(UNK_FORMAT_MSG + "number of descriptors not found");
		}
		long numberOfDescriptors = 0L;
		try {
			numberOfDescriptors = Long.parseUnsignedLong(line);
		} catch ( NumberFormatException e ) {
			throw new IOException(UNK_FORMAT_MSG + "number of descriptors format error: " + line, e);
		}
		line = reader.readLine();
		if ( line == null ) {
			throw new IOException(UNK_FORMAT_MSG + "number of elements not found");
		}
		try {
			header.setNumberOfElements(Long.parseUnsignedLong(line));
		} catch ( NumberFormatException e ) {
			throw new IOException(UNK_FORMAT_MSG + "number of elements format error: " + line, e);
		}
		for ( long i = 0; i < numberOfDescriptors; i ++ ) {
			line = reader.readLine();
			if ( line == null ) {
				throw new IOException(UNK_FORMAT_MSG + "number of source descriptors mismatch: "
						+ numberOfDescriptors);
			}
			try {
				header.addSourceDescriptor(line);
			} catch ( IllegalArgumentException e ) {
				throw new IOException(UNK_FORMAT_MSG + "incorrect source descriptor: " + i + ": " + line, e);
			}
		}
		return header;
	}

	/**
	 * Get file metadata without reading of header.
	 * <p>
	 * @param file - segment file
	 * @param numberOfElements - number of elements in segment
	 * @return segment metadata
	 * @throws IOException an error occurred
	 */
	public SegmentMetaData getMetaData(File file, long numberOfElements) throws IOException {
		if ( ! file.exists() ) {
			throw new FileNotFoundException("File not found: " + file.getPath());
		}
		Instant updateTime = Instant.ofEpochMilli(file.lastModified());
		return new SegmentMetaDataImpl()
				.setNumberOfElements(numberOfElements)
				.setUpdateTime(updateTime)
				.setPath(file.getPath())
				.setHashCode(DigestUtils.md2Hex(updateTime + "_" + file.length()));
	}
	
	public void writeHeader(Writer writer, CacheHeader header) throws IOException {
		writer.write(FIRST_LINE + LS
				+ header.getNumberOfSourceDescriptors() + LS
				+ header.getNumberOfElements() + LS);
		for ( CacheSourceDescriptor descr : header.getSourceDescriptors() ) {
			writer.write(descr.toString() + LS);
		}
	}
	
	public void writeSeries(Writer writer, Series<Candle> series) throws IOException {
		series.lock();
		try {
			int length = series.getLength();
			for ( int i = 0; i < length; i ++ ) {
				Candle candle = series.get(i);
				writer.write(candle.getStartTime()
						+ FS + candle.getOpen()
						+ FS + candle.getHigh()
						+ FS + candle.getLow()
						+ FS + candle.getClose()
						+ FS + candle.getVolume()
						+ LS);
			}
		} catch ( ValueException e ) {
			throw new IOException("Unexpected exception: ", e);
		} finally {
			series.unlock();
		}
	}
	
	public Candle parseOHLCVv1(String line, TimeFrame tframe) throws IOException {
		String chunks[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, FS);
		if ( chunks.length != 6 ) {
			throw new IOException("Number of fields mismatch: expected 6 but " + chunks.length);
		}
		Instant time;
		Double open, high, low, close;
		Long volume;
		try {
			time = Instant.parse(chunks[0]);
		} catch ( DateTimeParseException e ) {
			throw new IOException("Bad time format: " + chunks[0], e);
		}
		try {
			open = Double.parseDouble(chunks[1]);
		} catch ( NumberFormatException e ) {
			throw new IOException("Bad open price format: " + chunks[1], e);
		}
		try {
			high = Double.parseDouble(chunks[2]);
		} catch ( NumberFormatException e ) {
			throw new IOException("Bad high price format: " + chunks[2], e);
		}
		try {
			low  = Double.parseDouble(chunks[3]);
		} catch ( NumberFormatException e ) {
			throw new IOException("Bad low price format: " + chunks[3], e);
		}
		try {
			close = Double.parseDouble(chunks[4]);
		} catch ( NumberFormatException e ) {
			throw new IOException("Bad close price format: " + chunks[4], e);
		}
		try {
			volume = Long.parseUnsignedLong(chunks[5]);
		} catch ( NumberFormatException e ) {
			throw new IOException("Bad volume format: " + chunks[5], e);
		}
		return new Candle(tframe.getInterval(time), open, high, low, close, volume);
	}
	
	public CloseableIterator<Candle> createIterator(Series<Candle> series) {
		return new CloseableIteratorOfSeries<>(series);
	}
	
	public CloseableIterator<Candle> createIterator(BufferedReader reader, TimeFrame tframe) {
		return new CacheSegmentReader(reader, tframe, this);
	}
	
	public EditableTSeries<Candle>
		buildUsingSourceData(CloseableIterator<L1Update> source, TimeFrame tframe)
			throws Exception
	{
		CSUtils utils = new CSUtils();
		TSeriesImpl<Candle> series = new TSeriesImpl<>(tframe);
		while ( source.next() ) {
			utils.aggregate(series, source.item().getTick());
		}
		return series;
	}

}
