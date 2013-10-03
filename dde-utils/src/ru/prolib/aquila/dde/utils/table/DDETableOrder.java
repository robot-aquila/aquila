package ru.prolib.aquila.dde.utils.table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Dependencies;
import ru.prolib.aquila.core.utils.DependencyRule;
import ru.prolib.aquila.dde.utils.*;

/**
 * Смена порядка поступления таблиц.
 * <p>
 * Данный класс представляет собой обработчик/ретранслятор событий и  позволяет
 * изменить порядок первичного поступления таблиц.
 * <p>
 * Такая необходимость возникает когда DDE-сервер отправляет таблицы в
 * неподходящем порядке. Например, в процессе обработки DDE-таблиц из QUIK
 * важно, что бы сначала была обработана таблица портфелей (что бы создать
 * соответствующие портфели), а затем таблица позиций. При отсутствии
 * информации о портфелях, данные о позициях будут проигнорированы,
 * что приведет к некорректному отражению текущего состояния портфеля.
 * <p>
 * Данный класс использует граф зависимостей типа
 * {@link ru.prolib.aquila.core.utils.Dependencies Dependencies} для
 * описания зависимостей между таблицами. Объект данного класса является
 * одновременно обозревателем и типом события, ожидает поступления
 * {@link ru.prolib.aquila.dde.utils.DDETableEvent DDETableEvent} и
 * выполняет проверку зависимостей: если для входящей таблицы существует
 * зависимость, то таблица помещается в очередь ожидания. Если для таблицы 
 * нет зависимости, то таблица ретранслируется. При этом, в графе зависимость
 * от ретранслированной таблицы снимается для всех других таблиц.
 * <p>
 * 2012-09-20<br>
 * $Id: DDETableOrder.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class DDETableOrder extends EventTypeImpl implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DDETableOrder.class);
	}
	
	private final EventDispatcher dispatcher;
	private final Dependencies<String> deps;
	private final Map<String, DependencyRule> rules;
	private final LinkedList<DDETableEvent> queue;

	/**
	 * Создать ретранслятор.
	 * <p>
	 * Данный конструктор подразумевает, что для каждой таблицы имеющей
	 * неразрешенную зависимость выполняется правило
	 * {@link ru.prolib.aquila.core.utils.DependencyRule.WAIT
	 * DependencyRule.WAIT}.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param deps граф зависимостей
	 */
	public DDETableOrder(EventDispatcher dispatcher,
						 Dependencies<String> deps)
	{
		this(dispatcher, deps, new HashMap<String, DependencyRule>());
	}
	
	/**
	 * Создать ретранслятор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param deps граф зависимостей
	 * @param rules правила обработки данных при наличии зависимостей
	 */
	public DDETableOrder(EventDispatcher dispatcher,
						 Dependencies<String> deps,
						 Map<String, DependencyRule> rules)
	{
		super();
		this.dispatcher = dispatcher;
		this.deps = deps;
		this.queue = new LinkedList<DDETableEvent>();
		this.rules = rules;
	}
	
	/**
	 * Получить граф зависимостей.
	 * <p>
	 * @return граф звисимостей
	 */
	public Dependencies<String> getDependencies() {
		return deps;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( event.getClass() == DDETableEvent.class ) {
			DDETableEvent e = (DDETableEvent) event;
			String tableName = e.getTable().getTopic();
			if ( deps.hasDependency(tableName)
			  && rules.get(tableName) == DependencyRule.DROP )
			{
				Object[] args = { tableName, e.getTable().getRows() };
				logger.debug("Dropped table: {} ({} rows)", args);
				return;
			}
			queue.add(e);
			processQueue();
		} else {
			logger.warn("Ignore unknown event type: {}", event);
		}
	}
	
	private final DDETableEvent newEvent(DDETableEvent e) {
		return new DDETableEvent(this, e.getService(), e.getTable());
	}
	
	private void processQueue() {
		int i = 0;
		while ( i < queue.size() ) {
			DDETableEvent e = queue.get(i);
			String tableName = e.getTable().getTopic();
			if ( ! deps.hasDependency(tableName) ) {
				deps.dropDependentsTo(tableName);
				dispatcher.dispatch(newEvent(e));
				queue.remove(i);
				i = 0;
			} else {
				i ++;
			}
		}
	}

}
