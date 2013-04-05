package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import ru.prolib.aquila.core.*;

/**
 * Редактируемый портфель.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioImpl extends EditableImpl implements EditablePortfolio {
	public static final int VERSION = 0x02;
	private final Terminal terminal;
	private final Account account;
	private final EditablePositions positions;
	private final EventDispatcher dispatcher;
	private final EventType onChanged;
	private Double variationMargin,cash,balance;

	/**
	 * Создать объект портфеля.
	 * <p>
	 * @param terminal терминал
	 * @param account идентификатор портфеля
	 * @param positions набор позиций
	 * @param dispatcher диспетчер событий
	 * @param onChanged тип события
	 */
	public PortfolioImpl(Terminal terminal, Account account,
						 EditablePositions positions,
						 EventDispatcher dispatcher,
						 EventType onChanged)
	{
		super();
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		if ( account == null ) {
			throw new NullPointerException("Account cannot be null");
		}
		if ( positions == null ) {
			throw new NullPointerException("Positions cannot be null");
		}
		if ( dispatcher == null ) {
			throw new NullPointerException("Dispatcher cannot be null");
		}
		if ( onChanged == null ) {
			throw new NullPointerException("Event type cannot be null");
		}
		this.terminal = terminal;
		this.account = account;
		this.positions = positions;
		this.dispatcher = dispatcher;
		this.onChanged = onChanged;
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
	public EditablePositions getPositionsInstance() {
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
	public List<Position> getPositions() {
		return positions.getPositions();
	}

	@Override
	public Position getPosition(SecurityDescriptor descr) {
		return positions.getPosition(descr);
	}

	@Override
	public EventType OnPositionAvailable() {
		return positions.OnPositionAvailable();
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
	public void firePositionAvailableEvent(Position position) {
		positions.firePositionAvailableEvent(position);
	}

	@Override
	public EditablePosition getEditablePosition(SecurityDescriptor descr) {
		return positions.getEditablePosition(descr);
	}

	@Override
	public EventType OnPositionChanged() {
		return positions.OnPositionChanged();
	}

	@Override
	public int getPositionsCount() {
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

}
