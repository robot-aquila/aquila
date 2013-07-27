package ru.prolib.aquila.ui;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

/**
 * Интерфейс плагина.
 * <p>
 * 2013-02-28<br>
 * $Id: AquilaPlugin.java 559 2013-03-05 15:16:19Z whirlwind $
 */
public interface AquilaPlugin extends Starter {
	
	/**
	 * Инициализировать плагин для работы с терминалом.
	 * <p>
	 * Данный метод вызывается для каждого плагина один раз сразу после
	 * создания. Этот метод вызывается ДО вызова метода
	 * {@link #createUI(AquilaUI)} (если программа запускается в режиме UI).
	 * <p>
	 * @param locator сервис-локатор
	 * @param terminal рабочий терминал
	 * @param arg строка-аргумент запуска, которая может быть указана через
	 * пробел после класса в списке загружаемых плагинов. Если после имени
	 * класса нет строки-аргумента, то в качестве значения этого аргумента
	 * передается null
	 * @throws Exception
	 */
	public void initialize(ServiceLocator locator, Terminal terminal,
			String arg) throws Exception;

	/**
	 * Создать UI элементы плагина.
	 * <p>
	 * Данный метод вызывается для каждого плагина в случае, если программа
	 * запущена в режиме пользовательского интерфейса. Нельзя связывать логику
	 * работы плагина с его элементами пользовательского интерфейса, так как в
	 * случае режима работы без UI это обязательно приведет к ошибкам. Связь
	 * должна быть односторонней: от элементов UI к объектам бизнес-процесса
	 * плагина. Этот метод вызывается ПОСЛЕ вызова метода
	 * {@link #initialize(ServiceLocator, Terminal, String)}.
	 * <p>
	 * @param facade фасад UI
	 * @throws Exception
	 */
	public void createUI(AquilaUI facade) throws Exception;

}
