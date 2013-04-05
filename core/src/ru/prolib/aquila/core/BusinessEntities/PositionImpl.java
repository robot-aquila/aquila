package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

/**
 * Редактируемая торговая позиция.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionImpl.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImpl extends EditableImpl implements EditablePosition {
	private Account account;
	private final EventDispatcher dispatcher;
	private final EventType etChanged;
	private final SecurityDescriptor descr;
	private final EditableTerminal terminal;
	private long open;
	private long lock;
	private long curr;
	private double variationMargin;
	private Double marketValue = 0.0d, bookValue = 0.0d;
	private PositionType type = PositionType.CLOSE;

	/**
	 * Создать объект позиции.
	 * <p>
	 * @param account счет позиции
	 * @param terminal терминал
	 * @param descr дескриптор инструмента
	 * @param dispatcher диспетчер событий
	 * @param onChanged тип события
	 */
	public PositionImpl(Account account, EditableTerminal terminal,
			SecurityDescriptor descr, EventDispatcher dispatcher,
			EventType onChanged)
	{
		super();
		if ( account == null ) {
			throw new NullPointerException("Account cannot be null");
		}
		if ( descr == null ) {
			throw new NullPointerException("Descriptor cannot be null");
		}
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		if ( dispatcher == null ) {
			throw new NullPointerException("Event dispatcher cannot be null");
		}
		if ( onChanged == null ) {
			throw new NullPointerException("OnChanged type cannot be null");
		}
		this.account = account;
		this.terminal = terminal;
		this.descr = descr;
		this.dispatcher = dispatcher;
		this.etChanged = onChanged;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить используемый диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public Portfolio getPortfolio() throws PortfolioException {
		return terminal.getPortfolio(account);
	}

	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	@Override
	public Security getSecurity() throws SecurityException {
		return terminal.getSecurity(descr);
	}

	@Override
	public EventType OnChanged() {
		return etChanged;
	}

	@Override
	public void fireChangedEvent() {
		dispatcher.dispatch(new PositionEvent(etChanged, this));
	}

	@Override
	public synchronized double getVarMargin() {
		return variationMargin;
	}

	@Override
	public synchronized long getOpenQty() {
		return open;
	}

	@Override
	public synchronized long getLockQty() {
		return lock;
	}

	@Override
	public synchronized long getCurrQty() {
		return curr;
	}

	@Override
	public synchronized void setVarMargin(double margin) {
		if ( variationMargin != margin ) {
			variationMargin = margin;
			setChanged();
		}
	}

	@Override
	public synchronized void setOpenQty(long value) {
		if ( open != value ) {
			open = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setLockQty(long value) {
		if ( lock != value ) {
			lock = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setCurrQty(long value) {
		if ( curr != value ) {
			curr = value;
			if ( curr > 0 ) {
				type = PositionType.LONG;
			} else if ( curr < 0 ) {
				type = PositionType.SHORT;
			} else {
				type = PositionType.CLOSE;
			}
			setChanged();
		}
	}

	@Override
	public synchronized PositionType getType() {
		return type;
	}

	@Override
	public synchronized Double getMarketValue() {
		return marketValue;
	}

	@Override
	public synchronized void setMarketValue(Double value) {
		if ( value== null ? marketValue != null : !value.equals(marketValue) ) {
			marketValue = value;
			setChanged();
		}
	}

	@Override
	public synchronized Double getBookValue() {
		return bookValue;
	}

	@Override
	public synchronized void setBookValue(Double value) {
		if ( value== null ? bookValue != null : !value.equals(bookValue) ) {
			bookValue = value;
			setChanged();
		}
	}

	@Override
	public synchronized Account getAccount() {
		return account;
	}

	@Override
	public synchronized void setAccount(Account account) {
		if ( account == null ? this.account != null
				: ! account.equals(this.account) )
		{
			this.account = account;
			setChanged();
		}
	}

}
