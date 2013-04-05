package ru.prolib.aquila.ta;

import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Интерфейс конструктора шаблона последовательности баров.
 */
public interface BarSequencePatternBuilder {
	
	/**
	 * Сформировать шаблон последовательности баров.
	 * 
	 * @param data - источник данных
	 * @param startBar - индекс начального бара
	 * @param numBars - количество баров
	 * @return шаблон последовательности баров
	 * @throws BarPatternException
	 */
	public BarSequencePattern
		buildBarSequencePattern(MarketData data, int startBar, int numBars)
			throws BarPatternException;

}
