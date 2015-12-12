package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Набор инструментов торговли.
 */
public class Securities {
	private final SecuritiesEventDispatcher dispatcher;
	private final SecurityFactory factory;
	
	/**
	 * Карта определения инструмента по дескриптору.
	 * Содержит записи для каждого инструмента.
	 */
	private final Map<Symbol, EditableSecurity> map;
	
	/**
	 * Конструктор
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param factory фабрика инструментов
	 */
	public Securities(SecuritiesEventDispatcher dispatcher,
						  SecurityFactory factory)
	{
		super();
		this.dispatcher = dispatcher;
		map = new LinkedHashMap<Symbol, EditableSecurity>();
		this.factory = factory;
	}
	
	/**
	 * Конструктор
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public Securities(SecuritiesEventDispatcher dispatcher) {
		this(dispatcher, new SecurityFactory());
	}
	
	SecurityFactory getFactory() {
		return factory;
	}

	public List<Security> getSecurities() {
		return new Vector<Security>(map.values());
	}
	
	/**
	 * Получить используемый диспетчер событий
	 * <p>
	 * @return диспетчер событий
	 */
	public SecuritiesEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	public Security getSecurity(Symbol symbol)
		throws SecurityException
	{
		Security security = map.get(symbol);
		if ( security == null ) {
			throw new SecurityNotExistsException(symbol);
		}
		return security;
	}
	
	public boolean isSecurityExists(Symbol symbol) {
		return map.containsKey(symbol);
	}

	public EventType OnSecurityAvailable() {
		return dispatcher.OnAvailable();
	}

	public EditableSecurity
		getEditableSecurity(EditableTerminal terminal, Symbol symbol)
	{
		EditableSecurity security = map.get(symbol);
		if ( security == null ) {
			security = factory.createInstance(terminal, symbol);
			map.put(symbol, security);
			dispatcher.startRelayFor(security);
		}
		return security;
	}

	public void fireEvents(EditableSecurity security) {
		synchronized ( security ) {
			if ( security.isAvailable() ) {
				security.fireChangedEvent();
			} else {
				security.setAvailable(true);
				dispatcher.fireAvailable(security);
			}
			security.resetChanges();
		}
	}

	public EventType OnSecurityChanged() {
		return dispatcher.OnChanged();
	}

	public EventType OnSecurityTrade() {
		return dispatcher.OnTrade();
	}

	public int getSecuritiesCount() {
		return map.size();
	}

	/**
	 * Установить экземпляр инструмента.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @param security экземпляр инструмента
	 */
	protected void setSecurity(Symbol symbol, EditableSecurity security) {
		map.put(symbol, security);
	}

}
