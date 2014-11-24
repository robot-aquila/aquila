package ru.prolib.aquila.core.data;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Набор свойств.
 * <p>
 * Более высокоуровневая обертка над стандартным набором свойств.
 */
public class Props {
	private final Properties props;
	
	public Props() {
		this(new Properties());
	}
	
	public Props(Properties props) {
		super();
		this.props = props;
	}
	
	/**
	 * Получить вещественное значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @return вещественное значение
	 * @throws ValueNotExistsException значение отсутствует
	 * @throws ValueFormatException некорректный формат
	 */
	public Double getDouble(String variable) throws DataException {
		String value = props.getProperty(variable);
		if ( value == null ) {
			throw new ValueNotExistsException(variable);
		}
		try {
			return Double.parseDouble(value);
		} catch ( NumberFormatException e ) {
			throw new ValueFormatException(variable, value);
		}
	}
	
	/**
	 * Получить вещественное значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @param defaultValue значение по-умолчанию возвращается в том случае,
	 * если значения нет в наборе
	 * @return вещественное значение
	 * @throws ValueFormatException некорректный формат
	 */
	public Double getDouble(String variable, Double defaultValue)
			throws DataException
	{
		return props.containsKey(variable) ? getDouble(variable) : defaultValue;
	}
	
	/**
	 * Получить строковое значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @return строковое значение
	 * @throws ValueNotExistsException значение отсутствует
	 */
	public String getString(String variable) throws DataException {
		String value = props.getProperty(variable);
		if ( value == null ) {
			throw new ValueNotExistsException(variable);
		}
		return value;
	}
	
	/**
	 * Получить строковое значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @param defaultValue значение по-умолчанию возвращается в том случае,
	 * если значения нет в наборе
	 * @return строковое значение
	 */
	public String getString(String variable, String defaultValue) {
		String value = props.getProperty(variable);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Получить целочисленное значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @return целочисленное значение
	 * @throws ValueNotExistsException значение отсутствует
	 * @throws ValueFormatException некорректный формат
	 */
	public Integer getInteger(String variable) throws DataException {
		String value = props.getProperty(variable);
		if ( value == null ) {
			throw new ValueNotExistsException(variable);
		}
		try {
			return Integer.parseInt(value);
		} catch ( NumberFormatException e ) {
			throw new ValueFormatException(variable, value);
		}
	}
	
	/**
	 * Получить целочисленное значение.
	 * <p>
	 * @param variable идентификатор значения в наборе
	 * @param defaultValue значение по-умолчанию возвращается в том случае,
	 * если значения нет в наборе
	 * @return целочисленное значение
	 * @throws DataException 
	 * @throws ValueFormatException некорректный формат
	 */
	public Integer getInteger(String variable, Integer defaultValue)
			throws DataException
	{
		return props.containsKey(variable) ? getInteger(variable) : defaultValue;
	}
	
	/**
	 * Прочитать список свойств.
	 * <p>
	 * Делегирует методу {@link Properties#load(Reader)}. 
	 * <p>
	 * @param reader входящий поток данных
	 * @throws DataException
	 */
	public void load(Reader reader) throws DataException {
		try {
			props.load(reader);
		} catch ( IOException e ) {
			throw new DataException(e);
		}
	}

}
