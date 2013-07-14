package ru.prolib.aquila.quik.assembler.cache.dde;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Интерфейс шлюза таблицы DDE.
 * <p>
 * Шлюз знает все о требованиях, предъявляемым к импортируемой таблице и
 * абстрагирует обработчик таблицы от деталей реализации обработки данных
 * таблицы.
 */
public interface TableGateway {
	
	/**
	 * Получить список обязательных заголовков таблицы.
	 * <p>
	 * @return список заголовков
	 */
	public String[] getRequiredHeaders();
	
	/**
	 * Проверить необходимость обработки ряда.
	 * <p>
	 * Данный метод позволяет реализовывать специфическую проверку ряда на
	 * необходимость обработки. С помощью этого метода можно обеспечить
	 * фильтрацию таблицы на уровне рядов.
	 * <p>
	 * @param row ряд
	 * @return true - ряд должен быть обработан, false - не нужно обрабатывать
	 * @throws DDEException ошибка доступа к элементу ряда
	 */
	public boolean shouldProcess(Row row) throws DDEException;
	
	/**
	 * Обработать ряд таблицы.
	 * <p> 
	 * @param row ряд
	 * @throws DDEException ошибка обработки ряда
	 */
	public void process(Row row) throws DDEException;

}
