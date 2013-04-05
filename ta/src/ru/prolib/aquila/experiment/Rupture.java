package ru.prolib.aquila.experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.math.Max;
import ru.prolib.aquila.ta.math.Min;

/**
 * 2012-01-30
 * $Id: Rupture.java 199 2012-02-07 21:10:33Z whirlwind $
 * 
 * Пробойная стратегия.
 * 
 * В нейтральной позиции:
 * Открытие лонга при пробитии F(ull)-периодного максимума.
 * Открытие шорта при пробитии F(ull)-периодного минимума.
 * 
 * В лонге:
 * Закрытие лонга при пробитии H(alf)-периодного минимума.
 * 
 * В шорте:
 * Закрытие шорта при пробитии H(alf)-периодного максимума.
 * 
 * Примечания:
 * При экстренном закрытии остается висеть защитный стоп. Иногда это приводит к
 * открытию противоположной позиции. В общем то логично его оставить.
 * 
 * При переходе в состояние в позиции, открывающий ордер не снимается. Это
 * может привести к ситуации, когда открывающий не исполнен до конца, но
 * защитный выставляется и при чем по текущему количеству в портфеле. Надо ли
 * снимать открывающий в данном случае, пока вопрос открытый.
 * 
 * Идеи:
 * При малом значение H(alf) (или можно ввести новый параметр для целей защиты),
 * можно попытаться определить потенциально убыточные сделки до того момента,
 * как их еще можно закрыть с безубытком. Если в течение нескольких последних
 * периодов цена топчется на месте, то можно попробовать выставить ордер по
 * закупочной цене.
 * 
 * Правда придется погемороиться с тем, как это реализовать без снятия стопа
 * или как обойтись одном стопом. И, наверное это будет иметь смысл только во
 * флете.
 * 
 */
public class Rupture extends CommonStrategy {
	private static final Logger logger = LoggerFactory.getLogger(Rupture.class);
	
	/**
	 * Идентификатор параметра конфигурации, определяющего величину минимального
	 * спреда между максимумом и минимумом для открытия позиции в пунктах. Если
	 * спреды меньше указанного значения, то текущие заявки снимаются и а
	 * новые заявки на открытие позиции не выставляются. Дефолтное значение 0.
	 */
	public static final String PROP_FULL_SPREAD = "Rupture.FullSpread";
	public static final String PROP_HALF_SPREAD = "Rupture.HalfSpread";
	
	/**
	 * Идентификатор параметра конфигурации, определяющего количества периодов
	 * для расчета минимума и максимума. Дефолтное значение 34 и 17.
	 */
	public static final String PROP_FULL_PERIOD = "Rupture.FullPeriod";
	public static final String PROP_HALF_PERIOD = "Rupture.HalfPeriod";
	
	/**
	 * Идентификатор параметра конфигурации, определяющего величину отступа
	 * от расчетной цены заявки на открытие позиции в шагах цены. Значение
	 * по умолчанию 1. 
	 */
	public static final String PROP_PRICE_INDENT = "Rupture.PriceIndent";
	
	private Max maxFull,maxHalf;
	private Min minFull,minHalf;
	private MarketData data;
	
	private double indent;
	private double fullSpread = 0, halfSpread = 0;
	private int fullPeriod = 34, halfPeriod = 17;

	public Rupture(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void prepare() throws Exception {
		Props props = locator.getProperties();
		fullPeriod = props.getInt(PROP_FULL_PERIOD, 34);
		halfPeriod = props.getInt(PROP_HALF_PERIOD, 17);
		fullSpread = props.getDouble(PROP_FULL_SPREAD, 0d);
		halfSpread = props.getDouble(PROP_HALF_SPREAD, 0d);
		indent = getAsset().getPriceStep() * props.getInt(PROP_PRICE_INDENT, 1);
		data = locator.getMarketData();
		maxFull = data.addMax(MarketData.HIGH, fullPeriod, "max(F)");
		maxHalf = data.addMax(MarketData.HIGH, halfPeriod, "max(H)");
		minFull = data.addMin(MarketData.LOW, fullPeriod, "min(F)");
		minHalf = data.addMin(MarketData.LOW, halfPeriod, "min(H)");
		
		logger.info("$Id: Rupture.java 199 2012-02-07 21:10:33Z whirlwind $");
		logger.info("Configured with:");
		logger.info("Full period={}, half period={}", fullPeriod, halfPeriod);
		logger.info("Full spread={}, half spread={}", fullSpread, halfSpread);
		logger.info("Price indent {} pts.", indent);
	}
	
	@Override
	public void inNeutralPosition() throws Exception {
		logger.info("max(F)={}, min(F)={}", maxFull.get(), minFull.get());
		double curSpreadF = maxFull.get() - minFull.get();
		double curSpreadH = maxHalf.get() - minHalf.get();
		if ( curSpreadF > fullSpread && curSpreadH > halfSpread ) {
			driver.addLong(maxFull.get() + indent, "Rupture max(F)");
			driver.addShort(minFull.get() - indent, "Rupture min(F)");
		} else {
			logger.info("Spread too small: full={}, half={}",
					curSpreadF, curSpreadH);
			driver.killAll();
		}
	}

	@Override
	public void inLongPosition() throws Exception {
		logger.info("min(H)={}", minHalf.get());
		if ( driver.getBuy() != null ) {
			driver.killBuy();
		}
		
		// <= важно, так как последний бар может определять текущий минимум
		if ( data.getLow().get().compareTo(minHalf.get()) <= 0 ) {
			driver.closeLongImmediately("By stop-line");
		} else {
			driver.closeLong(minHalf.get(), "Rupture min(H)");
		}
	}

	@Override
	public void inShortPosition() throws Exception {
		logger.info("max(H)={}", maxHalf.get());
		if ( driver.getSell() != null ) {
			driver.killSell();
		}
		
		// >= важно! так как последний бар может определять текущий максимум
		if ( data.getHigh().get().compareTo(maxHalf.get()) >= 0 ) {
			driver.closeShortImmediately("By stop-line");
		} else {
			driver.closeShort(maxHalf.get(), "Rupture max(H)");
		}
	}

	@Override
	public void clean() {
		
	}

}
