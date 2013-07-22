package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Редактируемый портфель.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioImpl extends EditableImpl implements EditablePortfolio {
	public static final int VERSION = 0x03;
	private final Terminal terminal;
	private final Account account;
	private EditablePositions positions;
	private final EventDispatcher dispatcher;
	private final EventType onChanged;
	private Double variationMargin, cash, balance;

	/**
	 * Создать объект портфеля.
	 * <p>
	 * @param terminal терминал
	 * @param account идентификатор портфеля
	 * @param dispatcher диспетчер событий
	 * @param onChanged тип события
	 */
	public PortfolioImpl(Terminal terminal, Account account,
						 EventDispatcher dispatcher,
						 EventType onChanged)
	{
		super();
		this.terminal = terminal;
		this.account = account;
		this.dispatcher = dispatcher;
		this.onChanged = onChanged;
	}
	
	/**
	 * Установить набор позиций.
	 * <p>
	 * Между набором позиций и портфелем есть двунаправленная зависимость связь.
	 * Для инстанцирования набора позиций требуется экземпляр портфеля. Какой-то
	 * из этих двух объектов должен быть создан не зависимо. В данной реализации
	 * под таким объектом подразумевается портфель, который создается без
	 * определенного набора позиций. То есть, портфель не требует передачи
	 * набора позиций в конструктор. Вместо этого, набор позиций должен быть
	 * установлен в любое время после инстанцирования портфеля через вызов
	 * данного метода. 
	 * <p>
	 * @param positions набор позциий
	 */
	public synchronized void setPositionsInstance(EditablePositions positions) {
		this.positions = positions;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public Account getAccount() {
		return account;
	}
	
	/**
	 * Получить набор позиций.
	 * <p>
	 * @return набор позиций
	 */
	public synchronized EditablePositions getPositionsInstance() {
		return positions;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized Double getVariationMargin() {
		return variationMargin;
	}

	@Override
	public synchronized Double getCash() {
		return cash;
	}

	@Override
	public synchronized List<Position> getPositions() {
		return positions.getPositions();
	}

	@Override
	public void fireChangedEvent() {
		dispatcher.dispatch(new PortfolioEvent(onChanged, this));
	}

	@Override
	public synchronized void setVariationMargin(Double margin) {
		if ( variationMargin != margin ) {
			variationMargin = margin;
			setChanged();
		}
	}

	@Override
	public synchronized void setCash(Double cash) {
		if ( this.cash != cash ) {
			this.cash = cash;
			setChanged();
		}
	}

	@Override
	public EventType OnChanged() {
		return onChanged;
	}
	
	@Override
	public EventType OnPositionAvailable() {
		return positions.OnPositionAvailable();
	}

	@Override
	public void fireEvents(EditablePosition position) {
		positions.fireEvents(position);
	}

	@Override
	public EventType OnPositionChanged() {
		return positions.OnPositionChanged();
	}

	@Override
	public synchronized int getPositionsCount() {
		return positions.getPositionsCount();
	}

	@Override
	public Position getPosition(Security security) {
		return positions.getPosition(security);
	}

	@Override
	public synchronized Double getBalance() {
		return balance;
	}

	@Override
	public synchronized void setBalance(Double value) {
		if ( value == null ? balance != null : ! value.equals(balance) ) {
			setChanged();
			balance = value;
		}
	}

	@Override
	public synchronized
			EditablePosition getEditablePosition(Security security)
	{
		return positions.getEditablePosition(security);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioImpl.class ) {
			return false;
		}
		PortfolioImpl o = (PortfolioImpl) other;
		return new EqualsBuilder()
			.append(o.isAvailable(), isAvailable())
			.append(o.account, account)
			.append(o.balance, balance)
			.append(o.cash, cash)
			.append(o.dispatcher, dispatcher)
			.append(o.onChanged, onChanged)
			.append(o.positions, positions)
			.appendSuper(o.terminal == terminal)
			.append(o.variationMargin, variationMargin)
			.isEquals();
	}

}
