package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Этапы согласования объектов.
 * <p>
 * Данный класс содержит методы согласования объектов на отдельных этапах.
 * Фактически, данный класс представляет собой макросы для обработки
 * соответствующих ситуаций, а решение о применении того или иного макроса
 * принимается вызывающим кодом.
 */
public class AssemblerMidLvl {
	/**
	 * Минимальное время жизни заявки в миллисекундах.
	 * <p>
	 * Данный параметр используется в процедуре отмены заявок по удалению
	 * кэш-записи из соответствующего кэша. Так как данные могут приходить
	 * с задержкой (например, сначала ответ на транзакцию, а затем обновление
	 * кэша), безусловная отмена может привести к тому, что заявка будет
	 * локально отменена до того, как поступят данные таблицы.
	 */
	private static final long MIN_ORDER_LIFETIME = 3600000;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerMidLvl.class);
	}
	
	private final EditableTerminal terminal;
	private final Cache cache;
	private final AssemblerLowLvl low;
	
	public AssemblerMidLvl(EditableTerminal terminal, Cache cache,
			AssemblerLowLvl helper)
	{
		super();
		this.terminal = terminal;
		this.cache = cache;
		this.low = helper;
	}
	
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public AssemblerLowLvl getAssemblerLowLevel() {
		return low;
	}
	
	private void error(Throwable t) {
		logger.error("Unexpected exception: ", t);
	}

	/**
	 * Проверить и обработать ситуацию удаления заявки из кэша.
	 * <p>
	 * Заявки из таблицы заявок могут быть удалены до сведения или отмены
	 * (например, перед началом новой сессии). Никакого иного способа определить
	 * эту ситуацию, кроме как контролировать момент исчезновения кэш-записи
	 * соответствующей заявки не существует.
	 * <p>
	 * В связи с этим. Каждая активная заявка должна быть проверена на наличие
	 * соответствующей ей кэш-записи. Если кэш-запись отсутствует, то заявка
	 * должна быть отменена стекущим состоянием. Данный метод выполняет
	 * описанную выше проверку для заявки в любом состоянии. Если в соответствии
	 * с вышеописанными условиями заявка должна быть отменена, выставляется
	 * соответствующий статус заявки и генерируется событие об изменении.
	 * <p>
	 * @param order экземпляр заявки для проверки
	 * @return true - если заявка была отменена, false - заявка не изменилась
	 */
	public boolean checkIfOrderRemoved(EditableOrder order) {
		synchronized ( order ) {
			try {
				if ( order.getStatus() == OrderStatus.ACTIVE
					&& terminal.getCurrentTime().getTime()
						- order.getTime().getTime() > MIN_ORDER_LIFETIME
					&& cache.getOrderCache(order.getId()) == null )
				{
					order.setStatus(OrderStatus.CANCELLED);
					order.setLastChangeTime(terminal.getCurrentTime());
					order.fireChangedEvent();
					order.resetChanges();
					return true;
				} else {
					return false;
				}
			} catch ( EditableObjectException e ) {
				error(e);
				return false;
			}
		}
	}
	
	/**
	 * Проверить и обработать ситуацию удаления стоп-заявки из кэша.
	 * <p>
	 * Работает аналогично методу {@link #checkIfOrderRemoved(EditableOrder)}
	 * только для стоп-заявок.
	 * <p>
	 * @param order экземпляр стоп-заявки для проверки
	 * @return true - если стоп-заявка была отменена, false - нет изменений
	 */
	public boolean checkIfStopOrderRemoved(EditableOrder order) {
		synchronized ( order ) {
			try {
				if ( order.getStatus() == OrderStatus.ACTIVE
					&& terminal.getCurrentTime().getTime()
						- order.getTime().getTime() > MIN_ORDER_LIFETIME
					&& cache.getStopOrderCache(order.getId()) == null )
				{
					order.setStatus(OrderStatus.CANCELLED);
					order.setLastChangeTime(terminal.getCurrentTime());
					order.fireChangedEvent();
					order.resetChanges();
					return true;
				} else {
					return false;			
				}
			} catch ( EditableObjectException e ) {
				error(e);
				return false;
			}
		}
	}
	
	/**
	 * Создать и зарегистрировать новую заявку на основе кэш-записи.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return true - была создана новая заявка, false - заявка не создана
	 */
	public boolean createNewOrder(OrderCache entry) {
		try {
			Account account = low.getAccountByOrderCache(entry);
			SecurityDescriptor descr =
					low.getSecurityDescriptorByOrderCache(entry);
			if ( account == null || descr == null ) {
				return false;
			}
			EditableOrder order = terminal.createOrder();
			order.setDirection(entry.getDirection());
			order.setPrice(entry.getPrice());
			order.setQty(entry.getQty());
			order.setQtyRest(entry.getQty());
			order.setTime(entry.getTime());
			order.setTransactionId(entry.getTransId());
			order.setType(entry.getType());
			order.setAccount(account);
			order.setSecurityDescriptor(descr);
			for ( TradeCache t : cache.getAllTradesByOrderId(entry.getId()) ) {
				low.adjustOrderTrade(t, order);
			}
			low.adjustOrderStatus(entry, order);
			terminal.registerOrder(entry.getId(), order);
			order.setAvailable(true);
			order.resetChanges();
			terminal.fireOrderAvailableEvent(order);
			return true;
		} catch ( EditableObjectException e ) {
			error(e);
			return false;
		}
	}
	
	/**
	 * Создать и зарегистрировать новую стоп-заявку на основе кэш-записи.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return true - была создана новая заявка, false - заявка не создана 
	 */
	public boolean createNewStopOrder(StopOrderCache entry) {
		try {
			Account account = low.getAccountByStopOrderCache(entry);
			SecurityDescriptor descr =
				low.getSecurityDescriptorByStopOrderCache(entry);
			if ( account == null || descr == null ) {
				return false;
			}
			EditableOrder order = terminal.createStopOrder();
			order.setAccount(account);
			order.setDirection(entry.getDirection());
			order.setOffset(entry.getOffset());
			order.setPrice(entry.getPrice());
			order.setQty(entry.getQty());
			order.setSecurityDescriptor(descr);
			order.setSpread(entry.getSpread());
			order.setStopLimitPrice(entry.getStopLimitPrice());
			order.setTakeProfitPrice(entry.getTakeProfitPrice());
			order.setTime(entry.getTime());
			order.setTransactionId(entry.getTransId());
			order.setType(entry.getType());
			low.adjustStopOrderStatus(entry, order);
			terminal.registerStopOrder(entry.getId(), order);
			order.setAvailable(true);
			terminal.fireStopOrderAvailableEvent(order);
			return true;
		} catch ( EditableObjectException e ) {
			error(e);
			return false;
		}
	}
	
	/**
	 * Согласовать состояние существующей заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return true - заявка была обновлена, false - заявка не обновлена
	 */
	public boolean updateExistingOrder(OrderCache entry) {
		try {
			EditableOrder order = terminal.getEditableOrder(entry.getId());
			synchronized ( order ) {
				// Неактивные заявки игнорируются.
				if ( order.getStatus() != OrderStatus.ACTIVE ) {
					return false;
				}
				for (TradeCache t:cache.getAllTradesByOrderId(entry.getId())) {
					low.adjustOrderTrade(t, order);
				}
				low.adjustOrderStatus(entry, order);
				order.fireChangedEvent();
				boolean changed = order.hasChanged();
				order.resetChanges();
				return changed;
			}
		} catch ( EditableObjectException e ) {
			error(e);
			return false;
		}
	}
	
	/**
	 * Согласовать состояние существующей стоп-заявки.
	 * <p>
	 * @param entry кэш-запись стоп-заявки
	 * @return стоп-заявка была обновлена, false - без изменений
	 */
	public boolean updateExistingStopOrder(StopOrderCache entry) {
		try {
			EditableOrder order = terminal.getEditableStopOrder(entry.getId());
			synchronized ( order ) {
				// Неактивные заявки игнорируются.
				if ( order.getStatus() != OrderStatus.ACTIVE ) {
					return false;
				}
				// Непонятно что происходит с параметрами сложного тейк-профита.
				// Надо проверять, но лениво - ситуация редкая...
				// В этой связи обновляем эти атрибуты при каждом согласовании.
				OrderType type = order.getType();
				if ( type == OrderType.TAKE_PROFIT || type == OrderType.TPSL ) {
					order.setTakeProfitPrice(entry.getTakeProfitPrice());
					order.setStopLimitPrice(entry.getStopLimitPrice());
					order.setPrice(entry.getPrice());
				}
				low.adjustStopOrderStatus(entry, order);
				order.fireChangedEvent();
				boolean changed = order.hasChanged();
				order.resetChanges();
				return changed;
			}
		} catch ( EditableObjectException e ) {
			error(e);
			return false;
		}
	}
	
	/**
	 * Собрать портфель по деривативам на основе данных кэш-записи.
	 * <p>
	 * @param entry кэш-запись
	 */
	public void updatePortfolioFORTS(PortfolioFCache entry) {
		try {
			Account account = new Account(entry.getFirmId(),
					entry.getAccountCode(), entry.getAccountCode());
			EditablePortfolio portfolio = null;
			if ( terminal.isPortfolioAvailable(account) ) {
				portfolio = terminal.getEditablePortfolio(account);
			} else {
				portfolio = terminal.createPortfolio(account);
			}
			synchronized ( portfolio ) {
				portfolio.setBalance(entry.getBalance());
				portfolio.setCash(entry.getCash());
				portfolio.setVariationMargin(entry.getVarMargin());
				if ( portfolio.isAvailable() ) {
					portfolio.fireChangedEvent();
				} else {
					cache.registerAccount(account);
					terminal.firePortfolioAvailableEvent(portfolio);
					portfolio.setAvailable(true);
				}
				portfolio.resetChanges();
			}
		} catch ( EditableObjectException e ) {
			error(e);
		}
	}
	
	/**
	 * Собрать инструмент на основе данных кэш-записи.
	 * <p>
	 * @param entry кэш-запись
	 */
	public void updateSecurity(SecurityCache entry) {
		try {
			SecurityDescriptor descr = entry.getDescriptor();
			EditableSecurity security = null;
			if ( terminal.isSecurityExists(descr) ) {
				security = terminal.getEditableSecurity(descr);
			} else {
				security = terminal.createSecurity(descr);
			}
			synchronized ( security ) {
				security.setAskPrice(entry.getAskPrice());
				security.setBidPrice(entry.getBidPrice());
				security.setClosePrice(entry.getClosePrice());
				security.setDisplayName(entry.getDisplayName());
				security.setHighPrice(entry.getHighPrice());
				security.setLastPrice(entry.getLastPrice());
				security.setLotSize(entry.getLotSize());
				security.setLowPrice(entry.getLowPrice());
				security.setMaxPrice(entry.getMaxPrice());
				security.setMinPrice(entry.getMinPrice());
				security.setMinStepPrice(entry.getMinStepPrice());
				security.setMinStepSize(entry.getMinStepSize());
				security.setOpenPrice(entry.getOpenPrice());
				security.setPrecision(entry.getPrecision());
				if ( security.isAvailable() ) {
					security.fireChangedEvent();
				} else {
					cache.registerSecurityDescriptor(descr,
							entry.getShortName());
					terminal.fireSecurityAvailableEvent(security);
					security.setAvailable(true);
				}
				security.resetChanges();
			}
		} catch ( EditableObjectException e ) {
			error(e);
		}
	}
	
	/**
	 * Собрать позицию на основе кэш-записи таблицы позиций по деривативам.
	 * <p>
	 * @param entry кэш-запись
	 */
	public void updatePositionFORTS(PositionFCache entry) {
		try {
			Account account = new Account(entry.getFirmId(),
					entry.getAccountCode(), entry.getAccountCode());
			if ( ! terminal.isPortfolioAvailable(account) ) {
				logger.debug("Still wait for portfolio: {}", account);
				return;
			}
			String secName = entry.getSecurityShortName();
			if ( ! cache.isSecurityDescriptorRegistered(secName) ) {
				logger.debug("Still wait for security by short name: {}",
						secName);
				return;
				
			}
			SecurityDescriptor descr =
				cache.getSecurityDescriptorByName(secName);
			if ( ! terminal.isSecurityExists(descr) ) {
				logger.debug("Still wait for security: {}", descr);
				return;
			}
			EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
			EditablePosition position = portfolio
				.getEditablePosition(terminal.getSecurity(descr));
			synchronized ( position ) {
				position.setCurrQty(entry.getCurrentQty());
				position.setOpenQty(entry.getOpenQty());
				position.setVarMargin(entry.getVarMargin());
				if ( position.isAvailable() ) {
					position.fireChangedEvent();
				} else {
					portfolio.firePositionAvailableEvent(position);
					position.setAvailable(true);
				}
				position.resetChanges();
			}
		} catch ( EditableObjectException e ) {
			error(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerMidLvl.class ) {
			return false;
		}
		AssemblerMidLvl o = (AssemblerMidLvl) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.low, low)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}
