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

}
