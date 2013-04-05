package ru.prolib.aquila.stat;

import java.util.List;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.util.Observable;

/**
 * Наблюдаемая позиция.
 * Генерирует события об изменении и закрытии позиции.
 */
@Deprecated
public interface TrackingPosition extends Observable {
	public static final Integer EVENT_CHANGED = 0x01;
	public static final Integer EVENT_CLOSED = 0x02;

	/**
	 * Получить список изменений.
	 * 
	 * @return
	 */
	public List<TrackingPositionChange> getChanges();

	/**
	 * Зафиксировать изменение позиции.
	 * 
	 * Добавляет информацию об изменениях позиции в историю изменений после чего
	 * проверяет не была ли позиция закрыта. Позиция считается закрытой, если
	 * суммарное количество по всем заявкам дает ноль. Частный случай, когда
	 * суммарное количество меньше (для лонга) или больше (для шорта) нуля
	 * так же учитывается - в этом случае в лог будет выведено предупреждение.    
	 * 
	 * @param order заявка-основание
	 * @throws TrackingException объект закрыт для модификаций, заявка
	 * с нулевым количеством, заявка не исполнена, заявка не лимитная
	 */
	public void addChange(Order order) throws TrackingException;

	/**
	 * Является данный трейд завершенным?
	 * 
	 * @return
	 */
	public boolean isClosed();
	
	/**
	 * Является данный трейд игрой в короткую?
	 * 
	 * @return
	 */
	public boolean isShort();
	
	/**
	 * Является данный трейд игрой в длинную?
	 * 
	 * @return
	 */
	public boolean isLong();
	
	/**
	 * Получить первую сделку трейда.
	 * 
	 * @return
	 */
	public TrackingPositionChange getFirstChange();
	
	/**
	 * Получить последнюю сделку трейда.
	 * 
	 * @return
	 */
	public TrackingPositionChange getLastChange();

}