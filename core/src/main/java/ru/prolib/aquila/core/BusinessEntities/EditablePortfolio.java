package ru.prolib.aquila.core.BusinessEntities;


/**
 * Portfolio service interface.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public interface EditablePortfolio extends Portfolio, UpdatableStateContainer {
	
	/**
	 * Get managed position.
	 * <p>
	 * @param symbol - the symbol
	 * @return position instance
	 */
	public EditablePosition getEditablePosition(Symbol symbol);
	
	/**
	 * Lock object for appearing new positions.
	 * <p>
	 * This method opens critical section to prevent other threads execute
	 * methods which will cause new position object creation. After lock this
	 * thread shouldn't call  any methods which will cause new position
	 * creation. Otherwise the {@link IllegalStateException} will be thrown.
	 * Other threads will wait until this thread releases the lock.
	 * <p>
	 * The case when this lock should be used is an exclusive lock for several
	 * objects where one side is portfolio and its positions and other - all
	 * securities related to these positions. During building the set of
	 * objects to be locked new positions may be instantiated to the portfolio.
	 * If this will done while enumerating positions some security instances
	 * may be lost in final object list (to lock). Locking portfolio as usual
	 * by calling lock method may cause deadlocks. To prevent deadlocks and
	 * appearing new positions make this call, then create multilock for
	 * portfolio, all securities and lock them, then release this lock by call
	 * {@link #unlockNewPositions()} method.
	 */
	void lockNewPositions();
	
	/**
	 * Release object for appearing new positions.
	 * <p>
	 * See {@link #lockNewPositions()} for details.
	 */
	void unlockNewPositions();

}
