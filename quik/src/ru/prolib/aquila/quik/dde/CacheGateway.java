package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Интерфейс шлюза к кэшу DDE-таблицы.
 * <p>
 * Шлюз знает все о требованиях, предъявляемым к импортируемой таблице и
 * абстрагирует обработчик таблицы от деталей реализации кэша.
 */
public interface CacheGateway {
	
	/**
	 * Получить список обязательных заголовков таблицы.
	 * <p>
	 * @return список заголовков
	 */
	public String[] getRequiredHeaders();
	
	/**
	 * Кэшировать ряд таблицы.
	 * <p> 
	 * @param row ряд
	 * @throws DDEException ошибка конвертирования ряда
	 */
	public void toCache(Row row) throws DDEException;
	
	/**
	 * Генерировать событие об обновлении кэша.
	 */
	public void fireUpdateCache();
	
	/**
	 * Очистить кэш.
	 */
	public void clearCache();

}
