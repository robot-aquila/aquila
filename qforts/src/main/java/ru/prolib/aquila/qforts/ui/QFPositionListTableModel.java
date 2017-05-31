package ru.prolib.aquila.qforts.ui;

import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.qforts.impl.QFPositionField;
import ru.prolib.aquila.ui.form.PositionListTableModel;

public class QFPositionListTableModel extends PositionListTableModel {
	private static final long serialVersionUID = 1L;
	public static final int CID_VMARGIN = 80;
	public static final int CID_VMARGIN_INTER = 81;
	public static final int CID_VMARGIN_CLOSE = 82;

	public QFPositionListTableModel(IMessages messages) {
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
	protected Object getColumnValue(Position p, int columnID) {
		switch ( columnID ) {
		case CID_VMARGIN:
			return p.getMoney(QFPositionField.QF_VAR_MARGIN);
		case CID_VMARGIN_INTER:
			return p.getMoney(QFPositionField.QF_VAR_MARGIN_INTER);
		case CID_VMARGIN_CLOSE:
			return p.getMoney(QFPositionField.QF_VAR_MARGIN_CLOSE);
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
			return FMoney.class;
		default:
			return super.getColumnClass(col);
		}
	}

}
