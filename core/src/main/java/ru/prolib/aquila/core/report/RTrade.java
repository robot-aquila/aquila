package ru.prolib.aquila.core.report;

import org.joda.time.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Публичный интерфейс записи трейд-отчета.
 * <p>
 * Данный интерфейс представляет собой доступ к отчету о трейде - суммарной
 * информации о последовательности сделок от открытия до закрытия позиции.
 * Интерфейс предназначен для публичного использования потребителями сервиса.
 */
public interface RTrade extends Comparable<RTrade>, Cloneable {
	
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
	public Symbol getSymbol();
	
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
	public DateTime getEnterTime();
	
	/**
	 * Получить время выхода из трейда.
	 * <p>
	 * @return время выхода из трейда или null, если трейд не закрыт
	 */
	public DateTime getExitTime();
	
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
	public RTrade clone();
	
	/**
	 * Получить прибыль или убыток.
	 * <p>
	 * @return положительное - доход, отрицательное - убыток выраженный в
	 * единицах цены или null, если трейд не закрыт
	 */
	public Double getProfit();
	
	/**
	 * Получить прибыль или убыток в процентном выражении.
	 * <p>
	 * @return положительное - доход, отрицательное - убыток в процентном
	 * выражении от цены входа или null, если трейд не закрыт
	 */
	public Double getProfitPerc();

}
