package ru.prolib.aquila.core.timetable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Дескриптор дня недели.
 * <p>
 * Данный класс позволяет определять даты внутри недели. Дескриптор всегда
 * указывает на полные сутки. Это значит, что при проверке времени учитываются
 * только даты.
 */
@XStreamAlias("DayOfWeek")
public class DOWSpan implements Span {
	@XStreamAsAttribute
	private final DOW day;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dayOfWeek день недели)
	 */
	public DOWSpan(DOW dayOfWeek) {
		super();
		this.day = dayOfWeek;
	}
	
	/**
	 * Получить день недели.
	 * <p>
	 * @return день недели
	 */
	public DOW getDayOfWeek() {
		return day;
	}
	
	@Override
	public boolean less(LocalDateTime time) {
		return day.getNumber() < time.getDayOfWeek().getValue();
	}

	@Override
	public boolean lessOrEquals(LocalDateTime time) {
		return day.getNumber() <= time.getDayOfWeek().getValue();
	}

	@Override
	public boolean greater(LocalDateTime time) {
		return day.getNumber() > time.getDayOfWeek().getValue();
	}
	
	@Override
	public boolean greaterOrEquals(LocalDateTime time) {
		return day.getNumber() >= time.getDayOfWeek().getValue();
	}

	/**
	 * Выровнять абсолютное время по дню недели.
	 * <p>
	 * Выравнивание даты по дню недели подразумевает выбор ближайшей будущей
	 * даты, день недели которой соответствует дню недели дескриптора. Время не
	 * изменяется.
	 */
	@Override
	public LocalDateTime align(LocalDateTime time) {
		LocalDateTime aligned = (LocalDateTime) DayOfWeek.of(day.getNumber()).adjustInto(time);
		return aligned.isBefore(time) ? aligned.plusWeeks(1) : aligned;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DOWSpan.class ) {
			return false;
		}
		DOWSpan o = (DOWSpan) other;
		return new EqualsBuilder()
			.append(o.day, day)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return day.toString();
	}

}
