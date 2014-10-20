package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * Интерфейс хранилища тиковых данных.
 */
public interface TickDataStorage {

	/**
	 * Получить ридер тиков по инструменту начиная с указанного времени.
	 * <p>
	 * Отсутствие данных по указанному инструменту не приводит к выбросу
	 * исключения. В таких случаях возвращается пустой ридер (не содержащий ни
	 * одного тика). Запрос данных с указанием времени, находящимся за границами
	 * имеющихся данных так же не приводит к ошибке. Обе описанные ситуации
	 * рассматриваются как естественное отсутствие данных. Например, по причине
	 * периода отсутствия торгов или невысокой ликвидности инструмента.  
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param from время начала данных
	 * @return итератор тиковых данных
	 * @throws DataException ошибка доступа к данным. 
	 */
	public Aqiterator<Tick> getTicks(SecurityDescriptor descr, DateTime from)
			throws DataException;

}
