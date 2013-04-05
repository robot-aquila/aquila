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
import ru.prolib.aquila.tasks.CloseImmediately;
import ru.prolib.aquila.tasks.LimitBuy;
import ru.prolib.aquila.tasks.LimitSell;
import ru.prolib.aquila.tasks.OpenLongWithTrailingStopLoss;
import ru.prolib.aquila.tasks.StopLossLong;
import ru.prolib.aquila.tasks.StopLossShort;
import ru.prolib.aquila.tasks.TargetedLongWithFixedStopLoss;
import ru.prolib.aquila.tasks.TargetedShortWithFixedStopLoss;
import ru.prolib.aquila.tasks.Task;

/**
 * Ping-Pong. Experimental prototype.
 * 
 * 2012-02-07
 * $Id: PingPong.java 218 2012-05-20 12:15:33Z whirlwind $
 */
public class PingPong extends CommonStrategy {
	public static final int UNKNOWN = 0;
	public static final int UPTREND = 1;
	public static final int DNTREND = -1;
	
	private static final Logger logger = LoggerFactory.getLogger(PingPong.class);
	
	private MarketData data;
	private Value<Date> time;
	private Value<Double> stdev,atr;
	private int range;
	private double stdevMin;
	private Candle lastSwingBar;
	private LinkedList<Swing> swings = new LinkedList<Swing>();
	private Task task;
	private double price,target,stopPrice;
	
