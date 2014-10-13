package ru.prolib.aquila.core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.data.finam.Quote2CsvWriter;
import ru.prolib.aquila.core.data.row.*;

/**
 * Фасад загрузчика данных формата ФИНАМ.
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
	private static final String TIMEFORMAT = "yyyyMMdd HHmmss";
	public static SimpleDateFormat df = new SimpleDateFormat(TIMEFORMAT);
	
	/**
	 * Загрузить свечи из CSV-файла.
	 * <p>
	 * @param csvfile файл с котировками в формате ФИНАМ CSV
	 * @param candles целевой набор свечей
	 * @throws FileNotFoundException
	 * @throws ParseException  
	 * @throws ValueException 
	 * @throws NumberFormatException 
	 * @throws RowSetException
	 */
	public void loadCandles(File csvfile, EditableCandleSeries candles)
			throws FileNotFoundException, ParseException, NumberFormatException,
				ValueException
	{
		//SimpleDateFormat df = new SimpleDateFormat(TIMEFORMAT);
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
			Date time = df.parse(rs.get(DATE) + " " + rs.get(TIME));
			candles.add(new Candle(
				tf.getInterval(new DateTime(time)),
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

}
