package ru.prolib.aquila.probe;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.data.*;

/**
 * Интерфейс фасада доступа к курсам валют.
 */
public interface CurrencyExchangeRate {
	
	/**
	 * Получить значение обменного курса.
	 * <p>
	 * @param baseCurrCode код базовой валюты (например EUR в EUR/USD=1.25)
	 * @param quoteCurrCode код валюты котирования (RUB в USD/RUB=32.40)
	 * @param at время актуальности курса
	 * @return значение обменного курса на указанное время
	 * @throws ValueException
	 */
	public Tick getRate(String baseCurrCode, String quoteCurrCode, DateTime at)
		throws ValueException;

}