	public PingPong(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void nextPass() throws Exception {
		checkSwing();
		super.nextPass();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepare() throws Exception {
		data = locator.getMarketData();
		stdevMin = 30;
		range = 10;
		time = data.getTime();
		data.addBollingerBands(MarketData.MEDIAN, range, 2.0d, "bb");
		stdev = data.getValue("bb.stdev");
		data.addSub(MarketData.HIGH, MarketData.LOW, "bar.height");
		atr = data.addSma("bar.height", range, "atr");
	}
	
	@Override
	public void inNeutralPosition() throws Exception {
		if ( swings.size() < 2 ) {
			return;
		}
		Swing swing = swings.getLast();
		Candle now = data.getBar();
		if ( ! lastSwingBar.equals(now) ) {
			return;
		}
		// Если свечка слишком большая, это подозрительно.
		if ( now.getHeight() > atr.get() * 1.2 ) {
			logger.debug("Strange volatile bar. Skip signal.");
			return;
		}
		
		
		// Если дистанция свинга >= 90% от общего периода и последний пик
		// соответствует текущему бару, это может свидетельствовать о
		// стабилизации STDEV на устойчивом тренде. Открываем в том же
		// направлении.
		if ( swing.distance() >= (double)range * 0.9 ) { 
			if ( swing.pivot2().equals(now) ) {
				logger.debug("Start task: open {} with trailing stop",
					swing.isUptrend() ? "LONG" : "SHORT");
			} else {
				logger.debug("I am not assured that is steady trend");
			}
			return;
		}
		
		// Если текущий бар это крайний пайвот, то это подозрительно.
		// Это всегда может оказаться продолжением тренда.
		//if ( swing.pivot2().equals(now) ) {
		//	logger.debug("It seems like trend continues.");
		//	return;
		//}
		
		//if ( swing.impulse() < 300 ) {
		//	if ( swing.impulse() < 200 ) {
		//		return;
		//	}
			// низкий профит
			startTargetedSequence();
			return;
		//}
		
		// TODO: это полная жопа
		// Импульс тренда норм. Открываем против тренда с трейлинговым стопом
		//if ( swing.isDowntrend() ) {
		//	price = now.getCandleCenterOrCloseIfLower();
		//	stopPrice = price - (atr.get() > 150 ? 150 : atr.get());
		//	task = new OpenLongWithTrailingStopLoss(locator)
		//		.openNotLongerThan(3)
		//		.openPrice(price)
		//		.stopPrice(stopPrice)
		//		.stopSpread(40);
		//	task.start();
		//}
	}

	/**
	 * Открытие позиции с четким определением цели.
	 * Используется для свингов с малым импульсом.
	 * @throws Exception
	 */
	private void startTargetedSequence() throws Exception {
		logger.debug("Start targeted sequence");
		Swing swing = swings.getLast();
		Candle now = data.getBar();

		target = swing.impulse() * 0.2;
		if ( swing.isDowntrend() ) {
			target = now.getCandleCenter() + target;
			price = now.getCandleCenterOrCloseIfLower();
			stopPrice = price - (atr.get() > 150 ? 150 : atr.get());
			task = new TargetedLongWithFixedStopLoss(locator)
				.openNotLongerThan(3)
				.openPrice(price)
				.closeNotLongerThan(5)
				.closePrice(target)
				.stopPrice(stopPrice);
		} else {
			target = now.getCandleCenter() - target;
			price = now.getCandleCenterOrCloseIfHigher();
			stopPrice = price + (atr.get() > 150 ? 150 : atr.get());
			task = new TargetedShortWithFixedStopLoss(locator)
				.openNotLongerThan(3)
				.openPrice(price)
				.closeNotLongerThan(5)
				.closePrice(target)
				.stopPrice(stopPrice);
		}
		task.start();
	}
	
	@Override
	public void inShortPosition() throws Exception {
		double warnPrice = price + (atr.get() * 2);
		if ( task.cancelled() || getAsset().getPrice() > warnPrice ) {
			logger.debug("Start emergency close short");
			task.cancel();
			new CloseImmediately(locator)
				.comment("Close short immediately")
				.start();
		}
	}
	
	@Override
	public void inLongPosition() throws Exception {
		double warnPrice = price - (atr.get() * 2);
		if ( task.cancelled() || getAsset().getPrice() < warnPrice ) {
			logger.debug("Start emergency close long");
			task.cancel();
			new CloseImmediately(locator)
				.comment("Close long immediately")
				.start();
		}
	}

	@Override
	public void clean() {

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
	 * Частные случаи:
	 * 
	 *  H
	 *   X       C  - это уже uptrend
	 *    X    XX
	 *     X XX
	 *      L
	 *      
	 *      H
	 *     X X
	 *    X   X
	 *   X     C    - это уже downtrend
	 *  X
	 *  L
	 *  
	 * Если до ближней точки более 40% от всего рассмотренного
	 * периода или потенциал движения между текущей и последней точкой
	 * более 20% от потенциала определенного свинга, строим свинг от последней
	 * до текущей точки. 
	 *  
	 * @return {@link #UNKNOWN} - не удалось определить направление движения,
	 * {@link #UPTREND}, {@link #DNTREND}
	 * @throws SwingSamePivotIdException 
	 */
	private Swing tryDetectTrend() throws ValueException {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.debug("At {}", time.get());

		Candle cur = data.getBar();
		Pivot max = new Pivot(PivotType.MAX, cur);
		Pivot min = new Pivot(PivotType.MIN, cur);
		for ( int i = 1; i < range; i ++ ) {
			Candle bar = data.getBar(-i);
			if ( bar.getHigh() > max.getHigh() ) {
				max = new Pivot(PivotType.MAX, bar);
			}
			if ( bar.getLow() < min.getLow() ) {
				min = new Pivot(PivotType.MIN, bar);
			}
		}
		
		Swing swing = new Swing(max, min);
		Pivot lastPivot = swing.pivot2();
		if ( ! lastPivot.equals(cur) ) {
			Pivot curPivot = new Pivot(lastPivot.isMax() ?
					PivotType.MIN : PivotType.MAX, cur);
			Swing swing2 = new Swing(lastPivot, curPivot);
			if ( swing2.impulse() >= swing.impulse() * 0.20
			  && swing2.distance() >= range * 0.40 )
			{
				logger.debug("Period from current bar to last pivot seems like a new swing");
				logger.debug("First swing:");
				logger.debug("From {}", swing.pivot1());
				logger.debug("To   {}", swing.pivot2());
				logger.debug("Distance {} bars", swing.distance());
				logger.debug("Impulse {} pts.", swing.impulse());
				logger.debug("Swing will be replaced");
				swing = swing2;
			}
		}
		lastSwingBar = cur;
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
