package ru.prolib.aquila.ta;

import ru.prolib.aquila.core.data.Candle;



/**
 * Интерфейс конструктора шаблона бара.
 */
public interface BarPatternBuilder {
	
	/**
	 * Сформировать шаблон бара.
	 * 
	 * @param zeroPrice - нулевая цена соответствует нулевому сегменту
	 * @param segmentHeight - высота сегмента в единицах цены
	 * @param bar - экземпляр бара
	 * @return шаблон бара
	 * @throws BarPatternException
	 */
	public BarPattern
		buildBarPattern(double zeroPrice, double segmentHeight, Candle bar)
			throws BarPatternException;

}
