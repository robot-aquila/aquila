package ru.prolib.aquila.ui.wrapper;
/**
 * $Id: Table.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class Table {

	/*
	 * как то так употреблять
	 * 
	public void createUI(Orders orders) {
    
        TableDataSource source = new TableDataSource(new OrderRowGetter(), orders);
        orders.OnOrderAvailable().addListener(source.getOnAvailableListener());
        orders.OnOrderChanged().addListener(source.getOnChangedListener());
        
        TableModel tb = new TableModel(source);
        tb.addColumn(new TableColumn("ORDER_ID", new GOrderId()));
        tb.addColumn(new TableColumn("ORDER_PRICE", new GOrderPrice()));
        tb.addColumn(new TableColumn("ORDER_SECURITY", new GOrderSecurity(), 200));
        
        Table table = new Table(tb);
        table.start();
        add(table.getUnderlayed()(;
    }
	 */
}
