package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;

/**
 * Ряд фикстуры для проверки конвертации времени.
 */
class FR2 {
	final LocalDateTime time;
	final LocalDateTime expected;
	FR2(LocalDateTime time, LocalDateTime expected) {
		this.time = time;
		this.expected = expected;
	}
}