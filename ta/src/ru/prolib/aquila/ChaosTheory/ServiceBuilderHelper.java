package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public interface ServiceBuilderHelper {
	
	/**
	 * Получить значение атрибута.
	 * @param attr
	 * @return
	 * @throws ServiceBuilderException
	 */
	public String getAttribute(String attr, HierarchicalStreamReader reader)
		throws ServiceBuilderAttributeNotExistsException;

	/**
	 * Получить int атрибут текущего узла.
	 * @param attr
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract int getInt(String attr, HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить int атрибут текущего узла либо вернуть значение по умолчанию.
	 * @param attr
	 * @param def
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract int getInt(String attr, int def,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить int из содержимого текущего узла. 
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract int getInt(HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить long атрибут текущего узла.
	 * @param attr
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract long getLong(String attr, HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить long атрибут текущего узла либо вернуть значение по умолчанию.
	 * @param attr
	 * @param def
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract long getLong(String attr, long def,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить long из содержимого текущего узла. 
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract long getLong(HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить double атрибут текущего узла.
	 * @param attr
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract double getDouble(String attr,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить double атрибут текущего узла либо вернуть значение по умолчанию.
	 * @param attr
	 * @param def
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract double getDouble(String attr, double def,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить double из содержимого текущего узла.
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract double getDouble(HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить String атрибут текущего узла.
	 * @param attr
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract String getString(String attr,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить String атрибут текущего узла либо вернуть значение по умолчанию.
	 * @param attr
	 * @param def
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract String getString(String attr, String def,
			HierarchicalStreamReader reader) throws ServiceBuilderException;

	/**
	 * Получить String из содержимого текущего узла.
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract String getString(HierarchicalStreamReader reader)
			throws ServiceBuilderException;

	/**
	 * Получить набор параметров.
	 * 
	 * Создает объект параметров, который заполняет на основе содержимого
	 * текущего узла. Имена дочерних узлов используются в качестве имен
	 * параметров, а содержимое - в качестве значений соответствующих
	 * параметров.
	 * 
	 * @param reader
	 * @return
	 * @throws ServiceBuilderException
	 */
	public abstract Props getProps(HierarchicalStreamReader reader)
			throws ServiceBuilderException;
}