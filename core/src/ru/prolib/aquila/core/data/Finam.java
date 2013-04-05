package ru.prolib.aquila.core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ru.prolib.aquila.core.data.row.CsvRowSet;
import ru.prolib.aquila.core.data.row.RowSet;

/**
 * Загрузчик данных формата ФИНАМ.
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
	private static final String TIMEFORMAT = "yyyyMMdd HHmmss";
	
	/**
	 * Загрузить свечи из CSV-файла.
	 * <p>
	 * @param csvfile файл с котировками в формате ФИНАМ CSV
	 * @param candles целевой набор свечей
	 * @throws FileNotFoundException
	 * @throws ParseException  
	 */
	public void loadCandles(File csvfile, EditableSeries<Candle> candles)
			throws FileNotFoundException, ParseException
	{
		SimpleDateFormat df = new SimpleDateFormat(TIMEFORMAT);
		RowSet rs = new CsvRowSet(csvfile);
		Long volume = 0L;
		while ( rs.next() ) {
			Object rawVol = rs.get(VOLUME);
			if ( rawVol != null && ! rawVol.equals("") ) {
				volume = Long.parseLong((String) rawVol); 
			} else {
				volume = 0L;				
			}
			candles.add(new Candle(
				df.parse(rs.get(DATE) + " " + rs.get(TIME)),
				Double.parseDouble((String) rs.get(OPEN)),
				Double.parseDouble((String) rs.get(HIGH)),
				Double.parseDouble((String) rs.get(LOW)),
				Double.parseDouble((String) rs.get(CLOSE)),
				volume));
		}
		rs.close();
	}

}
