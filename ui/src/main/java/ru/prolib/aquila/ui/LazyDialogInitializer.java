package ru.prolib.aquila.ui;

import javax.swing.JDialog;

public class LazyDialogInitializer implements DialogFactory {
	private final DialogFactory factory;
	private JDialog dialog;
	
	public LazyDialogInitializer(DialogFactory factory) {
		this.factory = factory;
	}

	@Override
	public JDialog produce() {
		if ( dialog == null ) {
			dialog = factory.produce();
		}
		return dialog;
	}
	
}