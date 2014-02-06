package ru.prolib.aquila.ui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * 2012-12-09<br>
 * $Id: PortfoliosTableModel.java 491 2013-02-05 20:31:41Z huan.kaktus $
 */
public class PortfoliosTableModel extends AbstractTableModel
	implements EventListener, Starter
{
	private static final long serialVersionUID = -5756493335060286251L;
	private static Logger logger = LoggerFactory.getLogger(PortfolioDataPanel.class);
	private static final String COL_ACCOUNT = "Account";
	private static final String COL_CASH = "Cash";
	private static final String[] header = {
		COL_ACCOUNT,
		COL_CASH,
	};
	
	private final Terminal portfolios;
	private final List<Account> list;
	
	public PortfoliosTableModel(Terminal portfolios) {
		super();
		this.portfolios = portfolios;
		this.list = new Vector<Account>();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}
	
	@Override
	public String getColumnName(int col) {
		return header[col];
	}

	@Override
	public int getRowCount() {
		return portfolios.getPortfolios().size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		try {
			Portfolio portfolio = portfolios.getPortfolio(list.get(row));
			if ( header[col] == COL_ACCOUNT ) {
				return portfolio.getAccount();
			} else if ( header[col] == COL_CASH ) {
				return portfolio.getCash();
			} else {
				return null;
			}
		} catch (PortfolioException e) {
			logger.error("PortfolioException: ", e);
			return null;
		}
	}

	@Override
	public void start() {
		try {
			for ( int i = 0; i < list.size(); i ++ ) {
				portfolios.getPortfolio(list.get(i)).OnChanged().addListener(this);
			}
			portfolios.OnPortfolioAvailable().addListener(this);
		} catch(PortfolioException e) {
			logger.error("PortfolioException: ", e);
		}
	}

	@Override
	public void stop() {
		try {
			portfolios.OnPortfolioAvailable().removeListener(this);
			for ( int i = 0; i < list.size(); i ++ ) {
				portfolios.getPortfolio(list.get(i))
					.OnChanged().removeListener(this);
			}
		} catch(PortfolioException e) {
			logger.error("PortfolioException: ", e);
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(portfolios.OnPortfolioAvailable()) ) {
			PortfolioEvent e = (PortfolioEvent) event;
			Portfolio portfolio = e.getPortfolio();
			int rowIndex = list.size();
			list.add(portfolio.getAccount());
			portfolio.OnChanged().addListener(this);
			fireTableRowsInserted(rowIndex, rowIndex);
		} else if ( event instanceof PortfolioEvent ) {
			PortfolioEvent e = (PortfolioEvent) event;
			Portfolio portfolio = e.getPortfolio();
			if ( event.isType(portfolio.OnChanged()) ) {
				int rowIndex = list.indexOf(portfolio.getAccount());
				fireTableRowsUpdated(rowIndex, rowIndex);
			}
		}
	}

}
