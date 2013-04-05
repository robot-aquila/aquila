package ru.prolib.aquila.core.utils;

import java.util.Set;

/**
 * Интерфейс набора зависимостей.
 * <p>
 * @param <T> - тип объектов, между которыми устанавливаются зависимости
 * <p>
 * 2012-11-07<br>
 * $Id: Dependencies.java 306 2012-11-07 13:03:09Z whirlwind $
 */
public interface Dependencies<T> {

	/**
	 * Проверка на наличие зависимостей.
	 * <p>
	 * @param subj объект, от которого потенциально зависят другие объекты
	 * @return наличие зависимостей
	 */
	public boolean hasDependentTo(T subj);
	
	/**
	 * Проверка на наличие зависимостей.
	 * <p>
	 * @param subj объект, потенциально зависящий от других объектов
	 * @return наличие зависимостей
	 */
	public boolean hasDependency(T subj);
	
	/**
	 * Проверка на наличие зависимости.
	 * <p>
	 * @param subj объект, потенциально зависимый (который зависит)
	 * @param dependentTo объект, от которого зависит первый объект 
	 * @return наличие зависимости
	 */
	public boolean hasDependency(T subj, T dependentTo);
	
	/**
	 * Получить список зависимых объектов.
	 * <p>
	 * @param subj объект, от которого зависят возвращаемые объекты
	 * @return список зависимых объектов
	 */
	public Set<T> getDependentsTo(T subj);
	
	/**
	 * Получить список зависимостей.
	 * <p>
	 * @param subj зависимый объект (который зависит)
	 * @return список зависимостей (от которых зависит указанный объект) 
	 */
	public Set<T> getDependencies(T subj);
	
	/**
	 * Установить зависимость.
	 * <p>
	 * @param subj объект, для которого устанавливается зависимость
	 * @param dependentTo объект, от которого зависит первый объект
	 * @return набор зависимостей (возвращает сам себя)
	 */
	public Dependencies<T> setDependency(T subj, T dependentTo);
	
	/**
	 * Сбросить зависимость.
	 * <p>
	 * @param subj зависимый объект (который зависит)
	 * @param dependentTo субъект-зависимости (от которого зависит)
	 * @return набор зависимостей (возвращает сам себя)
	 */
	public Dependencies<T> dropDependency(T subj, T dependentTo);
	
	/**
	 * Сбросить все зависимости.
	 * <p>
	 * @param subj зависимый объект (для которого сбросить зависимости)
	 * @return набор зависимостей (возвращает сам себя)
	 */
	public Dependencies<T> dropDependencies(T subj);
	
	/**
	 * Сбросить зависимости для всех зависимых объектов.
	 * <p>
	 * @param dependentTo субъект-зависимости (от которого зависит)
	 * @return набор зависимостей (возвращает сам себя)
	 */
	public Dependencies<T> dropDependentsTo(T dependentTo);

}
