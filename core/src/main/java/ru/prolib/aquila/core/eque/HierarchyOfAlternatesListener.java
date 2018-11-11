package ru.prolib.aquila.core.eque;

import java.util.Set;

public interface HierarchyOfAlternatesListener {
	
	/**
	 * Called when hierarchy changed.
	 * <p>
	 * @param alreadyNotified - set of already notified listeners. Used to avoid
	 * stack overflow in case of cyclic references. Do not pass to another
	 * threads.
	 */
	void onHierarchyOfAlternatesChange(Set<HierarchyOfAlternatesListener> alreadyNotified);
	
}
