package ru.prolib.aquila.ui.wrapper;

import javax.swing.table.TableColumn;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.data.G;
/**
 * $Id: TableColumn.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableColumnWrp implements Starter {
	private String name;
	private String text = "";
	private G<?> getter;
	private int width;
	private TableColumn underlayed;
	
	public TableColumnWrp(String name, G<?> getter) {
		this.name = name;
		this.getter = getter;
	}
	
	public TableColumnWrp(String name, G<?> getter, String text) {
		this.name = name;
		this.getter = getter;
		this.text = text;
	}
	
	public TableColumnWrp(String name, G<?> getter, int width) {
		this.name = name;
		this.getter = getter;
		this.width = width;
	}
	
	public TableColumnWrp(String name, G<?> getter, String text, int width) {
		this.name = name;
		this.getter = getter;
		this.width = width;
		this.text = text;
	}
	
	public void setUnderlayed(TableColumn underlayed) {
		this.underlayed = underlayed;
	}
	
	public TableColumn getUnderlayed() {
		return underlayed;
	}
	
	public String getText() {
		return text;
	}
	
	public String getName() {
		return name;
	}
	
	public G<?> getGetter() {
		return getter;
	}
	
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		if((Integer) width != null) {
			underlayed.setPreferredWidth(width);
		}		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		// TODO Auto-generated method stub
		
	}
}
