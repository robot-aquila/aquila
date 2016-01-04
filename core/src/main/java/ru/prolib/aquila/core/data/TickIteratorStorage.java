package ru.prolib.aquila.core.data;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

/**
 * Интерфейс хранилища тиковых данных.
 */
public interface TickIteratorStorage {

	/**
	 * Получить ридер тиков.
	 * <p>
	 * Данный метод используется для получения итератора по тиковым данным, где
	 * начало потока данных настроено на указанноге время. 
	 * <p>
	 * Отсутствие данных по указанному идентификатору не приводит к выбросу
	 * исключения. В таких случаях возвращается пустой ридер (не содержащий ни
	 * одного тика). Но может быть выброшено исключение в случае, если
	 * запрошенный объект (например, инструмент) отсутствует в базе данных (т.е.
	 * неизвестен). Запрос данных с указанием времени, находящимся за границами
	 * имеющихся данных так же не приводит к ошибке. Обе описанные ситуации
	 * рассматриваются как естественное отсутствие данных. Например, по причине
	 * периода отсутствия торгов или невысокой ликвидности инструмента.
	 * <p>
	 * @param dataId идентификатор данных (например, идентификатор инструмента)
	 * @param start время начала данных
	 * @return итератор тиковых данных
	 * @throws DataException ошибка доступа к данным. 
	 */
	public Aqiterator<Tick> getIterator(String dataId, LocalDateTime start)
			throws DataException;
	
	public Aqiterator<Tick> getIterator(Symbol symbol, LocalDateTime start)
			throws DataException;

}
