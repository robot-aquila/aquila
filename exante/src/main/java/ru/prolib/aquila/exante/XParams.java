package ru.prolib.aquila.exante;

import java.io.File;

import ru.prolib.aquila.data.Sequence;

public class XParams {
	private final File settingsFilename;
	private final Sequence orderIDSeq;
	
	public XParams(
			File settings_filename,
			Sequence order_id_seq
		)
	{
		this.settingsFilename = settings_filename;
		this.orderIDSeq = order_id_seq;
	}
	
	/**
	 * Get quickfix settings filename.
	 * <p>
	 * @return file with quickfix session settings
	 */
	public File getSessionSettings() {
		if ( settingsFilename == null ) {
			throw new NullPointerException();
		}
		return settingsFilename;
	}
	
	/**
	 * Get order ID sequence.
	 * <p>
	 * @return id sequence
	 */
	public Sequence getOrderIDSequence() {
		if ( orderIDSeq == null ) {
			throw new NullPointerException();
		}
		return orderIDSeq;
	}

}
