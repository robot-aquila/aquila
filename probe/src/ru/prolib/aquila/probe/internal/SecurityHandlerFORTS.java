package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Обработчик инструмента типа фьючерс ФОРТС.
 * <p>
 */
public class SecurityHandlerFORTS implements TickHandler {
	private final PROBETerminal terminal;
	private final EditableSecurity security;
	private final SecurityProperties props;
	
	public SecurityHandlerFORTS(PROBETerminal terminal,
			EditableSecurity security, SecurityProperties props)
	{
		super();
		this.terminal = terminal;
		this.security = security;
		this.props = props;
	}

	@Override
	public void doInitialTask(Tick firstTick) {
		security.setDisplayName(props.getDisplayName());
		security.setLotSize(props.getLotSize());
		security.setPrecision(props.getPricePrecision());
		security.setMinStepSize(props.getMinStepSize());
		double price = firstTick.getValue();
		security.setInitialPrice(price);
		security.setMinStepPrice(calculateMinStepPrice());
		security.setInitialMargin(calculateInitialMargin());
		security.setOpenPrice(price);
		security.setHighPrice(price);
		security.setLowPrice(price);
		security.setClosePrice(price);
	}
	
	/**
	 * Расчет стоимости минимального шага цены.
	 * <p>
	 * @return стоимость шага цены
	 */
	private Double calculateMinStepPrice() {
		return 1d; // TODO: 
	}
	
	/**
	 * Расчет стоимости начальной маржи.
	 * <p>
	 * @return начальная маржа
	 */
	private Double calculateInitialMargin() {
		return security.getInitialPrice(); // TODO:
	}

	@Override
	public void doFinalTask(Tick lastTick) {

	}

	@Override
	public void doDailyTask(Tick prevDateTick, Tick nextDateTick) {
		terminal.schedule(new Runnable() {
			@Override public void run() { eveningClearing(); }
			}, nextDateTick.getTime().withTime(18, 55, 0, 0));
	}
	
	@Override
	public Runnable createTask(final Tick tick) {
		return new Runnable() {
			@Override public void run() { regularTrade(tick); }
		};
	}

	/**
	 * Вечерний клиринг.
	 * <p>
	 */
	private void eveningClearing() {
		security.setClosePrice(security.getLastPrice());
		security.setInitialPrice(security.getLastPrice());
		security.setInitialMargin(calculateInitialMargin());
		security.setMinStepPrice(calculateMinStepPrice());
	}
	
	/**
	 * Обработчик тика.
	 * <p>
	 * @param tick тик данных
	 */
	private void regularTrade(Tick tick) {
		double price = tick.getValue();
		if ( security.getOpenPrice() == null ) {
			security.setOpenPrice(price);
			security.setHighPrice(price);
			security.setLowPrice(price);
		} else {
			security.setHighPrice(Math.max(security.getHighPrice(), price));
			security.setLowPrice(Math.min(security.getLowPrice(), price));
		}
		security.fireChangedEvent();
		security.fireTradeEvent(new BMUtils().tradeFromTick(tick, security));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityHandlerFORTS.class ) {
			return false;
		}
		SecurityHandlerFORTS o = (SecurityHandlerFORTS) other;
		return new EqualsBuilder()
			.appendSuper(o.security == security)
			.isEquals();
	}

}
