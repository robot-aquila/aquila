package ru.prolib.aquila.quik.subsys;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Делегат киперу при срабатывании таймера.
 * <p>
 * Используется для вызова соответствующего метода кипера, инициирующего
 * соединение с QUIK API через определенный промежуток времени.
 * <p>
 * 2013-02-11<br>
 * $Id$
 */
public class QUIKConnectionKeeperTask extends TimerTask {
	private final QUIKConnectionKeeper keeper;
	
	public QUIKConnectionKeeperTask(QUIKConnectionKeeper keeper) {
		super();
		this.keeper = keeper;
	}
	
	/**
	 * Получить кипер подключения к QUIK API.
	 * <p>
	 * @return кипер
	 */
	public QUIKConnectionKeeper getConnectionKeeper() {
		return keeper;
	}

	@Override
	public void run() {
		keeper.restoreConnection();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == QUIKConnectionKeeperTask.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		QUIKConnectionKeeperTask o = (QUIKConnectionKeeperTask) other;
		return new EqualsBuilder()
			.append(keeper, o.keeper)
			.isEquals();
	}

}
