package ru.prolib.aquila.core.data;

import java.io.*;
import java.text.*;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.data.finam.CsvTickReader;
import ru.prolib.aquila.core.data.finam.Quote2CsvWriter;
import ru.prolib.aquila.core.data.row.*;

/**
 * Фасад к функциям обработки данных формата FINAM.
 * <p>
 * 2013-03-07<br>
 * $Id: Finam.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class Finam {
	public static final String DATE = "<DATE>";
	public static final String TIME = "<TIME>";
	public static final String OPEN = "<OPEN>";
	public static final String HIGH = "<HIGH>";
	public static final String LOW = "<LOW>";
	public static final String CLOSE = "<CLOSE>";
	public static final String VOLUME = "<VOL>";
	public static final String LAST = "<LAST>";
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyyMMdd HHmmss");
	}
	
	/**
	 * Получить временную метку из его строкового представления в формате FINAM.
	 * <p>
	 * @param date строковое представление даты
	 * @param time строковое представление времени
	 * @return временная метка
	 * @throws ParseException некорректный формат даты или времени
	 */
	public static DateTime parseDateTime(String date, String time)
			throws ParseException
	{
		try {
			return df.parseDateTime(date + " " + time);
		} catch ( IllegalArgumentException e ) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/**
	 * Загрузить свечи из CSV-файла.
	 * <p>
	 * @param csvfile файл с котировками в формате ФИНАМ CSV
	 * @param candles целевой набор свечей
	 * @throws FileNotFoundException - If the CSV file does not exists.
	 * @throws ParseException - If the CSV file contanins corrupted data.
	 * @throws ValueException - If error occured.
	 * @throws NumberFormatException - If error occured.
	 * @throws RowSetException - If error occured.
	 */
	public void loadCandles(File csvfile, EditableCandleSeries candles)
			throws FileNotFoundException, ParseException, NumberFormatException,
				ValueException
	{
		RowSet rs = new CsvRowSet(csvfile);
		Long volume = 0L;
		Timeframe tf = candles.getTimeframe();
		while ( rs.next() ) {
			Object rawVol = rs.get(VOLUME);
			if ( rawVol != null && ! rawVol.equals("") ) {
				volume = Long.parseLong((String) rawVol); 
			} else {
				volume = 0L;				
			}
			candles.add(new Candle(
				tf.getInterval(parseDateTime(
						rs.get(DATE).toString(),
						rs.get(TIME).toString())),
				Double.parseDouble((String) rs.get(OPEN)),
				Double.parseDouble((String) rs.get(HIGH)),
				Double.parseDouble((String) rs.get(LOW)),
				Double.parseDouble((String) rs.get(CLOSE)),
				volume));
		}
		rs.close();
	}
	
	/**
	 * Создать сервис сохранения котировок в csv-файл.
	 * <p>
	 * Создает сервис сохранения котировок в csv-файл в формате FINAM с полями
	 * DATE (формат yyyyMMdd), TIME (HHmmss), OPEN, HIGH, LOW, CLOSE, VOL.
	 * <p>
	 * @param csvfile целевой файл
	 * @param candles набор свечей
	 * @return сервис сохранения
	 */
	public CandlesWriter
		createWriter(File csvfile, EditableCandleSeries candles)
	{
		return new Quote2CsvWriter(candles, csvfile);
	}
	
	/**
	 * Создать поток чтения тиков.
	 * <p>
	 * Формат файла: &lt;DATE&gt;,&lt;TIME&gt;,&lt;LAST&gt;,&lt;VOL&gt; 
	 * <p>
	 * @param filename - filename
	 * @return поток тиков
	 * @throws IOException - If error occured. 
	 */
	public Aqiterator<Tick>
		createTickReader(String filename) throws IOException
	{
		CsvReader csv;
		if ( FilenameUtils.getExtension(filename).equals("gz") ) {
			csv = new CsvReader(new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(filename)))));
		} else {
			csv = new CsvReader(filename);
		}
		csv.readHeaders();
		return new CsvTickReader(csv);
	}

}
