package ru.prolib.aquila.core.report.io;

import java.io.*;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

/**
 * Сервис записи трейд-отчета в CSV-файл.
 * <p>
 * Выполняет обновление файла в момент поступления обновлений отчета. Работает
 * по принципу обновления хвоста файла, начиная с той позиции, которая
 * соответствует наиболее раннему отчету с непокрытым количеством. При
 * проходе сохранения корректирует позиции незакрытого отчета.
 */
public class TradesCsvFileWriter implements TradesWriter, EventListener {
	private static final Logger logger;
	private static final SimpleDateFormat dateFormat;
	private static final String SEPARATOR = ",";
	private static final String EOL = System.getProperty("line.separator");
	
	static {
		logger = LoggerFactory.getLogger(TradesCsvFileWriter.class);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	private final TradeReport report;
	private final File file;
	private RandomAccessFile target;
	/**
	 * Индекс самого раннего незакрытого отчета в списке отчетов.
	 */
	private int uncoveredIndex;
	/**
	 * Файловая позиция самого раннего незакрытого отчета.
	 */
	private long uncoveredPos;
	
	public TradesCsvFileWriter(TradeReport report, File file) {
		super();
		this.report = report;
		this.file = file;
	}
	
	public TradesCsvFileWriter(TradeReport report, String filename) {
		this(report, new File(filename));
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
		logger.debug("Start save trades to: {}", file);
		report.OnChanged().addListener(this);
		report.OnEnter().addListener(this);
		report.OnExit().addListener(this);
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
			logger.debug("Stop save trades to: {}", file);
		}
		report.OnChanged().removeListener(this);
		report.OnEnter().removeListener(this);
		report.OnExit().removeListener(this);
	}

	@Override
	public synchronized void onEvent(Event event) {
		try {
			update();
		} catch ( Exception e ) {
			Object args[] = { file, e };
			logger.error("Error update report: {}", args);
		}
	}
	
	/**
	 * Инициализация.
	 * <p>
	 * Создает необходимые объекты. Если файл открывается с нулевой позиции,
	 * то сохраняет строку заголовков CSV-отчета. В конце всех процедур
	 * обнуляет индекс несохраненного отчета и файловую позицию, соответствующую
	 * этому отчету.
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
			uncoveredIndex = 0;
			uncoveredPos = target.getFilePointer();
		} finally {
			try {
				lock.release();
			} catch ( IOException e ) {
				logger.error("Error release lock: ", e);
			}
		}
	}

	/**
	 * Обновление отчета.
	 * <p>
	 * Обновляет файл начиная с самого раннего непокрытого отчета в списке
	 * отчетов. Корректирует индекс и позицию после сохранения каждой строки
	 * отчета.
	 * <p>
	 * @throws IOException
	 */
	private void update() throws IOException {
		FileChannel channel = target.getChannel();
		FileLock lock = channel.lock();
		List<RTrade> list = report.getRecords();
		int size = list.size();
		try {
			channel.position(uncoveredPos);
			for ( int i = uncoveredIndex; i < size; i ++ ) {
				RTrade row = list.get(i);
				writeReport(row);
				if ( i == uncoveredIndex && ! row.isOpen() ) {
					// Если это был текущий незакрытый отчет и теперь он
					// закрылся, то сдвигаем индекс на следующий отчет,
					// сохраняем файловую позицию на начало следующей записи.
					uncoveredIndex = i + 1;
					uncoveredPos = target.getFilePointer();
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
	
	private String formatTime(DateTime time) {
		if ( time == null ) {
			return null;
		}
		return dateFormat.format(time.toDate());
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
				"Type",
				"Security",
				"Qty",
				"Uncovered",
				"EnterTime",
				"EnterPrice",
				"ExitTime",
				"ExitPrice",
				"EnterVol",
				"ExitVol",
				"P&L",
				"P&L%"
		};
		writeLine(joinToString(row));
	}
	
	private void writeReport(RTrade report) throws IOException {
		Object[] row = {
				report.getType(),
				report.getSecurityDescriptor(),
				report.getQty(),
				report.getUncoveredQty(),
				formatTime(report.getEnterTime()), 
				report.getEnterPrice(),
				formatTime(report.getExitTime()),
				report.getExitPrice(),
				report.getEnterVolume(),
				report.getExitVolume(),
				report.getProfit(),
				report.getProfitPerc()
		};
		writeLine(joinToString(row));
	}
	
}
