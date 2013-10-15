package ru.prolib.aquila.core.data;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;

/**
 * Сервис записи котировок в csv-файл формата ФИНАМ.
 * <p>
 */
public class FinamCandlesCsvFileWriter implements CandlesWriter, EventListener {
	private static final Logger logger;
	private static final SimpleDateFormat dateFormat, timeFormat;
	private static final String SEPARATOR = ",";
	private static final String EOL = System.getProperty("line.separator");
	
	static {
		logger = LoggerFactory.getLogger(FinamCandlesCsvFileWriter.class);
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		timeFormat = new SimpleDateFormat("HHmmss");
	}
	
	private final CandleSeries candles;
	private final File file;
	private RandomAccessFile target;
	/**
	 * Индекс последней сохраненной свечи.
	 */
	private int savedCandleIndex;
	
	public FinamCandlesCsvFileWriter(CandleSeries candles, File file) {
		super();
		this.candles = candles;
		this.file = file;
	}

	@Override
	public synchronized void start() throws StarterException {
		if ( target != null ) {
			throw new StarterException("Service already started");
		}
		try {
			init();
		} catch ( IOException e ) {
			target = null;
			throw new StarterException("Error initialization: " + file, e);
		}
		logger.debug("Start save candles to: {}", file);
		candles.OnAdded().addListener(this);
	}

	@Override
	public synchronized void stop() throws StarterException {
		if ( target != null ) {
			try {
				target.close();
			} catch ( IOException e ) {
				Object args[] = { file, e };
				logger.error("Error close file: {}", args);
			}
			target = null;
			logger.debug("Stop save candles to: {}", file);
		}
		candles.OnAdded().removeListener(this);
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( target == null ) {
			return;
		}
		try {
			update();
		} catch ( IOException e ) {
			Object args[] = { file, e };
			logger.error("Error update report: {}", args);
		}
	}
	
	/**
	 * Инициализация.
	 * <p>
	 * Создает необходимые объекты. Если целевой файл открывается в нулевой
	 * позиции, то сохраняет строку заголовков CSV-файла. Текущий индекс
	 * последней свечи рассматривается как индекс последней сохраненной. Т.е.
	 * сохраняться будут свечи, начиная со следующей.
	 * <p>
	 * @throws IOException
	 */
	private void init() throws IOException {
		target = new RandomAccessFile(file, "rws");
		FileChannel channel = target.getChannel();
		FileLock lock = channel.lock();
		try {
			if ( target.length() > 0L ) {
				target.seek(target.length());
			} else {
				writeHeaders();
			}
			savedCandleIndex = candles.getLength() - 1;
		} finally {
			try {
				lock.release();
			} catch ( IOException e ) {
				logger.error("Error release lock: ", e);
			}
		}
	}
	
	/**
	 * Обновить файл котировок.
	 * <p>
	 * @throws IOException
	 */
	private void update() throws IOException {
		FileChannel channel = target.getChannel();
		FileLock lock = channel.lock();
		int count = candles.getLength();
		try {
			for ( int i = savedCandleIndex + 1; i < count; i ++ ) {
				long position = channel.position();
				try {
					writeCandle(candles.get(i));
					savedCandleIndex = i;
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
					return;
				} catch ( IOException e ) {
					// restore position
					channel.position(position);
					throw e;
				}
			}
			channel.force(true);
		} finally {
			try {
				lock.release();
			} catch ( IOException e ) {
				logger.error("Error release lock: ", e);
			}
		}
	}
	
	private String formatTime(Date time) {
		return time == null ? null : timeFormat.format(time);
	}
	
	private String formatDate(Date time) {
		return time == null ? null : dateFormat.format(time);
	}
	
	private String joinToString(Object entries[]) {
		if ( entries.length == 0 ) {
			return "";
		}
		String res = entries[0] == null ? "" : entries[0].toString();
		for ( int i = 1; i < entries.length; i ++ ) {
			res += SEPARATOR +
				(entries[i] == null ? "" : entries[i].toString());
		}
		return res;
	}
	
	private void writeLine(String line) throws IOException {
		target.write(line.getBytes());
		target.write(EOL.getBytes());
	}
	
	private void writeHeaders() throws IOException {
		String row[] = {
				Finam.DATE,
				Finam.TIME,
				Finam.OPEN,
				Finam.HIGH,
				Finam.LOW,
				Finam.CLOSE,
				Finam.VOLUME
		};
		writeLine(joinToString(row));
	}
	
	private void writeCandle(Candle candle) throws IOException {
		Object row[] = {
				formatDate(candle.getStartTime().toDate()),
				formatTime(candle.getStartTime().toDate()),
				candle.getOpen(),
				candle.getHigh(),
				candle.getLow(),
				candle.getClose(),
				candle.getVolume()
		};
		writeLine(joinToString(row));
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null
				|| other.getClass() != FinamCandlesCsvFileWriter.class )
		{
			return false;
		}
		FinamCandlesCsvFileWriter o = (FinamCandlesCsvFileWriter) other;
		return new EqualsBuilder()
			.append(o.savedCandleIndex, savedCandleIndex)
			.append(o.candles, candles)
			.append(o.file, file)
			.isEquals();
	}

}
