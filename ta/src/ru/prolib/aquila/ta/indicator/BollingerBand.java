package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Полоса Боллинджера.
 * 
 * 2012-02-07
 * $Id: BollingerBand.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class BollingerBand extends BaseIndicator2S<Double, Double> {
	private final double width;

	/**
	 * Конструктор.
	 * @param cen центральная линия канала Боллинджера
	 * @param std стандартное отклонение
	 * @param width ширина
	 */
	public BollingerBand(Value<Double> cen, Value<Double> std, double width) {
		super(cen, std);
		this.width = width;
	}
	
	/**
	 * Получить ширину канала
	 * @return
	 */
	public double getWidth() {
		return width;
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		Double c = src1.get();
		Double d = src2.get();
		if ( c != null && d != null ) {
			return width * d + c;
		}
		return null;
	}

}
