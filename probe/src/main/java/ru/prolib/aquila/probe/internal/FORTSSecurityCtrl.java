package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Контроллер инструмента типа фьючерс ФОРТС.
 * <p>
 * Данный компонент разработан с целью обеспечить эмуляцию работы фьючерса
 * секции срочного рынка Московской Биржи. Класс осуществляет первичную
 * инициализацию инструмента, эмуляцию поступления информации о сделках
 * (обезличенные биржевые сделки), исполнения заявок, трекинг открытых позиций и
 * эмуляцию процедуры клиринга по отдельному инструменту. 
 * <p>
 * В соответствии с природой данного дериватива, основная задача ложится на
 * расчет вариационной маржи. Ниже описывается механизм работы применительно к
 * позиции по инструменту в рамках портфеля. То есть, для каждого портфеля
 * ведется раздельный учет позиций по связанному инструменту. Суммарная
 * вариационка по портфелю расчитывается по всем позициям и находится вне зоны
 * ответственности данного класса.
 * <p>
 * Расчет вариационки по позиции выполняется следующим образом. При открытии
 * позиции на балансовую стоимость записывается стоимость исполнения заявки
 * (то есть суммарная себестоимость). При открытии длинной позиции на баланс
 * зачитывается положительная сумма, а при открытии короткой - отрицательная.
 * Таким образом, вариационку можно рассчитать в любое время. Для этого
 * достаточно рассчитать текущую рыночную стоимость чистой позиции и найти
 * разницу между текущей рыночной и балансовой и просуммировать с накопленной
 * вариационкой по закрытым позициям.
 * <p>
 * На протяжении торговой сессии балансовая стоимость позиции может изменяться в
 * результате изменения позиции. Если сделка приводит к увеличению позиции, то к
 * текущей балансовой стоимости добавляется стоимость сделки (меняя таким
 * образом среднюю балансовую). При сокращении позиции с балансовой стоимости
 * списывается по средней пропорционально количеству в сделке. При этом разница
 * между рыночной в моменте и балансовой суммируется к накопленной
 * вариационной марже. Если сделка приводит к развороту позиции, то сначала
 * выполняется закрытие по части сделки в объеме открытом в текущем направлении,
 * а затем на оставшийся объем открывается позиция в противоположном направлении
 * с зачетом остатка на баланс по цене, указанной в сделке.
 * <p>
 * В момент клиринга осуществляется перенос позиций. Перед перерасчетом
 * атрибутов инструмента, на баланс портфеля возвращается ГО и накопленная за
 * сессию вариационка. Далее перерасчитываются атрибуты инструмента
 * (расчетная цена, стоимость шага цены, и т.п.). После этого балансовая
 * стоимость открытых позиций перерасчитывается исходя из размера позы,
 * последеней цены (расчетной) и стоимости шага цены. Накопленная вариационка
 * сбрасывается в ноль.
 * <p>
 * После клиринга необходимо пересчитывать портфель. Что бы не отягощать
 * контроллер дополнительными знаниями о глобальных процедурах и других
 * контроллерах, требование о пересчете портфеля инициируется вызовом
 * специального метода провайдера данных.
 * <p>
 */
public class FORTSSecurityCtrl implements TickHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FORTSSecurityCtrl.class);
	}
	
	public static class EveningClearing implements Runnable {
		private final FORTSSecurityCtrl ctrl;
		
		public EveningClearing(FORTSSecurityCtrl ctrl) {
			super();
			this.ctrl = ctrl;
		}

		@Override
		public void run() {
			ctrl.eveningClearing();
		}
		
		@Override
		public String toString() {
			return ctrl.toString() + "." + getClass().getSimpleName();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != getClass() ) {
				return false;
			}
			EveningClearing o = (EveningClearing) other;
			return ctrl == o.ctrl;
		}
		
	}
	
	public static class ForTick implements Runnable {
		private final FORTSSecurityCtrl ctrl;
		private final Tick data;
		
		public ForTick(FORTSSecurityCtrl ctrl, Tick data) {
			super();
			this.ctrl = ctrl;
			this.data = data;
		}

		@Override
		public void run() {
			ctrl.onTick(data);
		}
		
		@Override
		public String toString() {
			return ctrl.toString()
					+ "." + getClass().getSimpleName() + "{" + data + "}";
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != getClass() ) {
				return false;
			}
			ForTick o = (ForTick) other;
			return new EqualsBuilder()
				.append(ctrl, o.ctrl)
				.append(data, o.data)
				.isEquals();
		}
		
	}
	
	private final BMUtils bmut = new BMUtils();
	private final PROBETerminal terminal;
	private final EditableSecurity security;
	private final SecurityProperties props;
	
	public FORTSSecurityCtrl(PROBETerminal terminal,
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
		terminal.fireEvents(security);
		if ( logger.isDebugEnabled() ) {
			logger.debug("Initialize security: {}", security.getDescriptor());
		}
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
		DateTime dummy = nextDateTick.getTime(),
				x = dummy.withTime(18, 55, 0, 0);
		if ( ! dummy.isBefore(x) ) x = x.plusDays(1);
		terminal.schedule(new EveningClearing(this), x);
	}
	
	@Override
	public Runnable createTask(final Tick tick) {
		return new ForTick(this, tick);
	}

	/**
	 * Симуляция вечернего клиринга.
	 * <p>
	 */
	public void eveningClearing() {
		// TODO: вернуть ГО на баланс
		// TODO: пересчитать вариационку и перевести ее на баланс
		security.setClosePrice(security.getLastPrice());
		security.setInitialPrice(security.getLastPrice());
		security.setInitialMargin(calculateInitialMargin());
		security.setMinStepPrice(calculateMinStepPrice());
		// TODO: списать ГО с баланса
		// TODO: пересчитать балансовую стоимость
		// TODO: запросить пересчет портфеля 
	}
	
	/**
	 * Обработчик тика.
	 * <p>
	 * @param tick тик данных
	 */
	public void onTick(Tick tick) {
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
		security.setLastPrice(price);
		security.fireTradeEvent(bmut.tradeFromTick(tick, security));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FORTSSecurityCtrl.class ) {
			return false;
		}
		FORTSSecurityCtrl o = (FORTSSecurityCtrl) other;
		return new EqualsBuilder()
			.append(o.security, security)
			.append(o.terminal, terminal)
			.append(o.props, props)
			.isEquals();
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public PROBETerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить инструмент.
	 * <p>
	 * @return инструмент
	 */
	public EditableSecurity getSecurity() {
		return security;
	}
	
	/**
	 * Получить первичные свойства инструмента.
	 * <p>
	 * @return свойства инструмента
	 */
	public SecurityProperties getSecurityProperties() {
		return props;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + security.getDescriptor() + "}";
	}

}
