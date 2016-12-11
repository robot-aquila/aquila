package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import ru.prolib.aquila.core.*;

/**
 * Interface of editable portfolio.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioImpl extends ObservableStateContainerImpl implements EditablePortfolio {

	private static final int[] TOKENS_FOR_AVAILABILITY = {
		PortfolioField.CURRENCY,
		PortfolioField.BALANCE,
		PortfolioField.EQUITY,
		PortfolioField.PROFIT_AND_LOSS,
		PortfolioField.USED_MARGIN,
		PortfolioField.FREE_MARGIN
	};

	private final Account account;
	private EditableTerminal terminal;
	private final EventType onPositionAvailable, onPositionChange,
		onPositionCurrentPriceChange, onPositionUpdate, onPositionClose;
	private final Map<Symbol, EditablePosition> positions;
	
	private static String getID(Terminal terminal, Account account,
			String suffix)
	{
		return String.format("%s.%s.%s", terminal.getTerminalID(),
				account, suffix);	
	}
	
	private String getID(String suffix) {
		return getID(terminal, account, suffix);
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID(suffix));
	}

	public PortfolioImpl(EditableTerminal terminal, Account account,
			ObservableStateContainerImpl.Controller controller)
	{
		super(terminal.getEventQueue(), getID(terminal, account, "PORTFOLIO"), controller);
		this.terminal = terminal;
		this.account = account;
		positions = new HashMap<Symbol, EditablePosition>();
		onPositionAvailable = newEventType("PORTFOLIO.POSITION_AVAILABLE");
		onPositionChange = newEventType("PORTFOLIO.POSITION_CHANGE");
		onPositionCurrentPriceChange = newEventType("PORTFOLIO.POSITION_PRICE_CHANGE");
		onPositionUpdate = newEventType("PORTFOLIO.POSITION_UPDATE");
		onPositionClose = newEventType("PORTFOLIO.POSITION_CLOSE");
	}
	
	public PortfolioImpl(EditableTerminal terminal, Account account) {
		this(terminal, account, new PortfolioController());
	}
	
	@Override
	public Terminal getTerminal() {
		lock.lock();
		try {
			return terminal;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public Double getBalance() {
		return getDouble(PortfolioField.BALANCE);
	}

	@Override
	public Double getEquity() {
		return getDouble(PortfolioField.EQUITY);
	}

	@Override
	public Double getProfitAndLoss() {
		return getDouble(PortfolioField.PROFIT_AND_LOSS);
	}

	@Override
	public Double getUsedMargin() {
		return getDouble(PortfolioField.USED_MARGIN);
	}

	@Override
	public Double getFreeMargin() {
		return getDouble(PortfolioField.FREE_MARGIN);
	}

	@Override
	public Double getMarginCallLevel() {
		return getDouble(PortfolioField.MARGIN_CALL_AT);
	}

	@Override
	public Double getMarginStopOutLevel() {
		return getDouble(PortfolioField.MARGIN_STOP_OUT_AT);
	}

	@Override
	public Double getAssets() {
		return getDouble(PortfolioField.ASSETS);
	}

	@Override
	public Double getLiabilities() {
		return getDouble(PortfolioField.LIABILITIES);
	}

	@Override
	public String getCurrency() {
		return getString(PortfolioField.CURRENCY);
	}
	
	@Override
	public Double getLeverage() {
		return getDouble(PortfolioField.LEVERAGE);
	}

	@Override
	public int getPositionCount() {
		lock.lock();
		try {
			return positions.size();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Position> getPositions() {
		lock.lock();
		try {
			return new HashSet<Position>(positions.values());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Position getPosition(Symbol symbol) {
		return getEditablePosition(symbol);
	}

	@Override
	public EventType onPositionAvailable() {
		return onPositionAvailable;
	}

	@Override
	public EventType onPositionCurrentPriceChange() {
		return onPositionCurrentPriceChange;
	}

	@Override
	public EventType onPositionChange() {
		return onPositionChange;
	}

	@Override
	public EventType onPositionUpdate() {
		return onPositionUpdate;
	}

	@Override
	public EditablePosition getEditablePosition(Symbol symbol) {
		lock.lock();
		try {
			if ( terminal == null ) {
				throw new IllegalStateException("Portfolio closed");
			}
			EditablePosition position = positions.get(symbol);
			if ( position == null ) {
				position = new PositionImpl(terminal, account, symbol);
				positions.put(symbol, position);
				position.onAvailable().addAlternateType(onPositionAvailable);
				position.onCurrentPriceChange().addAlternateType(onPositionCurrentPriceChange);
				position.onPositionChange().addAlternateType(onPositionChange);
				position.onUpdate().addAlternateType(onPositionUpdate);
				position.onClose().addAlternateType(onPositionClose);
			}
			return position;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			terminal = null;
			onPositionAvailable.removeListeners();
			onPositionAvailable.removeAlternates();
			onPositionChange.removeListeners();
			onPositionChange.removeAlternates();
			onPositionCurrentPriceChange.removeListeners();
			onPositionCurrentPriceChange.removeAlternates();
			onPositionUpdate.removeListeners();
			onPositionUpdate.removeAlternates();
			super.close();
			for ( EditablePosition position : positions.values() ) {
				position.close();
			}
			positions.clear();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected EventFactory createEventFactory() {
		return new PortfolioEventFactory(this);
	}
	
	static class PortfolioController implements ObservableStateContainerImpl.Controller {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container) {
			
		}

		@Override
		public void processAvailable(ObservableStateContainer container) {
			
		}
		
	}
	
	static class PortfolioEventFactory implements EventFactory {
		private final Portfolio portfolio;
		
		PortfolioEventFactory(Portfolio portfolio) {
			super();
			this.portfolio = portfolio;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new PortfolioEvent(type, portfolio);
		}
		
	}

	@Override
	public EventType onPositionClose() {
		return onPositionClose;
	}

}
