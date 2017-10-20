package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;

/**
 * Функции сборки объектов модели.
 * <p>
 * Данный класс представляет собой набор функций уровня групп объектов.
 * Запросы на сборку отдельных объектов делегируются на более низкий уровень
 * сборки.
 */
public class AssemblerL1 {
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerL1.class);
	}
	
	private final QUIKTerminal terminal;
	private final AssemblerL2 l2;
	
	AssemblerL1(QUIKTerminal terminal, AssemblerL2 l2) {
		super();
		this.terminal = terminal;
		this.l2 = l2;
	}
	
	AssemblerL1(QUIKTerminal terminal) {
		this(terminal, new AssemblerL2(terminal));
	}
	
	QUIKTerminal getTerminal() {
		return terminal;
	}
	
	AssemblerL2 getAssemblerL2() {
		return l2;
	}
	
	/**
	 * Собрать портфель.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(PortfolioEntry)}.
	 * <p>
	 * @param entry кэш-запись портфеля
	 * @return всегда true
	 */
	public boolean tryAssemble(PortfolioEntry entry) {
		return l2.tryAssemble(entry);
	}

	/**
	 * Выполнить попытку сборки позиции.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(PositionEntry)}.
	 * <p>
	 * @param entry кэш-запись позиции
	 * @return true - данные были применены, false - данные не согласованы
	 */
	public boolean tryAssemble(PositionEntry entry) {
		return l2.tryAssemble(entry);
	}
	
	/**
	 * Выполнить сборку инструмента.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(SecurityEntry)}.
	 * <p>
	 * @param entry кэш-запись инструмента
	 * @return всегда true
	 */
	public boolean tryAssemble(SecurityEntry entry) {
		return l2.tryAssemble(entry);
	}
	
	/**
	 * Выполнить цикл сборки позиций по инструменту.
	 * <p>
	 * Выполняет попытку сборки всех кэшированных позиций, соответствующих
	 * инструменту с указанным кратким наименованием. В случае успешной сборки,
	 * кэш-запись позиции удаляется из кэша позиций.
	 * <p>
	 * @param securityShortName краткое наименование инструмента
	 */
	public void tryAssemblePositions(String securityShortName) {
		PositionsCache cache = terminal.getDataCache().getPositionsCache();
		synchronized ( cache ) {
			for ( PositionEntry entry :  cache.get(securityShortName) ) {
				if ( l2.tryAssemble(entry) ) {
					cache.purge(entry);
				}
			}
		}
	}
	
	/**
	 * Выполнить цикл обработки анонимных сделок.
	 */
	public void tryAssembleTrades() {
		TradesCache cache = terminal.getDataCache().getTradesCache();
		TradesEntry entry;
		while ( (entry = cache.getFirst()) != null ) {
			if ( ! l2.tryAssemble(entry) ) {
				break;
			}
			cache.purgeFirst();
		}
	}
	
	/**
	 * Выполнить попытку согласования заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public void tryAssemble(T2QOrder entry) {
		EditableOrder order = l2.tryGetOrder(entry);
		if ( order == null ) {
			return;
		}
		synchronized ( order ) {
			l2.tryActivate(order);
			for ( T2QTrade trade : terminal.getDataCache()
					.getOwnTradesByOrder(entry.getOrderId()) )
			{
				l2.tryAssemble(order, trade);
			}
			l2.tryFinalize(order, entry);
		}
	}
	
	/**
	 * Откорректировать значение нумератора заявок.
	 * <p>
	 * Данный метод использует номер транзакции, соответствующий заявке,
	 * для корректировки текущего значения нумератора заявок. Это необходимо
	 * для того, что бы исключить некорректное сопоставление данных кэша
	 * и соответствующих заявок для тех заявок, которые были созданы в процессе
	 * предыдущего запуска программы. Если номер транзакции не превышает
	 * текущее значение нумератора, то никаких изменений не выполняется.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public void correctOrderNumerator(T2QOrder entry) {
		int id = (int) entry.getTransId();
		Counter numerator = terminal.getOrderIdSequence();
		synchronized ( numerator ) {
			if ( numerator.get() < id ) {
				numerator.set(id);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerL1.class ) {
			return false;
		}
		AssemblerL1 o = (AssemblerL1) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.l2, l2)
			.isEquals();
	}
	
	/**
	 * Исправить пропадающий номер транзакции.
	 * <p>
	 * Фикс квикового глюка, когда перенесенные на следующий день заявки теряют
	 * заданный при создании номер транзакции. Исправление выполняется только
	 * в случае если номер транзакции кэш-записи равен нулю. В этом случае, 
	 * если заявка с таким системным номером уже есть в кэше, то создается
	 * новая кэш-запись на основе полученной в качестве аргумента и с номером
	 * транзакции, который соответствует кэш-записи находящейся в кэше (то есть
	 * предыдущей версии).
	 * <p>
	 * @param entry исходная кэш-запись
	 * @return исправленная кэш-запись
	 */
	public T2QOrder fixme(T2QOrder entry) {
		if ( entry.getTransId() != 0L ) {
			return entry;
		}
		Cache cache = terminal.getDataCache();
		synchronized ( cache ) {
			long orderId = entry.getOrderId();
			T2QOrder prevEntry = cache.getOrder(orderId);
			if ( prevEntry == null ) {
				//logger.debug("New order #{} entry without trans.ID", orderId);
				return entry;
			} else {
				long transId = prevEntry.getTransId();
				//Object args[] = { orderId, transId };
				//logger.debug("For order #{} zeroed trans.ID fixed to {}", args);
				return new T2QOrder(entry, transId);
			}
		}
	}

}
