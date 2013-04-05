package ru.prolib.aquila.experiment;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.*;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.ds.*;
import ru.prolib.aquila.ta.math.*;

/**
 * Patterns. Experimental prototype.
 * 
 * 2012-02-13
 * $Id: Patterns.java 218 2012-05-20 12:15:33Z whirlwind $
 */
public class Patterns extends CommonStrategy {
	public static final int UNKNOWN = 0;
	public static final int UPTREND = 1;
	public static final int DNTREND = -1;
	
	/**
	 * Статистика по шаблону.
	 */
	public static class PatternStats {
		
		// Общее количество попаданий по паттерну
		public int count = 0;
		
		// Сколько раз цена достигала значения цены закрытия паттерна
		// плюс N уровней, где N - номер уровня от 1-8.
		// В этом массиве индекс определяет смещение уровня от 1.
		// То есть нулевой элемент указывает цена закрытия + 1, 7 элемент
		// указывает цена закрытия + 8 уровней
		public Integer plus[] = {0,0,0,0,0,0,0};
		
		// То же самое что для плюса, только в отношении падения цены
		// до определенного уровня
		public Integer minus[] = {0,0,0,0,0,0,0};
		
	}
	
	public static class PriceMonitor implements Observer {
		private static final Logger logger = LoggerFactory.getLogger(PriceMonitor.class);
		private final long pattern;
		private final MarketData data;
		private final PatternStats stats;
		private final double price,levelHeight;
		private final int startBar;
		
		/**
		 * Конструктор.
		 * 
		 * Запускает монитор цены.
		 * 
		 * @param data источник данных о ценах
		 * @param stats счетчик
		 * @param price цена считающаяся нулевым уровнем
		 * @param levelHeight высота уровня
		 * @param startBar индекс бара, на котором начинается мониторинг
		 */
		public PriceMonitor(long pattern, MarketData data, PatternStats stats,
							double price, double levelHeight,
							int startBar)
		{
			super();
			this.pattern = pattern;
			this.data = data;
			this.stats = stats;
			this.price = price;
			this.levelHeight = levelHeight;
			this.startBar = startBar;
			data.addObserver(this);
		}

		@Override
		public void update(Observable arg0, Object arg1) {
			try {
				if ( data.getLastBarIndex() >= startBar ) {
					countStats();
				}
				if ( data.getLastBarIndex() >= startBar + 8 ) {
					logger.debug("Stop count stats for pattern {}", pattern);
					logger.debug("Counted from {}",
							data.getBar(startBar).getTime());
					data.deleteObserver(this);
				}
			} catch ( Exception e ) {
				logger.error("Error count stats", e);
			}
		}
		
		private void countStats() throws Exception {
			Candle b = data.getBar();
			for ( int i = 0; i < stats.plus.length; i ++ ) {
				double plusPrice = (i + 1) * levelHeight + price;
				double minusPrice = price - ((i + 1) * levelHeight); 
				if ( b.getHigh() >= plusPrice ) {
					Object args[] = { i+1, price, plusPrice, pattern };
					logger.debug("Price level +{} reached from {} to {} for pattern {}", args);
					stats.plus[i] ++;
				}
				if ( b.getLow() <= minusPrice ) {
					Object args[] = { i+1, price, minusPrice, pattern };
					logger.debug("Price level -{} reached from {} to {} for pattern {}", args);
					stats.minus[i] ++;
				}
			}
		}
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Patterns.class);
	
	private MarketData data;
	private Value<Date> time;
	private Value<Double> stdev;
	private int range;
	private double stdevMin;
	private LinkedList<Swing> swings = new LinkedList<Swing>();
	private HashMap<Long, PatternStats> patterns = new HashMap<Long, PatternStats>();
	
