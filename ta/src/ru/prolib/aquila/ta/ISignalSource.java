package ru.prolib.aquila.ta;

/**
 * Интерфейс источника сигналов.
 * Источник сигналов реализует алгоритм анализа ситуации.
 * Результат анализа посредством транслятора преобразуется в сигнал на продажу
 * или покупку.
 */
public interface ISignalSource {
	
	/**
	 * Инициировать процедуру анализа ситуации.
	 * @param translator
	 * @throws ValueException 
	 */
	public void analyze(ISignalTranslator translator) throws ValueException;

}
