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
		if ( ! cache.isAccountRegistered(entry.getClientCode(),
				entry.getAccountCode()))
		{
			logger.debug("Order {} still wait for account by clnt&acnt: {}@{}",
					new Object[] { entry.getId(), entry.getClientCode(),
					entry.getAccountCode() });
			return null;
		}
		Account account = cache.getAccount(entry.getClientCode(),
				entry.getAccountCode());
		if ( ! terminal.isPortfolioAvailable(account) ) {
			logger.debug("Order {} still wait for portfolio: {}",
					new Object[] { entry.getId(), account });
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
		if ( ! cache.isSecurityDescriptorRegistered(entry.getSecurityCode(),
				entry.getSecurityClassCode()))
		{
			logger.debug("Order {} still wait for descr by code&class: {}@{}",
					new Object[] { entry.getId(), entry.getSecurityCode(),
					entry.getSecurityClassCode() });
			return null;
		}

		SecurityDescriptor descr = cache.getSecurityDescriptorByCodeAndClass(
				entry.getSecurityCode(), entry.getSecurityClassCode());
		if ( ! terminal.isSecurityExists(descr) ) {
			logger.debug("Order {} still wait for security: {}",
					new Object[] { entry.getId(), descr });
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
		if ( order.getQtyRest() == 0L ) {
			order.setStatus(OrderStatus.FILLED);
			order.setLastChangeTime(order.getLastTradeTime());
		} else if ( entry.getStatus() == OrderStatus.CANCELLED ) {
			if ( entry.getQtyRest() == order.getQtyRest() ) {
				order.setStatus(OrderStatus.CANCELLED);
				order.setLastChangeTime(entry.getWithdrawTime());
			}
		} else {
			order.setStatus(OrderStatus.ACTIVE);
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
