package ru.prolib.aquila.ib.subsys.api;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Делегат киперу при срабатывании таймера.
 * <p>
 * Используется для вызова соответствующего метода контроллера соединения
 * с IB API через определенный промежуток времени.
 * <p>
 * 2013-01-15<br>
 * $Id: IBConnectionKeeperTask.java 435 2013-01-15 13:27:19Z whirlwind $
 */
public class IBConnectionKeeperTask extends TimerTask {
	private final IBConnectionKeeper keeper;
	
	public IBConnectionKeeperTask(IBConnectionKeeper keeper) {
		super();
		this.keeper = keeper;
	}

	@Override
	public void run() {
		keeper.restoreConnection();
	}
	
	/**
	 * Колучить экземпляр кипера соединения.
	 * <p>
	 * @return кипер
	 */
	public IBConnectionKeeper getConnectionKeeper() {
		return keeper;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == IBConnectionKeeperTask.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBConnectionKeeperTask o = (IBConnectionKeeperTask) other;
		return new EqualsBuilder()
			.append(keeper, o.keeper)
			.isEquals();
	}
	
}
