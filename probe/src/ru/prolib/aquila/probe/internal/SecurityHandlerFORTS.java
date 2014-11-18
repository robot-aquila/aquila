package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Обработчик инструмента в рамках эмулятора.
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
		// Здесь устанавливаем только первичные атрибуты.
		security.setDisplayName(props.getDisplayName());
		security.setLotSize(props.getLotSize());
		security.setPrecision(props.getPricePrecision());
		security.setMinStepSize(props.getMinStepSize());
		security.setInitialPrice(firstTick.getValue());
		// В первый раз запрашиваем расчет стоимости шага явно.
		// В последующем, это будет выполняться в момент клиринга, планирование
		// которого осуществляется на следующих этапах обработки данного тика.
		security.setMinStepPrice(calculateMinStepPrice());
		// Тоже самое с начальной маржой. 
		security.setInitialMargin(calculateInitialMargin());
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
			@Override public void run() { eveningClearing(); } },
			nextDateTick.getTime()
				.withHourOfDay(18)
				.withMinuteOfHour(50)
				.withSecondOfMinute(0)
				.withMillisOfDay(0));
	}
	
	@Override
	public Runnable createTask(final Tick tick) {
		return new Runnable() {
			@Override public void run() { regularTick(tick); }
		};
	}

	/**
	 * Вечерний клиринг.
	 * <p>
	 */
	private void eveningClearing() {
		
	}
	
	/**
	 * Обработчик тика.
	 * <p>
	 * @param tick тик данных
	 */
	private void regularTick(Tick tick) {
		double price = tick.getValue();
		Trade t = new Trade(terminal);
		t.setDirection(Direction.BUY);
		t.setPrice(price);
		t.setQty(tick.getOptionalValueAsLong());
		t.setSecurityDescriptor(security.getDescriptor());
		t.setTime(tick.getTime());
		t.setVolume(price / security.getMinStepSize() *
				security.getMinStepPrice() * tick.getOptionalValueAsLong());
		security.fireTradeEvent(t);
		security.setHighPrice(Math.max(security.getHighPrice(), price));
		security.setLowPrice(Math.min(security.getLowPrice(), price));
		security.fireChangedEvent();
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
