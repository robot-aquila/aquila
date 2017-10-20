package ru.prolib.aquila.ui.form;

import java.time.Instant;
import java.time.LocalDateTime;

public interface TimeSelectionDialogView {
	
	public LocalDateTime showDialog(LocalDateTime initialTime);
	
	public Instant showDialog(Instant initialTime);

}