	public Patterns(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void nextPass() throws Exception {
		checkSwing();
		int minBars = 5;
		if ( data.getLength() >= minBars ) {
			int startBar = data.getLength() - minBars;
			Candle bars[] = new Candle[minBars];
			for ( int i = 0; i < minBars; i ++ ) {
				bars[i] = data.getBar(startBar + i);
			}
			bars[minBars - 1] = data.getBar();
			Pattern8 pattern = new Pattern8(bars);
			long bits = pattern.getMatrix();
			Candle bar = data.getBar();
			logger.debug("At {} pattern {}: level height={}", new Object[]{
					data.getBar().getTime(), bits, pattern.getLevelHeight()});
			logger.debug("First bar at {} last bar at {}",
					bars[0].getTime(), bars[minBars - 1].getTime());
			logger.debug("HI: {}, LO: {}, CLOSE: {}", new Object[]{
					pattern.getMax(), pattern.getMin(), bar.getClose() });
			for ( int level = 7; level >= 0; level -- ) {
				logger.debug(Pattern8.matrixLevelToString(bits, level,
						"X", "O", " ") + " @" +
						pattern.getLevelMax(level) + "/" +
						pattern.getLevelMin(level));
			}
			logger.debug("");
			PatternStats stats = patterns.containsKey(bits) ?
					patterns.get(bits) : new PatternStats();
			stats.count ++;
			patterns.put(bits, stats);
			
			new PriceMonitor(bits, data, stats, data.getBar().getClose(),
					pattern.getLevelHeight(), data.getLastBarIndex() + 1);
		}
		super.nextPass();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepare() throws Exception {
		data = locator.getMarketData();
		stdevMin = 50;
		range = 10;
		time = data.getTime();
		data.addBollingerBands(MarketData.MEDIAN, range, 2.0d, "bb");
		stdev = data.getValue("bb.stdev");
		data.addSub(MarketData.HIGH, MarketData.LOW, "bar.height");
	}
	
	@Override
	public void inNeutralPosition() throws Exception {
		
	}
	
	@Override
	public void inShortPosition() throws Exception {

	}
	
	@Override
	public void inLongPosition() throws Exception {

	}

	@Override
	public void clean() {
		try {
			FileOutputStream fs = new FileOutputStream("patterns.csv");
			PrintStream out = new PrintStream(fs);
			Iterator it = patterns.entrySet().iterator();
			out.println("pattern,count,p1,p2,p3,p4,p5,p6,p7,m1,m2,m3,m4,m5,m6,m7");
			while ( it.hasNext() ) {
				Map.Entry pairs = (Map.Entry)it.next();
				PatternStats stats = (PatternStats) pairs.getValue();
				out.println(pairs.getKey().toString() + "," +
						stats.count + "," +
						new StrBuilder()
							.appendWithSeparators(stats.plus, ",")
							.toString() + "," +
						new StrBuilder()
							.appendWithSeparators(stats.minus, ","));
		    }
			out.close();
		} catch ( Exception e ) {
			error("Cannot dump pattern stat", e);
		}
	}
	
	private Swing checkSwing() throws Exception {
		if ( ! isStdevDowntrend() ) return null;
		Swing swing = tryDetectTrend();
		if ( swing == null ) return null;
		swings.add(swing);
		return swings.size() >= 2 ? swing : null;
	}
	
	private boolean isStdevDowntrend() {
		try {
			return stdev.get(-1) > stdev.get()
				&& stdev.get(-2) < stdev.get(-1)
				&& stdev.get() > stdevMin;
		} catch ( ValueException e ) {
			error("Failed check stdev", e);
			return false;
		}
	}
	
	/**
	 * Выполняет попытку определить направление тренда.
	 * 
	 * Работаем следующим образом: смотрит максимум range предыдущих баров
	 * для определения индекса минимума и максимума. Если максимум
	 * имеет более ранний индекс чем минимум, значит движение вниз. И наоборот
	 * - если сначала идет минимум затем максимум, значит было движение вверх и
	 * откат предположительно вниз.
	 * 
	 * @return {@link #UNKNOWN} - не удалось определить направление движения,
	 * {@link #UPTREND}, {@link #DNTREND}
	 * @throws SwingSamePivotIdException 
	 */
	private Swing tryDetectTrend() throws ValueException {
		Candle bar = data.getBar();
		Pivot max = new Pivot(PivotType.MAX, bar);
		Pivot min = new Pivot(PivotType.MIN, bar);
		for ( int i = 1; i < range; i ++ ) {
			bar = data.getBar(-i);
			if ( bar.getHigh() > max.getHigh() ) {
				max = new Pivot(PivotType.MAX, bar);
			}
			if ( bar.getLow() < min.getLow() ) {
				min = new Pivot(PivotType.MIN, bar);
			}
		}
		Swing swing = new Swing(max, min);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.debug("At {}", time.get());
		logger.debug("{}trend slowed", swing.isDowntrend() ? "Down" : "Up");
		logger.debug("From {}", swing.pivot1());
		logger.debug("To   {}", swing.pivot2());
		logger.debug("Distance {} bars", swing.distance());
		logger.debug("Impulse {} pts.", swing.impulse());
		return swing;
	}
	
	private void error(String msg, Exception e) {
		msg = msg + ": " + e.getMessage();
		if ( logger.isDebugEnabled() ) {
			logger.error(msg, e);
		} else {
			logger.error(msg);
		}
	}

}
