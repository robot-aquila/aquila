package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;
import ru.prolib.aquila.quik.dde.Cache;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Терминала QUIK.
 * <p>
 * Реализован в виде расширения декоратора терминала с добавлением некоторых
 * специфических методов. 
 */
public class QUIKTerminalImpl extends TerminalDecorator
	implements QUIKTerminal
{
	private QUIKServiceLocator locator;
	
	public QUIKTerminalImpl() {
		super();
	}
	
	/**
	 * Установить сервис-локатор, ассоциированный с терминалом.
	 * <p>
	 * @param locator сервис-локатор
	 */
	public synchronized void setServiceLocator(QUIKServiceLocator locator) {
		this.locator = locator;
	}

	@Override
	public synchronized Cache getDdeCache() {
		return locator.getDdeCache();
	}

}
