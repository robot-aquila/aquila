package ru.prolib.aquila.ta.SignalSource;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.SignalSource.PatternMatcher.*;

/**
 * Конструктор типовых источников сигналов.
 */
public class SignalSourceBuilder {
	private final Asset asset;
	
	public SignalSourceBuilder(Asset asset) {
		super();
		this.asset = asset;
	}

	/**
	 * Сконструировать источник сигналов по осциллятору AO.
	 * Сигналы формируются в соответствии с Теорией Хаоса Билла Вильямса
	 * в той части, что касается индикатора Awesome Oscillator.
	 * 
	 * @param oscillator значения осциллятора
	 * @param high максимальная цена
	 * @param low минимальная цена
	 * @return источник сигналов
	 */
	public ISignalSource fromAwesomeOscillator(Value<Double> oscillator,
			Value<Double> high, Value<Double> low)
	{
		IPriceCalculator bpc = new OffsetPriceCalculator(high, asset,  1);
		IPriceCalculator spc = new OffsetPriceCalculator(low,  asset, -1);
		return new CompositeSignalSource(new ISignalSource[]{
			new PatternSignalSource(Signal.BUY,
					new AO_BuySaucer(oscillator), bpc, "AO#BS"),
				
			new PatternSignalSource(Signal.BUY,
					new AO_BuyZeroCross(oscillator), bpc, "AO#BZC"),
				
			new PatternSignalSource(Signal.SELL,
					new AO_SellSaucer(oscillator), spc, "AO#SS"),
				
			new PatternSignalSource(Signal.SELL,
					new AO_SellZeroCross(oscillator), spc, "AO#SZC"),
		});
	}
	
	public ISignalSource fromFractal(Value<Double> high,
			Value<Double> low, Value<Double> filter)
	{
		IPriceCalculator bpc = new FractalPriceCalculator(high, asset,  1, 5);
		IPriceCalculator spc = new FractalPriceCalculator(low,  asset, -1, 5);
		return new CompositeSignalSource(new ISignalSource[]{
			new PatternSignalSource(Signal.BUY,
					new Fractal_BuyFiltered(high, filter), bpc, "F#B"),
				
			new PatternSignalSource(Signal.SELL,
					new Fractal_SellFiltered(low, filter), spc, "F#S")
				
		});
	}
	
	public ISignalSource fromAccelOscillator(Value<Double> oscillator,
			Value<Double> high, Value<Double> low)
	{
		IPriceCalculator bpc = new OffsetPriceCalculator(high, asset,  1);
		IPriceCalculator spc = new OffsetPriceCalculator(low,  asset, -1);
		return new CompositeSignalSource(new ISignalSource[]{
			new PatternSignalSource(Signal.BUY,
					new AC_BuyAboveZero(oscillator), bpc, "AC#BAZ"),
					
			new PatternSignalSource(Signal.BUY,
					new AC_BuyBelowZero(oscillator), bpc, "AC#BBZ"),
					
			new PatternSignalSource(Signal.SELL,
					new AC_SellAboveZero(oscillator), spc, "AC#SAZ"),
					
			new PatternSignalSource(Signal.SELL,
					new AC_SellBelowZero(oscillator), spc, "AC#SBZ"),
			
		});
	}
	
	public ISignalSource fromWilliamsZones(Value<Integer> wz,
			Value<Double> close, Value<Double> high, Value<Double> low)
	{
		IPriceCalculator bpc = new OffsetPriceCalculator(high, asset,  1);
		IPriceCalculator spc = new OffsetPriceCalculator(low,  asset, -1);
		return new CompositeSignalSource(new ISignalSource[]{
			new PatternSignalSource(Signal.BUY,
					new WilliamsZones_Buy(wz, close), bpc, "WZ#BG"),
					
			new PatternSignalSource(Signal.SELL,
					new WilliamsZones_Sell(wz, close), spc, "WZ#SR"),
		});
	}

}
