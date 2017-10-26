package ru.prolib.aquila.core.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.timeframe.*;

/**
 * Zoned timeframe interface.
 */
public interface ZTFrame {
	public static final ZoneId MSK = ZoneId.of("Europe/Moscow");
	
	/**
	 * 1 minute timeframe in UTC time zone.
	 */
	public static final ZTFrame M1 = new ZTFMinutes(1);
	public static final ZTFrame M1UTC = M1;

	/**
	 * 1 minite timeframe in MSK time zone.
	 */
	public static final ZTFrame M1MSK = new ZTFMinutes(1, MSK);
	
	/**
	 * 2 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M2 = new ZTFMinutes(2);
	public static final ZTFrame M2UTC = M2;
	
	/**
	 * 2 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M2MSK = new ZTFMinutes(2, MSK);
	
	/**
	 * 3 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M3 = new ZTFMinutes(3);
	public static final ZTFrame M3UTC = M3;
	
	/**
	 * 3 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M3MSK = new ZTFMinutes(3, MSK);
	
	/**
	 * 5 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M5 = new ZTFMinutes(5);
	public static final ZTFrame M5UTC = M5;
	
	/**
	 * 5 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M5MSK = new ZTFMinutes(5, MSK);
	
	/**
	 * 10 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M10 = new ZTFMinutes(10);
	public static final ZTFrame M10UTC = M10;
	
	/**
	 * 10 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M10MSK = new ZTFMinutes(10, MSK);
	
	/**
	 * 15 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M15 = new ZTFMinutes(15);
	public static final ZTFrame M15UTC = M15;
	
	/**
	 * 15 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M15MSK = new ZTFMinutes(15, MSK);
	
	/**
	 * 30 minutes timeframe in UTC time zone.
	 */
	public static final ZTFrame M30 = new ZTFMinutes(30);
	public static final ZTFrame M30UTC = M30;
	
	/**
	 * 30 minutes timeframe in MSK time zone.
	 */
	public static final ZTFrame M30MSK = new ZTFMinutes(30, MSK);
	
	/**
	 * 1 hour timeframe in UTC time zone.
	 */
	public static final ZTFrame H1 = new ZTFHours(1);
	public static final ZTFrame H1UTC = H1;
	
	/**
	 * 1 hour timeframe in MSK time zone.
	 */
	public static final ZTFrame H1MSK = new ZTFHours(1, MSK);
	
	/**
	 * 1 day timeframe in UTC time zone.
	 */
	public static final ZTFrame D1 = new ZTFDays(1);
	public static final ZTFrame D1UTC = D1;
	
	/**
	 * 1 day timeframe in MSK time zone.
	 */
	public static final ZTFrame D1MSK = new ZTFDays(1, MSK);
	
	/**
	 * Check that the timeframe is an intraday timeframe.
	 * <p>
	 * @return true if intraday
	 */
	boolean isIntraday();
	
	/**
	 * Get time unit of this timeframe.
	 * <p>
	 * @return time unit
	 */
	ChronoUnit getUnit();
	
	/**
	 * Get length of period in units.
	 * <p>
	 * @return length
	 */
	int getLength();
	
	/**
	 * Получить интервал для временной метки.
	 * <p>
	 * @param time временная метка
	 * @return интервал
	 */
	Interval getInterval(Instant time);
	
	/**
	 * Get Zone ID.
	 * <p>
	 * Zone ID is important to make intervals respect specified time zone.
	 * <p>
	 * @return zone ID
	 */
	ZoneId getZoneID();
	
	/**
	 * Check that the timeframe is compatible with another one.
	 * <p>
	 * Compatible timeframes must be in same time zone.
	 * <p>
	 * @param tframe - timeframe to compare to
	 * @return true if timeframes are compatible with each other
	 */
	boolean isCompatibleWith(ZTFrame tframe);

	/**
	 * Convert this object to time frame without a zone.
	 * <p>
	 * @return time frame without a zone
	 */
	TFrame toTFrame();
	
}
