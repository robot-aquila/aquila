package ru.prolib.aquila.quik.assembler;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.dde.*;

/**
 * Низкоуровневые функции согласования объектов.
 */
public class AssemblerLowLvl implements Starter {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final Cache cache;
	private Date startTime;
	
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
	public Account getAccount(OrderCache entry) {
		return getAccount(entry.getClientCode(), entry.getAccountCode(),
				entry.getId());
	}
	
	/**
	 * Проверить доступность портфеля и получить счет.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return торговый счет или null, если не удалось определить счет
	 */
	public Account getAccount(StopOrderCache entry) {
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
	public SecurityDescriptor getSecurityDescriptor(OrderCache entry) {
		return getSecurityDescriptor(entry.getSecurityCode(),
				entry.getSecurityClassCode(), entry.getId());
	}
	
	/**
	 * Проверить доступность инструмента и получить дескриптор.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return дескриптор инструмента или null, если не удалось определить
	 */
	public SecurityDescriptor getSecurityDescriptor(StopOrderCache entry) {
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
		if ( orderStatus != OrderStatus.ACTIVE ) {
			// Все заявки в статусе отличном от ACTIVE игнорируются
			return;
		}
		
		OrderStatus entryStatus = entry.getStatus();
		if ( entryStatus == OrderStatus.CANCELLED ) {
			// Неисполненный остаток отмененной заявки должен быть согласован
			// по сделкам. Только когда неисполненный остаток локальной заявки
			// и кэш записи совпадает, только тогда заявку можно считать
			// согласованной. В этом случае выполняется смена статуса заявки.
			Long rest = order.getQtyRest();
			if ( rest != null && rest == entry.getQtyRest() ) {
				order.setStatus(OrderStatus.CANCELLED);
				order.setLastChangeTime(entry.getWithdrawTime());
			}
			
		} else if ( order.getQtyRest() == 0L ) {
			// Здесь мы будем только если заявка активна а статус по кэшу
			// либо активная, либо исполненная. Проверяем согласованность по
			// сделкам, предварительная обработка которых приводит к выставлению
			// корректного неисполненного остатка.
			order.setStatus(OrderStatus.FILLED);
			order.setLastChangeTime(order.getLastTradeTime());			
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
	public void adjustOrderStatus(StopOrderCache entry, EditableOrder order) {
		Long orderId = entry.getId();
		OrderStatus entryStatus = entry.getStatus();
		if ( entryStatus == OrderStatus.CANCELLED ) {
			logger.debug("Stop-Order {} set cancelled by entry", orderId);
			order.setStatus(entryStatus);
			order.setLastChangeTime(entry.getWithdrawTime());
		} else if ( entryStatus == OrderStatus.FILLED ) {
			Long linkId = entry.getLinkedOrderId();
			if ( linkId == null ) {
				logger.debug("Stop-order {} still wait for linked ID", orderId);
			} else if ( cache.getOrderCache(linkId) != null ) {
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
	 * сделна не ранняя, то так же генерирует событие о новой сделке. Заявка
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
		if ( ! startTime.after(trade.getTime()) ) {
			order.fireTradeEvent(trade);
			logger.debug("Trade event fired: {}", trade);
		} else {
			logger.debug("Trade event skipped: {}", trade);
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
			.append(o.startTime, startTime)
			.isEquals();
	}
	
	/**
	 * Первый этап создания новой заявки.
	 * <p>
	 * Выполняет первичное заполнение атрибутов и регистрацию заявки,
	 * после чего генерирует событие о появлении новой заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @param order экземпляр новой заявки
	 * @throws OrderAlreadyExistsException ошибка регистрации заявки
	 */
	public void initNewOrder(OrderCache entry, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		order.setDirection(entry.getDirection());
		order.setPrice(entry.getPrice());
		order.setQty(entry.getQty());
		order.setQtyRest(entry.getQty());
		order.setTime(entry.getTime());
		order.setTransactionId(entry.getTransId());
		order.setType(entry.getType());
		order.setAvailable(true);
		order.resetChanges();
		terminal.registerOrder(entry.getId(), order);
		terminal.fireOrderAvailableEvent(order);
	}
	
	/**
	 * Генерировать события об изменении заявки.
	 * <p>
	 * Работает одинаково для заявок и стоп-заявок. Выполняется генерация
	 * событий об изменении заявки, если время заявки не раньше времени запуска
	 * сборщика. Подразумевается, что время заявки уже установлено. После
	 * обработки сбрасывает признак изменения заявки
	 * <p>
	 * @param order экземпляр заявки
	 */
	public void fireOrderChanges(EditableOrder order) {
		if ( ! startTime.after(order.getTime()) ) {
			try {
				order.fireChangedEvent();
			} catch ( EditableObjectException e ) {
				// Очень маловероятная ситуация, по этому обрабатываем здесь.
				logger.error("Order changed event suppressed: ", e);
			}
		}
		order.resetChanges();		
	}

	@Override
	public void start() throws StarterException {
		startTime = terminal.getCurrentTime();
	}

	@Override
	public void stop() throws StarterException {
		startTime = null;
	}
	
	/**
	 * Установить время запуска сборщика.
	 * <p>
	 * Служебный метод только для тестов.
	 * <p>
	 * @param time время
	 */
	protected void setStartTime(Date time) {
		startTime = time;
	}
	
	/**
	 * Получить время запуска сборщика.
	 * <p>
	 * Служебный метод только для тестов.
	 * <p>
	 * @return время запуска
	 */
	protected Date getStartTime() {
		return startTime;
	}
	
	/**
	 * Первый этап создания новой стоп-заявки.
	 * <p>
	 * Выполняет первичное заполнение атрибутов и регистрацию стоп-заявки,
	 * после чего генерирует событие о появлении новой стоп-заявки.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @param order экземпляр новой стоп-заявки
	 * @throws OrderAlreadyExistsException ошибка регистрации заявки
	 */
	public void initNewOrder(StopOrderCache entry, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		order.setDirection(entry.getDirection());
		order.setOffset(entry.getOffset());
		order.setPrice(entry.getPrice());
		order.setQty(entry.getQty());
		order.setSpread(entry.getSpread());
		order.setStopLimitPrice(entry.getStopLimitPrice());
		order.setTakeProfitPrice(entry.getTakeProfitPrice());
		order.setTime(entry.getTime());
		order.setTransactionId(entry.getTransId());
		order.setType(entry.getType());
		order.setAvailable(true);
		order.resetChanges();
		terminal.registerStopOrder(entry.getId(), order);
		terminal.fireStopOrderAvailableEvent(order);
	}

}
