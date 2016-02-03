package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.data.EditableContainer;

/**
 * Portfolio service interface.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public interface EditablePortfolio extends Portfolio, EditableContainer {
	
	/**
	 * Get managed position.
	 * <p>
	 * @param symbol - the symbol
	 * @return position instance
	 */
	public EditablePosition getEditablePosition(Symbol symbol);

}
