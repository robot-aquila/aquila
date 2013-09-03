package ru.prolib.aquila.quik.assembler;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;

/**
 * Функции сборки объектов модели.
 * <p>
 * Данный класс представляет собой набор функций уровня отдельных объектов.
 */
public class AssemblerL2 {
	private static Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerL2.class);
	}
	
	private final QUIKEditableTerminal terminal;
	
	AssemblerL2(QUIKEditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	/**
	 * Собрать портфель.
	 * <p>
	 * @param entry кэш-запись портфеля
	 * @return всегда true
	 */
	public boolean tryAssemble(PortfolioEntry entry) {
		EditablePortfolio p = terminal.getEditablePortfolio(entry.getAccount());
		synchronized ( p ) {
			p.setBalance(entry.getBalance());
			p.setCash(entry.getCash());
			p.setVariationMargin(entry.getVarMargin());
			terminal.fireEvents(p);
		}
		return true;
	}
	
	/**
	 * Выполнить попытку сборки позиции.
	 * <p>
	 * Позиция может быть собрана только в случае, если можно определить
	 * соответствующий позиции инструмент. Если кэш дескрипторов не содержит
	 * соответствующего дескриптора или инструмент отсутствует в хранилище
	 * инструментов, то согласование объекта позиции не выполняется.
	 * <p>
	 * Результат работы данного метода рассматривается как признак необходимости
	 * сохранить данные в кэше для дальнейшей обработки. Если метод возвращает
	 * true, значит данные были обработаны и сохранять их в кэше не нужно.
	 * В случае возврата false, данные должны быть сохранены в кэше, что бы
	 * выполнить согласование позиции позже.
	 * <p>
	 * @param entry кэш-запись позиции
	 * @return true - данные были применены, false - данные не согласованы
	 */
	public boolean tryAssemble(PositionEntry entry) {
		SecurityDescriptor descr = terminal.getDataCache()
			.getDescriptor(entry.getSecurityShortName());
		if ( descr == null ) {
			return false;
		}

		Security security;
		try {
			security = terminal.getSecurity(descr);
		} catch ( SecurityException e ) {
			logger.error("Unexpected exception: ", e);
			return false;
		}

		EditablePortfolio p = terminal.getEditablePortfolio(entry.getAccount());
		EditablePosition pos = p.getEditablePosition(security);
		synchronized ( pos ) {
			pos.setOpenQty(entry.getOpenQty());
			pos.setCurrQty(entry.getCurrentQty());
			pos.setVarMargin(entry.getVarMargin());
			p.fireEvents(pos);
		}
		return true;
	}
	
	/**
	 * Выполнить сборку инструмента.
	 * <p>
	 * @param entry кэш-запись инструмента
	 * @return всегда true
	 */
	public boolean tryAssemble(SecurityEntry entry) {
		EditableSecurity security;
		security = terminal.getEditableSecurity(entry.getDescriptor());
		synchronized ( security ) {
			security.setLotSize(entry.getLotSize());
			security.setMaxPrice(entry.getMaxPrice());
			security.setMinPrice(entry.getMinPrice());
			security.setMinStepPrice(entry.getMinStepPrice());
			security.setMinStepSize(entry.getMinStepSize());
			security.setPrecision(entry.getPrecision());
			security.setLastPrice(entry.getLastPrice());
			security.setOpenPrice(entry.getOpenPrice());
			security.setClosePrice(entry.getClosePrice());
			security.setDisplayName(entry.getDisplayName());
			security.setAskPrice(entry.getAskPrice());
			security.setBidPrice(entry.getBidPrice());
			security.setHighPrice(entry.getHighPrice());
			security.setLowPrice(entry.getLowPrice());
			terminal.fireEvents(security);
		}
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerL2.class ) {
			return false;
		}
		AssemblerL2 o = (AssemblerL2) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}
	
	/**
	 * Выполнить обработку блока сделок.
	 * <p>
	 * @param entry блок сделок
	 * @return true - блок обработан полностью и можно приступить к обработке
	 * следующего блока, false - блок обработан не полностью из-за недостатка
	 * данных.
	 */
	public boolean tryAssemble(TradesEntry entry) {
		try {
			do {
				Trade trade = entry.access(terminal);
				// Пустая сделка указывает на недоступность дескриптора. Здесь
				// это возможно, когда вызов метода является реакцией на
				// кэширование блока сделок. Просто ждем, когда прибудет
				// дескриптор.
				if ( trade == null ) {
					return false;
				}
				terminal.getEditableSecurity(trade.getSecurityDescriptor())
					.fireTradeEvent(trade);
			} while ( entry.next() );
		} catch ( RowException e ) {
			Object args[] = { entry.count(), e };
			logger.error("Error trade access. {} trades was lost", args);
		}
		return true;
	}
	
	/**
	 * Попытка получить локальный экземпляр заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return локальная заявка или null, если нет соответствующей заявки или
	 * заявка в финальном статуса
	 */
	public EditableOrder tryGetOrder(T2QOrder entry) {
		EditableOrder order;
		int id = (int) entry.getTransId();
		if ( ! terminal.isOrderExists(id) ) {
			return null;
		}
		try {
			order = terminal.getEditableOrder(id);
		} catch ( OrderException e ) {
			Object args[] = { id, e };
			logger.error("Error get order #{}: ", args);
			return null;
		}
		if ( order.getStatus().isFinal() ) {
			return null;
		}
		return order;
	}
	
	/**
	 * Согласовать сделку заявки.
	 * <p>
	 * @param order заявка
	 * @param entry кэш-запись сделки
	 */
	public void tryAssemble(EditableOrder order, T2QTrade entry) {
		if ( order.hasTrade(entry.getId()) ) {
			return;
		}
		Trade trade = new Trade(terminal);
		trade.setDirection(order.getDirection());
		trade.setId(entry.getId());
		trade.setOrderId((long) order.getId());
		trade.setPrice(entry.getPrice());
		trade.setQty(entry.getQty());
		trade.setSecurityDescriptor(order.getSecurityDescriptor());
		trade.setTime(getTime(entry));
		trade.setVolume(entry.getValue());
		order.addTrade(trade);
		order.fireTradeEvent(trade);
		logger.debug("Trade event fired: {}", trade);
		
	}
	
	private Date getTime(T2QTrade trade) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		
		int part = (int) trade.getDate();
		c.set(Calendar.DAY_OF_MONTH, part % 100);
		part /= 100;
		c.set(Calendar.MONTH, (part % 100) - 1);
		c.set(Calendar.YEAR, part / 100);
		
		part = (int) trade.getTime();
		c.set(Calendar.SECOND, part % 100);
		part /= 100;
		c.set(Calendar.MINUTE, part % 100);
		c.set(Calendar.HOUR_OF_DAY, part / 100);
		
		return c.getTime();
	}

	/**
	 * Активировать заявку.
	 * <p>
	 * Выполняет активацию заявки, если она в промежуточном статусе.
	 * <p>
	 * @param order заявка
	 */
	public void tryActivate(EditableOrder order) {
		if ( order.getStatus() == OrderStatus.SENT ) {
			order.setStatus(OrderStatus.ACTIVE);
			terminal.fireEvents(order);
			logger.debug("Order activated: {}", order.getId());
		}
	}
	
	/**
	 * Выполнить попытку финализации заявки.
	 * <p>
	 * Финализация заявки должна выполняться после согласования состояния
	 * заявки по сделкам, в результате которого расчитывается значение
	 * неисполненного остатка. Заявка переводится в статус
	 * {@link OrderStatus#FILLED} в случае, если неисполненный остаток заявки
	 * равен нулю. Если кэш-запись заявки указывает на отмененную заявку,
	 * то выполняется сравнение неисполненного остатка по локальной заявке
	 * и баланса кэш-записи. Если эти значения совпадают, то заявка переводится
	 * в статус {@link OrderStatus#CANCELLED}. 
	 * <p>
	 * @param order заявка
	 * @param entry кэш-запись заявки
	 * @return true - заявка финализирована, false - заявка не финализирована
	 */
	public boolean tryFinalize(EditableOrder order, T2QOrder entry) {
		long rest = order.getQtyRest();
		OrderStatus newStatus = null;
		if ( rest == 0L ) {
			newStatus = OrderStatus.FILLED;
			logger.debug("Set order filled: {}", order.getId());
		} else if ( entry.getStatus() == 2 && rest == entry.getBalance() ) {
			newStatus = OrderStatus.CANCELLED;
			logger.debug("Set order cancelled: {}", order.getId());
		}
		if ( newStatus != null ) {
			order.setStatus(newStatus);
			order.setLastChangeTime(terminal.getCurrentTime());
			terminal.fireEvents(order);
			return true;
		} else {
			return false;
		}
	}

}
