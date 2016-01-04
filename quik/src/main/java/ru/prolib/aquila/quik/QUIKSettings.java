package ru.prolib.aquila.quik;

import java.time.ZoneId;

public class QUIKSettings {
	public static final ZoneId TIMEZONE;
	
	static {
		TIMEZONE = ZoneId.of("Europe/Moscow");
	}

}
