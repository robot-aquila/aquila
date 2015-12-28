package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;

/**
 * Ряд фикстуры для проверки булева результата.
 */
class FR {
	final LocalDateTime time;
	final boolean expected;
	FR(LocalDateTime time, boolean expected) {
		this.time = time;
		this.expected = expected;
	}
}