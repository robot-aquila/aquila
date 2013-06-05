package ru.prolib.aquila.core.report;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Отчет по трейдам портфеля.
 * <p>
 * Данный класс предназначен для отслеживания трейдов по отдельному портфелю.
 * <p>
 * Данный класс отслеживает ситуацию исключительно по сделкам и не предназначен
 * для анализа сложных ситуаций, таких как изменение позиции по ходу торговой
 * сессии в результате нерыночных операций (например, перевод бумаг с другого
 * счета). На момент запуска сервиса портфель должен быть доступен, а все его
 * позиции отображать актуальную информацию. Информация о позициях используется
 * исключительно в момент запуска для формирования текущих открытых трейдов. Это
 * необходимо, что бы обеспечить правильный "знак" трейдов, расчитываемых по
 * последующим сделкам. В качестве цены открытия имеющихся на момент запуска
 * открытых позиций используется последняя любая доступная цена инструмента (в
 * порядке приоритета): last, (bid + ask) / 2, open, close, (high + low) / 2,
 * (max + min) / 2. В случае, если цену расчитать не удается, то в
 * качестве цены используется значение 0. 
 */
public class PortfolioTrades implements Trades, EventListener {
	private final EditableTrades trades;
	private final Portfolio portfolio;
	private final PortfolioTradesHelper helper;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param trades базовое хранилище отчетов
	 * @param portfolio портфель
	 */
	public PortfolioTrades(EditableTrades trades, Portfolio portfolio) {
		this(trades, portfolio, new PortfolioTradesHelper());
	}
	
	/**
	 * Служеюный конструктор (только для тестов).
	 * <p>
	 * @param trades базовое хранилище отчетов
	 * @param portfolio портфель
	 * @param helper помощник
	 */
	PortfolioTrades(EditableTrades trades, Portfolio portfolio,
			PortfolioTradesHelper helper)
	{
		super();
		this.trades = trades;
		this.portfolio = portfolio;
		this.helper = helper;
	}

	@Override
	public synchronized void start() throws StarterException {
		trades.start();
		for ( Position position : portfolio.getPositions() ) {
			Trade trade = helper.createInitialTrade(position);
			if ( trade != null ) {
				trades.addTrade(trade);
			}
		}
		getTerminal().OnOrderTrade().addListener(this);
	}

	@Override
	public synchronized void stop() throws StarterException {
		getTerminal().OnOrderTrade().removeListener(this);
		trades.stop();
	}

	@Override
	public synchronized int getTradeReportCount() {
		return trades.getTradeReportCount();
	}

	@Override
	public synchronized List<TradeReport> getTradeReports() {
		return trades.getTradeReports();
	}

	@Override
	public synchronized TradeReport getTradeReport(int index) {
		return trades.getTradeReport(index);
	}

	@Override
	public EventType OnEnter() {
		return trades.OnEnter();
	}

	@Override
	public EventType OnExit() {
		return trades.OnExit();
	}

	@Override
	public EventType OnChanged() {
		return trades.OnChanged();
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(getTerminal().OnOrderTrade()) ) {
			OrderTradeEvent e = (OrderTradeEvent) event;
			if ( portfolio.getAccount().equals(e.getOrder().getAccount()) ) {
				trades.addTrade(e.getTrade());
			}
		}
	}
	
	/**
	 * Получить терминал портфеля.
	 * <p>
	 * @return терминал
	 */
	private Terminal getTerminal() {
		return portfolio.getTerminal();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioTrades.class ) {
			return false;
		}
		PortfolioTrades o = (PortfolioTrades) other;
		return new EqualsBuilder()
			.append(o.helper, helper)
			.append(o.portfolio, portfolio)
			.append(o.trades, trades)
			.isEquals();
	}
	
	/**
	 * Получить базовое хранилище отчетов.
	 * <p>
	 * @return хранилище отчетов
	 */
	public EditableTrades getTrades() {
		return trades;
	}
	
	/**
	 * Получить портфель.
	 * <p>
	 * @return портфель
	 */
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	/**
	 * Получить экземпляр помощника.
	 * <p>
	 * @return помощник
	 */
	public PortfolioTradesHelper getHelper() {
		return helper;
	}

}
