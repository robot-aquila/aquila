package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Низкоуровневые функции согласования объектов.
 */
public class AssemblerLowLvl {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final Cache cache;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerLowLvl.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal
	 * @param cache
	 */
	public AssemblerLowLvl(EditableTerminal terminal, Cache cache) {
		super();
		this.terminal = terminal;
		this.cache = cache;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить DDE-кэш.
	 * <p> 
	 * @return DDE-кэш
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Проверить доступность портфеля и получить счет.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return торговый счет или null, если не удалось определить счет
	 */
	public Account getAccountByOrderCache(OrderCache entry) {
		return getAccount(entry.getClientCode(), entry.getAccountCode(),
				entry.getId());
	}
	
	/**
	 * Проверить доступность портфеля и получить счет.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return торговый счет или null, если не удалось определить счет
	 */
	public Account getAccountByStopOrderCache(StopOrderCache entry) {
		return getAccount(entry.getClientCode(), entry.getAccountCode(),
				entry.getId());
	}
	
	/**
	 * Проверить доступность портфеля и получить счет.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @param orderId номер заявки
	 * @return торговый счет или null, если не удалось определить счет
	 */
	private Account
		getAccount(String clientCode, String accountCode, Long orderId)
	{
		if ( ! cache.isAccountRegistered(clientCode, accountCode)) {
			logger.debug("Order {} still wait for account by clnt&acnt: {}@{}",
				new Object[] { orderId, clientCode, accountCode });
			return null;
		}
		Account account = cache.getAccount(clientCode, accountCode);
		if ( ! terminal.isPortfolioAvailable(account) ) {
			logger.debug("Order {} still wait for portfolio: {}",
				new Object[] { orderId, account });
			return null;
		}
		return account;
	}

	/**
	 * Проверить доступность инструмента и получить дескриптор.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return дескриптор инструмента или null, если не удалось определить
	 */
	public SecurityDescriptor
		getSecurityDescriptorByOrderCache(OrderCache entry)
	{
		return getSecurityDescriptor(entry.getSecurityCode(),
				entry.getSecurityClassCode(), entry.getId());
	}
	
	/**
	 * Проверить доступность инструмента и получить дескриптор.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return дескриптор инструмента или null, если не удалось определить
	 */
	public SecurityDescriptor
		getSecurityDescriptorByStopOrderCache(StopOrderCache entry)
	{
		return getSecurityDescriptor(entry.getSecurityCode(),
				entry.getSecurityClassCode(), entry.getId());
	}
	
	/**
	/**
	 * Проверить доступность инструмента и получить дескриптор.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @param orderId номер заявки
	 * @return дескриптор инструмента или null, если не удалось определить
	 */
	private SecurityDescriptor
		getSecurityDescriptor(String code, String classCode, Long orderId)
	{
		if ( ! cache.isSecurityDescriptorRegistered(code, classCode)) {
			logger.debug("Order {} still wait for descr by code&class: {}@{}",
				new Object[] { orderId, code, classCode });
			return null;
		}

		SecurityDescriptor descr =
			cache.getSecurityDescriptorByCodeAndClass(code, classCode);
		if ( ! terminal.isSecurityExists(descr) ) {
			logger.debug("Order {} still wait for security: {}",
				new Object[] { orderId, descr });
			return null;
		}
		return descr;
	}
	
	/**
	 * Согласовать статус заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @param order экземпляр заявки
	 */
	public void adjustOrderStatus(OrderCache entry, EditableOrder order) {
		OrderStatus orderStatus = order.getStatus();
		if ( orderStatus != OrderStatus.ACTIVE
		  && orderStatus != OrderStatus.PENDING )
		{
			// Все заявки в статусе отличном от ACTIVE и PENDING игнорируются
			return;
		}
		
		OrderStatus entryStatus = entry.getStatus();
		if ( entryStatus == OrderStatus.FILLED ) {
			// Статус заявки не проверяем, так как в случае создания нового
			// экземпляра заявки, новая заявка будет в статусе PENDING
			// и нигде в программе смена этого статуса выполнена не будет,
			// так как перевод PENDING -> ACTIVE выполняется только для заявок,
			// созданных локально. Просто проверяем согласованность по сделкам,
			// добавление которых должно выставить корректный неисполненный
			// остаток.
			Long rest = order.getQtyRest();
			if ( rest != null && rest == 0L ) {
				order.setStatus(OrderStatus.FILLED);
				order.setLastChangeTime(order.getLastTradeTime());
			}
			
		} else if ( entryStatus == OrderStatus.CANCELLED ) {
			// Неисполненный остаток отмененной заявки должен быть согласован
			// по сделкам. Только когда неисполненный остаток локальной заявки
			// и кэш записи совпадает, только тогда заявку можно считать
			// согласованной. В этом случае выполняется смена статуса заявки.
			Long rest = order.getQtyRest();
			if ( rest != null && rest == entry.getQtyRest() ) {
				order.setStatus(OrderStatus.CANCELLED);
				order.setLastChangeTime(entry.getWithdrawTime());
			}
			
		} else if ( orderStatus == OrderStatus.PENDING ) {
			// Здесь мы будем только в случае, когда локальная заявка -
			// это недавно созданный экземпляр, а в ТС активная заявка. 
			order.setStatus(OrderStatus.ACTIVE);
			
		}
	}
	
	/**
	 * Согласовать статус стоп-заявки.
	 * <p>
	 * Помимо статуса так же выставляет время последнего изменения и номер
	 * связанной заявки (в случае исполнения).
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @param order экземпляр заявки
	 */
	public
		void adjustStopOrderStatus(StopOrderCache entry, EditableOrder order)
	{
		Long orderId = entry.getId();
		OrderStatus entryStatus = entry.getStatus();
		if ( entryStatus == OrderStatus.ACTIVE ) {
			if ( order.getStatus() == OrderStatus.PENDING ) {
				logger.debug("Stop-order {} set activated", orderId);
				order.setStatus(OrderStatus.ACTIVE);
			}
		} else if ( entryStatus == OrderStatus.CANCELLED ) {
			logger.debug("Stop-Order {} set cancelled by entry", orderId);
			order.setStatus(entryStatus);
			order.setLastChangeTime(entry.getWithdrawTime());
		} else if ( entryStatus == OrderStatus.FILLED ) {
			Long linkId = entry.getLinkedOrderId();
			if ( terminal.isOrderExists(linkId) ) {
				order.setStatus(entryStatus);
				order.setLinkedOrderId(linkId);
				order.setLastChangeTime(terminal.getCurrentTime());
				logger.debug("Stop-Order {} set filled with linked order {}",
					new Object[] { orderId, linkId });
			} else {
				logger.debug("Stop-Order {} wait for linked order", orderId);
			}
		}
	}
	
	/**
	 * Согласовать сделку заявки.
	 * <p>
	 * Проверяет необходимость согласования указанной сделки заявки по
	 * кэш-записи сделки. Если кэш-запись сделки указывает на несогласованную
	 * сделку, то создает экземпляр сделки и добавляет его в заявку. Если
	 * заявка доступна, то так же генерирует событие о новой сделке. Заявка
	 * должна содержать корректные значения первичных атрибутов.
	 * <p>
	 * @param entry кэш-запись сделки
	 * @param order экземпляр заявки
	 * @return true - сделка была добавлена, false - сделка не добавлена
	 */
	public boolean adjustOrderTrade(TradeCache entry, EditableOrder order) {
		if ( order.hasTrade(entry.getId()) ) {
			return false;
		}
		Trade trade = new Trade(terminal);
		trade.setDirection(order.getDirection());
		trade.setId(entry.getId());
		trade.setOrderId(entry.getOrderId());
		trade.setPrice(entry.getPrice());
		trade.setQty(entry.getQty());
		trade.setSecurityDescriptor(order.getSecurityDescriptor());
		trade.setTime(entry.getTime());
		trade.setVolume(entry.getVolume());
		order.addTrade(trade);
		if ( order.isAvailable() ) {
			order.fireTradeEvent(trade);
		}
		return true;
	}
	
	/**
	 * Сравнить двух помощников.
	 * <p>
	 * Два помощника считаются эквивалентными, если они используют один и тот же
	 * экземпляр терминала и эквивалентные кэши. Такой способ сравнения выбран с
	 * целью подавления бесконечной рекурсии при сравнении, когда терминал прямо
	 * или косвенно ссылается на экземпляр. Например, терминал содержит стартер,
	 * который ссылается на фасад сборки объектов, содержащий экземпляр
	 * помощника, который в свою очередь ссылается на терминал.
	 */
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other == null || other.getClass() != AssemblerLowLvl.class) {
			return false;
		}
		AssemblerLowLvl o = (AssemblerLowLvl) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}
