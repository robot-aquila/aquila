package ru.prolib.aquila.core.timetable;

import org.joda.time.DateTime;

/**
 * Ряд фикстуры для проверки конвертации времени.
 */
class FR2 {
	final DateTime time;
	final DateTime expected;
	FR2(DateTime time, DateTime expected) {
		this.time = time;
		this.expected = expected;
	}
}