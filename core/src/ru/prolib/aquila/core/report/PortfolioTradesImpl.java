package ru.prolib.aquila.core.report;

import java.util.List;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Position;

/**
 * Отчет по трейдам портфеля.
 * <p>
 * Данный класс предназначен для отслеживания трейдов по отдельному портфелю - 
 * последовательностей сделок, приводящих к открытию и последующему закрытию
 * позиции.
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
 * порядке приоритета): last, (bid + ask) / 2, close, (high + low) / 2,
 * (max + min) / 2, open. В случае, если цену расчитать не удается, то в
 * качестве цены используется значение 0.   
 */
public class PortfolioTradesImpl implements Trades {

	@Override
	public void start() throws StarterException {
		/*
		getTerminal().OnOrderTrade().addListener(this);
		for ( Position position : portfolio.getPositions() ) {
			if ( position.getCurrQty() != 0 ) {
				activeReports.addTrade(helper.createTrade(position));
			}
		}
		*/
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws StarterException {
		/*
		getTerminal().OnOrderTrade().removeListener(this);
		*/
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTradeReportCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<TradeReport> getTradeReports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TradeReport getTradeReport(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventType OnEnter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventType OnExit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventType OnChanged() {
		// TODO Auto-generated method stub
		return null;
	}

}
