package ru.prolib.aquila.ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

/**
 * Модель таблицы отчета по трейдам.
 */
public class TradesReportTableModel extends AbstractTableModel
		implements EventListener, Starter
{
	private static final long serialVersionUID = 676641706428117427L;
	private static final String TEXT_SEC = "TradeReport";
	private static final String COL_TYPE = "COL_TYPE";
	private static final String COL_SEC_DESCR = "COL_SEC_DESCR";
	private static final String COL_QTY = "COL_QTY";
	private static final String COL_UNCOVERED_QTY = "COL_UNCOVERED_QTY";
	private static final String COL_ENTER_TIME = "COL_ENTER_TIME";
	private static final String COL_ENTER_PRICE = "COL_ENTER_PRICE";
	private static final String COL_EXIT_TIME = "COL_EXIT_TIME";
	private static final String COL_EXIT_PRICE = "COL_EXIT_PRICE";
	private static final String COL_ENTER_VOL = "COL_ENTER_VOL";
	private static final String COL_EXIT_VOL = "COL_EXIT_VOL";
	private static final String COL_PROF_LOSS = "COL_PROF_LOSS";
	private static final String COL_PROF_LOSS_PERC = "COL_PROF_LOSS_PERC";
	
	private static final String[] header = {
		COL_TYPE,
		COL_SEC_DESCR,
		COL_QTY,
		COL_UNCOVERED_QTY,
		COL_ENTER_TIME,
		COL_ENTER_PRICE,
		COL_EXIT_TIME,
		COL_EXIT_PRICE,
		COL_ENTER_VOL,
		COL_EXIT_VOL,
		COL_PROF_LOSS,
		COL_PROF_LOSS_PERC,
	};
	
	private final TradeReport trades;
	private final ClassLabels labels;
	private List<RTrade> list;
	
	public TradesReportTableModel(TradeReport trades, UiTexts texts) {
		super();
		this.trades = trades;
		this.labels = texts.get(TEXT_SEC);
		list = trades.getRecords();
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}
	
	@Override
	public String getColumnName(int col) {
		return labels.get(header[col]);
	}

	@Override
	public Object getValueAt(int row, int col) {
		RTrade report = list.get(row);
		String hdr = header[col];
		if ( hdr == COL_TYPE ) {
			return report.getType();
		} else if ( hdr == COL_SEC_DESCR ) {
			return report.getSecurityDescriptor();
		} else if ( hdr == COL_QTY ) {
			return report.getQty();
		} else if ( hdr == COL_UNCOVERED_QTY ) {
			return report.getUncoveredQty();
		} else if ( hdr == COL_ENTER_TIME ) {
			return report.getEnterTime();
		} else if ( hdr == COL_ENTER_PRICE ) {
			return report.getEnterPrice();
		} else if ( hdr == COL_EXIT_TIME ) {
			return report.getExitTime();
		} else if ( hdr == COL_EXIT_PRICE ) {
			return report.getExitPrice();
		} else if ( hdr == COL_ENTER_VOL ) {
			return report.getEnterVolume();
		} else if ( hdr == COL_EXIT_VOL ) {
			return report.getExitVolume();
		} else if ( hdr == COL_PROF_LOSS ) {
			return report.getProfit();
		} else if ( hdr == COL_PROF_LOSS_PERC ) {
			return report.getProfitPerc();
		}
		return null;
	}

	@Override
	public void start() throws StarterException {
		trades.OnEnter().addListener(this);
		trades.OnExit().addListener(this);
		trades.OnChanged().addListener(this);
		// ВНИМАНИЕ: Стартовать отчет здесь не стоит!
		// Наблюдателей отчета может быть несколько и если каждый будет
		// его стартовать, то получится каша с начальными трейдами.
	}

	@Override
	public void stop() throws StarterException {
		trades.OnEnter().removeListener(this);
		trades.OnExit().removeListener(this);
		trades.OnChanged().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		synchronized ( list ) {
			list = trades.getRecords();
		}
		if ( event.isType(trades.OnEnter()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			fireTableRowsInserted(e.getIndex(), e.getIndex());
		} else if ( event.isType(trades.OnChanged())
				 || event.isType(trades.OnExit()) )
		{
			TradeReportEvent e = (TradeReportEvent) event;
			fireTableRowsUpdated(e.getIndex(), e.getIndex());
		}
	}

}
