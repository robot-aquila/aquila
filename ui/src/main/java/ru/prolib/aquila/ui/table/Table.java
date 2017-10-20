package ru.prolib.aquila.ui.table;

import javax.swing.JTable;
import ru.prolib.aquila.core.*;

/**
 * Базовая таблица на основе типовой модели.
 * <p>
 * Устанавливает параметры колонок и автоматически запускает модель таблицы в
 * работу при старте таблицы.
 */
public class Table extends JTable implements Starter {
	private static final long serialVersionUID = -2341221190031085189L;
	private final TableModel model;
	private boolean init = false;
	
	public Table(TableModel model) {
		super(model);
		this.model = model;
	}

	@Override
	public void start() {
		if ( ! init ) {
			setUpColumns();
			init = true;
		}
		model.start();
	}

	@Override
	public void stop() {
		model.stop();
	}
	
	private void setUpColumns() {
		double totalPts = 0;
		Columns columns = model.getColumns();
		for ( int i = 0; i < columns.getCount(); i ++ ) {
			totalPts += columns.get(i).getWidth();
		}
		
		// 1 px / 1 pts= component width / totalPts
		double point = getWidth() / totalPts;
		for ( int i = 0; i < columns.getCount(); i ++ ) {
			int width = (int) (columns.get(i).getWidth() * point);
			columnModel.getColumn(i).setPreferredWidth(width);
		}
	}

}
