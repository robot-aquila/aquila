package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Контейнер индикатора полосы Боллинджера.
 * 
 * 2012-02-07
 * $Id: BollingerBands.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class BollingerBands {
	private final Value<Double> central;
	private final Value<Double> upper;
	private final Value<Double> lower;
	
	/**
	 * Конструктор
	 * @param central центральная линия
	 * @param upper верхняя полоса
	 * @param lower нижняя полоса
	 */
	public BollingerBands(Value<Double> central,
						  Value<Double> upper,
						  Value<Double> lower)
	{
		super();
		this.central = central;
		this.upper = upper;
		this.lower = lower;
	}
	
	/**
	 * Получить центральную линию
	 * @return
	 */
	public Value<Double> getCentralLine() {
		return central;
	}
	
	/**
	 * Получить верхнюю полосу
	 * @return
	 */
	public Value<Double> getUpperBand() {
		return upper;
	}
	
	/**
	 * Получить нижнюю полосу
	 * @return
	 */
	public Value<Double> getLowerBand() {
		return lower;
	}

}
