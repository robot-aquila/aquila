package ru.prolib.aquila.qforts.ui;

import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.qforts.impl.QFPortfolioField;
import ru.prolib.aquila.ui.form.PortfolioListTableModel;

public class QFPortfolioListTableModel extends PortfolioListTableModel {
	private static final long serialVersionUID = 1L;
	public static final int CID_VMARGIN = 90;
	public static final int CID_VMARGIN_INTER = 91;
	public static final int CID_VMARGIN_CLOSE = 92;

	public QFPortfolioListTableModel(IMessages messages) {
		super(messages);
	}
	
	@Override
	protected List<Integer> getColumnIDList() {
		List<Integer> x = super.getColumnIDList();
		x.add(CID_VMARGIN);
		x.add(CID_VMARGIN_INTER);
		x.add(CID_VMARGIN_CLOSE);
		return x;
	}
	
	@Override
	protected Map<Integer, MsgID> getColumnIDToHeaderMap() {
		Map<Integer, MsgID> x = super.getColumnIDToHeaderMap();
		x.put(CID_VMARGIN, QFortsMsg.VMARGIN);
		x.put(CID_VMARGIN_INTER, QFortsMsg.VMARGIN_INTER);
		x.put(CID_VMARGIN_CLOSE, QFortsMsg.VMARGIN_CLOSE);
		return x;
	}
	
	@Override
	protected Object getColumnValue(Portfolio p, int columnID) {
		switch ( columnID ) {
		case CID_VMARGIN:
			return p.getCDecimal(QFPortfolioField.QF_VAR_MARGIN);
		case CID_VMARGIN_INTER:
			return p.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_INTER);
		case CID_VMARGIN_CLOSE:
			return p.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_CLOSE);
		default:
			return super.getColumnValue(p, columnID);
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch ( getColumnID(col) ) {
		case CID_VMARGIN:
		case CID_VMARGIN_INTER:
		case CID_VMARGIN_CLOSE:
			return CDecimal.class;
		default:
			return super.getColumnClass(col);
		}
	}

}
