package ru.prolib.aquila.experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Стратегия Waterfall.
 * 
 * Рассматриваем три бара подряд.
 * 
 * Открытие позы:
 * Если лоу и хай каждого последущего бара ниже соответственно лоу и хая
 * предыдущего, то открываем шорт по лоу последнего + зазор.
 * 
 * Если хай и лоу каждого последующего бара выше соответственно хая и лоу
 * предыдущего, то открываем лонг по хаю последнего + зазор. 
 * 
 * В шорте: 
 * Немедленное закрытие позы, если хай последнего бара выше хая предпоследнего.
 * Иначе выставляем стоп-лосс по хаю последнего бара.
 * 
 * В лонге:
 * Немедленное закрытие позы, если лоу последнего бара ниже лоу предпоследнего.
 * Иначе стоп-лосс по лоу последнего бара.
 *
 * Возможные фильтры:
 * Для шорта: разница между хаем первого бара последовательности и лоу
 * последнего что бы не меньше X пунктов. Это позволяет примерно оценить
 * потенциал движения.
 */
public class Waterfall extends CommonStrategy {
	/**
	 * Дельта "наклона" водопада в пунктах, по достижении которого движение
	 * рассматривается как потенциально прибыльное. Для лонга это разница
	 * между хаем последнего и лоу первого баров. Для шорта это разница между
	 * хаем первого и лоу последнего баров.
	 */
	public static final String PROP_POTENTIAL = "Waterfall.PotentialPoints";
	
	/**
	 * Отступ в шагах цены для заявок.
	 */
	public static final String PROP_PRICE_INDENT = "Waterfall.PriceIndent";
	
	private static final Logger logger = LoggerFactory.getLogger(Waterfall.class);
	private MarketData data;
	private double potential,indent;

	public Waterfall(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}

	@Override
	public void inLongPosition() throws Exception {
		Candle b1 = data.getBar(-1);
		Candle b2 = data.getBar();
		if ( b2.getLow() < b1.getLow() ) {
			driver.killAll();
			driver.closeLongImmediately("Spring broken");
		} else {
			driver.closeLong(b1.getLow() - indent, "Catch spring");
		}
	}

	@Override
	public void inShortPosition() throws Exception {
		Candle b1 = data.getBar(-1);
		Candle b2 = data.getBar();
		if ( b2.getHigh() > b1.getHigh() ) {
			driver.killAll();
			driver.closeShortImmediately("Waterfall broken");
		} else {
			driver.closeShort(b1.getHigh() + indent, "Catch waterfall");
		}
	}

	@Override
	public void inNeutralPosition() throws Exception {
		if ( data.getLength() < 4 ) {
			return;
		}
		driver.killAll();
		
		Candle b1 = data.getBar(-2);
		Candle b2 = data.getBar(-1);
		Candle b3 = data.getBar();
		double b1h = height(b1);
		double b2h = height(b2);
		double b3h = height(b3);
		// События развиваются слева-направо
		// -> более ранние бары должны быть менее выражены
		if ( b1h / b2h > 0.95d || b2h / b3h > 0.95d ) {
			return;
		}
		
		if ( b1.getHigh() > b2.getHigh() && b2.getHigh() > b3.getHigh()
     		&& b1.getLow() > b2.getLow() && b2.getLow() > b3.getLow()
     		&& b1.getHigh() - b3.getLow() >= potential )
		{
			driver.addShort(b3.getLow() - indent, "Waterfall");
			
		/*
		} else if ( b1.getHigh() < b2.getHigh() && b2.getHigh() < b3.getHigh()
			&& b1.getLow() < b2.getLow() && b2.getLow() < b3.getLow()
			&& b3.getHigh() - b1.getLow() >= potential )
		{
			driver.addLong(b3.getHigh() + indent, "Spring");
		*/
			
		}
	}

	@Override
	public void prepare() throws Exception {
		data = locator.getMarketData();
		Props props = locator.getProperties();
		potential = props.getDouble(PROP_POTENTIAL, 0d);
		indent = getAsset().getPriceStep() * props.getInt(PROP_PRICE_INDENT, 1);
		logger.info("With potential={}, indent={}", potential, indent);
	}

	@Override
	public void clean() {

	}
	
	private double height(Candle b) {
		return b.getHigh() - b.getLow();
	}

}
