package ru.prolib.aquila.core.timetable;

import org.joda.time.DateTime;

/**
 * Ряд фикстуры для проверки булева результата.
 */
class FR {
	final DateTime time;
	final boolean expected;
	FR(DateTime time, boolean expected) {
		this.time = time;
		this.expected = expected;
	}
}