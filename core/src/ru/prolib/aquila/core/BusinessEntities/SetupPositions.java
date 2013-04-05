package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;


/**
 * Интерфейс набора спецификаций позиций.
 * <p>
 * 2013-01-10<br>
 * $Id: SetupPositions.java 411 2013-01-12 10:55:36Z whirlwind $
 */
public interface SetupPositions extends Cloneable {
	
	/**
	 * Получить спецификацию позиции по инструменту.
	 * <p>
	 * Если это новый инструмент, то создается настройка нейтральной позиции по
	 * инструменту.
	 * <p>
	 * @param security дескриптор инструмента
	 * @return спецификация позиции
	 */
	public SetupPosition getPosition(SecurityDescriptor security);
	
	/**
	 * Удалить инструмент из спецификации портфеля.
	 * <p>
	 * Если указанный инструмент не в портфеле, то ничего не происходит.
	 * <p>
	 * @param security дескриптор инструмента
	 */
	public void removePosition(SecurityDescriptor security);
	
	/**
	 * Получить спецификации позиций портфеля.
	 * <p>
	 * @return спецификации позиций
	 */
	public List<SetupPosition> getPositions();
	
	/**
	 * Создать копию объекта.
	 * <p>
	 * @return копия объекта
	 */
	public SetupPositions clone();

}
