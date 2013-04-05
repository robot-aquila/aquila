package ru.prolib.aquila.ui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;


/**
 * $Id: PositionsTableModel.java 544 2013-02-25 14:31:32Z huan.kaktus $
 */
public class PositionsTableModel extends AbstractTableModel implements
		EventListener, Starter 
{
	private static final long serialVersionUID = 8731892367809895323L;
	private static Logger logger = LoggerFactory.getLogger(PositionsTableModel.class);
	private static final String COL_ACCOUNT = "COL_ACCOUNT";
	private static final String COL_SEC = "COL_SEC";
	private static final String COL_TYPE = "COL_TYPE";
	private static final String COL_CURR_VAL = "COL_CURR_VAL";
	private static final String COL_MARKET = "COL_MARKET";
	private static final String COL_LOCK_VAL = "COL_LOCK_VAL";
	private static final String COL_VAR_MARG = "COL_VAR_MARG";
	private static final String COL_OPEN_VAL = "COL_OPEN_VAL.";
	private static final String COL_BALANCE = "COL_BALANCE";
	
	private static final String[] header = {
		COL_ACCOUNT,
		COL_SEC,
		COL_TYPE,
		COL_CURR_VAL,
		COL_MARKET,
		COL_LOCK_VAL,
		COL_VAR_MARG,
		COL_OPEN_VAL,
		COL_BALANCE,
	};
	private final ClassLabels uiLabels;
	private final Portfolios portfolios;
	private final List<Position> list;
	
	public PositionsTableModel(Portfolios portfolios, UiTexts uiTexts) {
		super();
		uiLabels = uiTexts.get("PositionsTableModel");
		this.portfolios = portfolios;
		this.list = new Vector<Position>();
	}
	
	@Override
	public void start() {
		portfolios.OnPositionAvailable().addListener(this);
		portfolios.OnPositionChanged().addListener(this);

	}

	@Override
	public void stop() {
		portfolios.OnPositionAvailable().removeListener(this);
		portfolios.OnPositionChanged().removeListener(this);
	}

	@Override
	public Object getValueAt(int row, int col) {			
		Position p = list.get(row);
		if(header[col] == COL_ACCOUNT) {
			return p.getAccount();
		}else if( header[col] == COL_SEC ) {			
			try {
				return p.getSecurity().getDescriptor();
			}catch (SecurityException e) {
				logger.error("SecurityException: ", e);
				return "";
			}
		} else if ( header[col] == COL_VAR_MARG ) {
			return p.getVarMargin();
		} else if (header[col] == COL_OPEN_VAL ) {
			return p.getOpenQty();
		} else if ( header[col] == COL_LOCK_VAL ) {
			return p.getLockQty();
		} else if ( header[col] == COL_CURR_VAL ) {
			return p.getCurrQty();
		} else if ( header[col] == COL_TYPE ) {
			return p.getType();
		} else if ( header[col] == COL_MARKET ) {
			return p.getMarketValue();
		} else if ( header[col] == COL_BALANCE ) {
			return p.getBookValue();
		} else {
			return null;
		}
	}

	@Override
	public void onEvent(Event event) {
		final PositionEvent e = (PositionEvent) event;
		
		if ( event.isType(portfolios.OnPositionAvailable()) ) {
			insertNewRow(e);			
		} else if ( event.isType(portfolios.OnPositionChanged()) ) {
			updateRow(e);
		}
		
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}
	
	@Override
	public String getColumnName(int col) {
		return uiLabels.get(header[col]);
	}

	@Override
	public int getRowCount() {
		return list.size();
	}
	
	private void updateRow(PositionEvent e) {
		Position position = e.getPosition();
		int rowIndex = list.indexOf(position);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	private void insertNewRow(PositionEvent e) {
		Position position = e.getPosition();
		int rowIndex = list.size();
		list.add(position);
		fireTableRowsInserted(rowIndex, rowIndex);
	}
}
