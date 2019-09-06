package ru.prolib.aquila.ui.FastOrder;

import javax.swing.DefaultComboBoxModel;

public class SortedComboBoxModel<E extends Comparable<E>> extends DefaultComboBoxModel<E> {
	private static final long serialVersionUID = 1L;

	@Override
	public void addElement(E element) {
		insertElementAt(element, 0);
	}
	
	@Override
	public void insertElementAt(E element, int index) {
		int size = getSize();
		for ( index = 0; index < size; index ++ ) {
			E o = getElementAt(index);
			if ( o.compareTo(element) > 0 ) {
				break;
			}
		}
		super.insertElementAt(element, index);
		if ( index == 0 && element != null ) {
			setSelectedItem(element);
		}
	}

}
