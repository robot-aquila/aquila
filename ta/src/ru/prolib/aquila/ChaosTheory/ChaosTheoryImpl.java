package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.SignalSource.*;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.math.*;

/**
 * Первая реализация стратегии торговли по Теории Хаоса.
 * Открываемся по фракталу. Добираем в случае получения сигналов с любого
 * измерения. Закрываемся по стоп-лоссу по пересечению цены закрытия с зубами
 * аллигатора. В случае, если стоп-лосс не сработал, то экстренное закрытие
 * позиции по цене закрытия.
 */
public class ChaosTheoryImpl extends TradingStrategy {
	/**
	 * Состояние открытия.
	 * 
	 * Условия входа: позиция в портфеле должна быть равна нулю.
	 * 
	 * Рассматривается каждый очередной фрактал за пределами зубов
	 * аллигатора. Для фракталов вверх: предыдущая заявка на покупку
	 * отменяется, если таковая была выставлена, выставляется новая заявка
	 * на покупку 1 лота по цене хая бара на котором возник фрактал + 1
	 * пункт. Для фракталов вниз то же самое, только наоборот.
	 * 
	 * Условие выхода: 
	 * 1. {@link #STATE_POSITION} - Позиция не равна нулю
	 * При выходе снимаются все активные заявки.
	 */
	public static final int STATE_OPENING = 1;
	
	/**
	 * Состояние удержания и добора позиции.
	 * 
	 * Условия входа: позиция в портфеле не ноль, иначе переход в
	 * состояние {@link #STATE_OPENING}.
	 * 
	 * На каждом этапе:
	 * 1. Проверяется пересечение цены закрытия и зубов аллигатора. Если в
	 * лонге пересечение сверху вниз или в шорте пересечение снизу вверх, то
	 * осуществляется переход на экстренное закрытие позы.
	 * 2. Рассчитывается и в случае необходимости заменяется текущий
	 * стоп-лосс, цена которого равна текущему значению на зубах аллигатора.
	 * 3. Рассматриваются сигналы включая новые фракталы в направлении
	 * открытой позиции. При наличии таких сигналов выставляется новая
	 * заявка по принципу как в состоянии открытия.
	 * 
	 * Условие выхода:
	 * 1. {@link #STATE_EMERG_CLOSE} - Цена пересекла зубы аллигатора.
	 * 2. {@link #STATE_OPENING} - Позиция равна нулю (закрылась по стопу)
	 * При выходе снимаются все активные заявки.
	 */
	public static final int STATE_POSITION = 2;
	
	/**
	 * Состояния экстренного закрытия позиции.
	 * 
	 * Условия входа: позиция в портфеле не ноль, иначе переход в
	 * состояние {@link #STATE_OPENING}.
	 * 
	 * Здесь должен быть алгоритм экстренного закрытия позиции.
	 * 
	 * Условие выхода:
	 * 1. {@link #STATE_OPENING} - Позиция равна нулю.
	 * При выходе снимаются все активные заявки.
	 */
	public static final int STATE_EMERG_CLOSE = 3;
	
	protected Alligator alligator;
	protected SignalSourceList signalGeneratorList;
	protected int state = STATE_OPENING;

	public ChaosTheoryImpl(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void prepare() throws Exception {
		MarketData data = locator.getMarketData();
		alligator = data.addAlligator("allig");
		signalGeneratorList = new SignalSourceList();
		
		// -= Новая версия =-
		Value<Double> ao = data.addAwesomeOscillator("ao");
		Value<Double> ac = data.addAccelerationOscillator("ao", "ac");
		WilliamsZones wz = data.addWilliamsZones("ao", "ac", "wz");
		
		SignalSourceBuilder ssb = new SignalSourceBuilder(driver.getAsset());
		signalGeneratorList.addSignalSource(ssb.fromFractal(
				data.getHigh(),
				data.getLow(),
				alligator.teeth));
		signalGeneratorList.addSignalSource(ssb.fromAwesomeOscillator(
				ao,
				data.getHigh(),
				data.getLow()));
		signalGeneratorList.addSignalSource(ssb.fromAccelOscillator(
				ac,
				data.getHigh(),
				data.getLow()));
		signalGeneratorList.addSignalSource(ssb.fromWilliamsZones(
				wz,
				data.getClose(),
				data.getHigh(),
				data.getLow()));
		state = driver.isNeutral() ? STATE_OPENING : STATE_POSITION;
	}
	
	@Override
	public void nextPass() throws Exception {
		enterState();
	}
	
	@Override
	public void clean() {
		
	}
	
	protected void changeState(int newState) throws Exception {
		driver.killAll();
		state = newState;
		enterState();
	}
	
	private void enterState() throws Exception {
		switch ( state ) {
			case STATE_OPENING:		stateOpening();		break;
			case STATE_POSITION:	statePosition();	break;
			case STATE_EMERG_CLOSE:	stateEmergClose();	break;
			default: throw new RuntimeException("Unknown state");
		}
	}
	
	protected void stateEmergClose() throws Exception {
		if ( driver.isNeutral() ) {
			changeState(STATE_OPENING);
			return;
		} else if ( driver.isLong() ) {
			closeNow("Emergency close (Long)");
		} else {
			closeNow("Emergency close (Short)");
		}
	}
	
	protected void statePosition() throws Exception {
		double step = driver.getAsset().getPriceStep();
		MarketData data = locator.getMarketData();
		
		double teethPrice = Math.round(alligator.teeth.get() / step) * step;
		SignalList signals = signalGeneratorList.getCurrentSignals();
		Signal signal = null;

		if ( driver.isNeutral() ) {
			changeState(STATE_OPENING);
		} else if ( driver.isLong() ) {
			// обработка лонга
			if ( data.getClose().get() <= alligator.teeth.get() ) {
				changeState(STATE_EMERG_CLOSE);
				return;
			}
			
			signal = signals.findOne(null, Signal.BUY);
			if ( signal != null ) {
				driver.addLong(signal.getPrice(), signal.getComment());
			}
			
			driver.closeLong(teethPrice, "Stop-loss (protect LONG)");
			
		} else {
			// обработка шорта
			if ( data.getClose().get() >= alligator.teeth.get() ) {
				changeState(STATE_EMERG_CLOSE);
				return;
			}
			
			signal = signals.findOne(null, Signal.SELL);
			if ( signal != null ) {
				driver.addShort(signal.getPrice(), signal.getComment());
			}
			
			driver.closeShort(teethPrice, "Stop-loss (protect Short)");
		}
	}
	
	protected void stateOpening() throws Exception {
		if ( ! driver.isNeutral() ) {
			changeState(STATE_POSITION);
		} else {
			SignalList signals = signalGeneratorList.getCurrentSignals();
			
			// Если есть фрактал на покупку 
			Signal signal = signals.findOne(1, Signal.BUY);
			if ( signal != null ) {
				driver.addLong(signal.getPrice(), signal.getComment());
			}
			
			// Если есть фрактал на продажу
			signal = signals.findOne(1, Signal.SELL);
			if ( signal != null ) {
				driver.addShort(signal.getPrice(), signal.getComment());
			}
		}
	}
	
	protected void closeNow(String comment) throws Exception {
		//portDrv.killAll();
		if ( driver.isLong() ) {
			driver.closeLongImmediately(comment);
		} else if ( driver.isShort() ) {
			driver.closeShortImmediately(comment);
		}
	}
	
}