package ru.prolib.aquila.ta.SignalSource;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Рассчет цены смещением текущего значения.
 * Используется для рассчета цены сигнала по формулам HI+PIPS и LO-PIPS.
 */
public class OffsetPriceCalculator implements IPriceCalculator {
	protected final Value<Double> src;
	private final double mul;
	private final Asset asset;
	
	public OffsetPriceCalculator(Value<Double> src, Asset asset, double mul) {
		super();
		this.src = src;
		this.asset = asset;
		this.mul = mul;
	}
	
	public Value<Double> getSourceValue() {
		return src;
	}
	
	public double getMul() {
		return mul;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	@Override
	public double getPrice() {
		try {
			return getValue() + (mul * asset.getPriceStep());
		} catch ( Exception e ) {
			return 0;
		}
	}
	
	protected double getValue() throws ValueException {
		return src.get();
	}

}