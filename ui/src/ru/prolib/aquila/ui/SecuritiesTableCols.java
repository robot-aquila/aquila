package ru.prolib.aquila.ui;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ui.plugin.getters.*;
import ru.prolib.aquila.ui.wrapper.TableColumnAlreadyExistsException;
import ru.prolib.aquila.ui.wrapper.TableColumnWrp;
import ru.prolib.aquila.ui.wrapper.TableModel;

/**
 * $Id$
 */
public class SecuritiesTableCols {

	private final String[] colIndex = {
		"COL_NAME",
		"COL_TYPE",
		"COL_STATUS",
		"COL_CURR",
		"COL_SYMBOL",
		"COL_CLASS",
		"COL_LAST",
		"COL_OPEN",
		"COL_HIGH",
		"COL_LOW",
		"COL_CLOSE",
		"COL_ASK",
		"COL_ASK_SIZE",
		"COL_BID",
		"COL_BID_SIZE",
		"COL_LOT",
		"COL_TICK",
		"COL_PREC"
	};
	
	private static final Map<String, Integer> width = new HashMap<String, Integer>();
	
	private static final Map<String, G<?>> getters = new HashMap<String, G<?>>();
	
	static {
		getters.put("COL_NAME", new GSecurityName());
		getters.put("COL_TYPE", new GSecurityType());
		getters.put("COL_STATUS", new GSecurityStatus());
		getters.put("COL_CURR", new GSecurityCurrency());
		getters.put("COL_SYMBOL", new GSecuritySymbol());
		getters.put("COL_CLASS", new GSecurityClass());
		getters.put("COL_LAST", new GSecurityLastPrice());
		getters.put("COL_OPEN", new GSecurityOpenPrice());
		getters.put("COL_HIGH", new GSecurityHighPrice());
		getters.put("COL_LOW", new GSecurityLowPrice());
		getters.put("COL_CLOSE", new GSecurityClosePrice());
		getters.put("COL_ASK", new GSecurityAskPrice());
		getters.put("COL_ASK_SIZE", new GSecurityAskSize());
		getters.put("COL_BID", new GSecurityBidPrice());
		getters.put("COL_BID_SIZE", new GSecurityBidSize());
		getters.put("COL_LOT", new GSecurityLotSize());
		getters.put("COL_TICK", new GSecurityMinStep());
		getters.put("COL_PREC", new GSecurityPrecision());
		
		width.put("COL_NAME", 200);
	}
	
	public SecuritiesTableCols() {
		super();
	}
	
	public String[] getColIndex() {
		return colIndex;
	}
	
	public Map<String, Integer> getWidth() {
		return width;
	}
	
	public Map<String, G<?>> getGetters() {
		return getters;
	}
	
	public void addColumnsToModel(TableModel model, ClassLabels texts) throws TableColumnAlreadyExistsException {
		for(int i = 0; i < colIndex.length; i++) {
			String colId = colIndex[i];
			if(width.containsKey(colId)) {
				model.addColumn(new TableColumnWrp(
						colId, getters.get(colId), texts.get(colId), width.get(colId)));
			} else {
				model.addColumn(new TableColumnWrp(
					colId, getters.get(colId), texts.get(colId)));
			}
		}
	
	}
}
