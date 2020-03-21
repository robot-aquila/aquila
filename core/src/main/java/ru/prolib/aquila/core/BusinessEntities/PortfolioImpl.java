package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Condition;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParams;

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
	private final ObjectFactory objectFactory;
	private final Condition newPosLocked;
	private long newPosLockedTID;
	
	public PortfolioImpl(PortfolioParams params) {
		super(params);
		this.newPosLocked = lock.newCondition();
		this.terminal = (EditableTerminal) params.getTerminal();
		this.account = params.getAccount();
		this.objectFactory = params.getObjectFactory();
		positions = new HashMap<Symbol, EditablePosition>();
		final String pfx = params.getID() + ".";
		onPositionAvailable = new EventTypeImpl(pfx + "POSITION_AVAILABLE");
		onPositionChange = new EventTypeImpl(pfx + "POSITION_CHANGE");
		onPositionCurrentPriceChange = new EventTypeImpl(pfx + "POSITION_PRICE_CHANGE");
		onPositionUpdate = new EventTypeImpl(pfx + "POSITION_UPDATE");
		onPositionClose = new EventTypeImpl(pfx + "POSITION_CLOSE");
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
	public CDecimal getBalance() {
		return getCDecimal(PortfolioField.BALANCE);
	}

	@Override
	public CDecimal getEquity() {
		return getCDecimal(PortfolioField.EQUITY);
	}

	@Override
	public CDecimal getProfitAndLoss() {
		return getCDecimal(PortfolioField.PROFIT_AND_LOSS);
	}

	@Override
	public CDecimal getUsedMargin() {
		return getCDecimal(PortfolioField.USED_MARGIN);
	}

	@Override
	public CDecimal getFreeMargin() {
		return getCDecimal(PortfolioField.FREE_MARGIN);
	}

	@Override
	public CDecimal getMarginCallLevel() {
		return getCDecimal(PortfolioField.MARGIN_CALL_AT);
	}

	@Override
	public CDecimal getMarginStopOutLevel() {
		return getCDecimal(PortfolioField.MARGIN_STOP_OUT_AT);
	}

	@Override
	public CDecimal getAssets() {
		return getCDecimal(PortfolioField.ASSETS);
	}

	@Override
	public CDecimal getLiabilities() {
		return getCDecimal(PortfolioField.LIABILITIES);
	}

	@Override
	public String getCurrency() {
		return getString(PortfolioField.CURRENCY);
	}
	
	@Override
	public CDecimal getLeverage() {
		return getCDecimal(PortfolioField.LEVERAGE);
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
			if ( ! positions.containsKey(symbol) ) {
				while ( newPosLockedTID != 0L ) {
					if ( newPosLockedTID == Thread.currentThread().getId() ) {
						unlockNewPositions();
						throw new IllegalStateException("New positions are locked by this thread");
					}
					newPosLocked.await();
					// TODO: Здесь можно написать более надежно. Например, если
					// блокирующий поток был прибит, то текущая реализация будет
					// висеть вечно. Можно переписать на await с аргументами и
					// периодически проверять, жив ли блокирующий поток. Если нет,
					// то снимать блокировку здесь.
				}
			}
			EditablePosition position = positions.get(symbol);
			if ( position == null ) {
				position = objectFactory.createPosition(terminal, account, symbol);
				positions.put(symbol, position);
				position.onAvailable().addAlternateType(onPositionAvailable);
				position.onCurrentPriceChange().addAlternateType(onPositionCurrentPriceChange);
				position.onPositionChange().addAlternateType(onPositionChange);
				position.onUpdate().addAlternateType(onPositionUpdate);
				position.onClose().addAlternateType(onPositionClose);
			}
			return position;
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Unexpected interruption: ", e);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void close() {
		super.close();
		List<EditablePosition> list = new LinkedList<>();
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
			for ( EditablePosition position : positions.values() ) {
				list.add(position);
			}
			positions.clear();
		} finally {
			lock.unlock();
		}
		for ( EditablePosition position : list ) {
			position.close();
		}
	}
	
	@Override
	protected EventFactory createEventFactory(Instant time) {
		return new PortfolioEventFactory(this, time);
	}
	
	@Override
	protected EventFactory createEventFactory(Instant time,
			Map<Integer, Object> old_vals,
			Map<Integer, Object> new_vals)
	{
		return new PortfolioUpdateEventFactory(this, time, old_vals, new_vals);
	}
	
	public static class PortfolioController implements OSCController {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container, Instant time) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container, Instant time) {
			
		}

		@Override
		public void processAvailable(ObservableStateContainer container, Instant time) {
			
		}

		@Override
		public Instant getCurrentTime(ObservableStateContainer container) {
			PortfolioImpl p = (PortfolioImpl) container;
			return p.isClosed() ? null : p.getTerminal().getCurrentTime();
		}
		
	}
	
	static class PortfolioEventFactory implements EventFactory {
		private final Portfolio portfolio;
		protected final Instant time;

		PortfolioEventFactory(Portfolio portfolio, Instant time) {
			super();
			this.portfolio = portfolio;
			this.time = time;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new PortfolioEvent(type, portfolio, time);
		}
		
	}
	
	static class PortfolioUpdateEventFactory extends OSCUpdateEventFactory {
		private final Portfolio portfolio;

		public PortfolioUpdateEventFactory(Portfolio portfolio, Instant time,
				Map<Integer, Object> old_values, Map<Integer, Object> new_values)
		{
			super(portfolio, time, old_values, new_values);
			this.portfolio = portfolio;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new PortfolioUpdateEvent(type, portfolio, time, oldValues, newValues);
		}
		
	}

	@Override
	public EventType onPositionClose() {
		return onPositionClose;
	}
	
	@Override
	public boolean isPositionExists(Symbol symbol) {
		lock.lock();
		try {
			return positions.containsKey(symbol);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void lockNewPositions() {
		lock.lock();
		try {
			long curTID = Thread.currentThread().getId();
			while ( newPosLockedTID != 0L ) {
				if ( newPosLockedTID == curTID ) {
					unlockNewPositions();
					throw new IllegalStateException("New positions already locked by this thread");
				}
				newPosLocked.await();
			}
			newPosLockedTID = curTID;
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Unexpected interruption: ", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void unlockNewPositions() {
		lock.lock();
		try {
			if ( newPosLockedTID != Thread.currentThread().getId() ) {
				throw new IllegalStateException("New positions locked by another thread");
			}
			newPosLockedTID = 0L;
			newPosLocked.signalAll();
		} finally {
			lock.unlock();
		}
	}

}
