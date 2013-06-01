package ru.prolib.aquila.core.report;

import java.util.Date;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Пользовательский интерфейс трейд-отчета.
 * <p>
 * Данный интерфейс представляет собой доступ к отчету о трейде - суммарной
 * информации о последовательности сделок от открытия до закрытия позиции.
 * Интерфейс предназначен для публичного использования потребителями сервиса.
 */
public interface TradeReport extends Comparable<TradeReport>, Cloneable {
	
	/**
	 * Получить тип позиции.
	 * <p>
	 * @return {@link PositionType#LONG} - длинная,
	 * {@link PositionType#SHORT} - короткая
	 */
	public PositionType getType();
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Получить суммарный объем по сделкам входа в трейд.
	 * <p>
	 * @return объем
	 */
	public Double getEnterVolume();
	
	/**
	 * Получить суммарный объем по сделкам выхода из трейда.
	 * <p>
	 * @return объем или null, если нет ни сделки на закрытие
	 */
	public Double getExitVolume();
	
	/**
	 * Получить среднюю цену входа в трейд.
	 * <p>
	 * @return средняя цена входа
	 */
	public Double getEnterPrice();
	
	/**
	 * Получить среднюю цену выхода из трейда.
	 * <p>
	 * @return средняя цена выхода или null, если нет ни сделки на закрытие
	 */
	public Double getExitPrice();
	
	/**
	 * Получить текущее количество трейда.
	 * <p> 
	 * @return количество
	 */
	public Long getQty();
	
	/**
	 * Получить незакрытое количество трейда.
	 * <p>
	 * @return количество
	 */
	public Long getUncoveredQty();
	
	/**
	 * Получить время входа в трейд.
	 * <p>
	 * @return время входа в трейд
	 */
	public Date getEnterTime();
	
	/**
	 * Получить время выхода из трейда.
	 * <p>
	 * @return время выхода из трейда или null, если трейд не закрыт
	 */
	public Date getExitTime();
	
	/**
	 * Этот трейд открыт?
	 * <p>
	 * @return true - трейд открыт, false - закрыт
	 */
	public boolean isOpen();
	
	/**
	 * Создать копию объекта.
	 * <p>
	 * @return копия объекта
	 */
	public TradeReport clone();

}
