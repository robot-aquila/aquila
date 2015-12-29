package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Команда эмулятора.
 * <p>
 * Команды используются для управления ходом эмуляции.
 */
public class TLCmd {
	private final TLCmdType type;
	private final LocalDateTime time;
	
	public static final TLCmd FINISH = new TLCmd(TLCmdType.FINISH);
	public static final TLCmd PAUSE = new TLCmd(TLCmdType.PAUSE);
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для команд, не требующих указания
	 * временой метки.
	 * <p>
	 * @param type тип команды
	 */
	public TLCmd(TLCmdType type) {
		super();
		this.type = type;
		this.time = null;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инстанцирования команд типа
	 * {@link TLCmdType#RUN}.
	 * <p>
	 * @param time аргумент-время
	 */
	public TLCmd(LocalDateTime time) {
		super();
		this.type = TLCmdType.RUN;
		this.time = time;
	}
	
	/**
	 * Проверить соответствие типу команды.
	 * <p>
	 * @param type тип команды
	 * @return true - типы совпадают, false - типы не совпадают
	 */
	public boolean isType(TLCmdType type) {
		return this.type == type;
	}
	
	/**
	 * Получить тип команды.
	 * <p>
	 * @return тип команды
	 */
	public TLCmdType getType() {
		return type;
	}
	
	/**
	 * Получить временную метку.
	 * <p>
	 * @return время аргумент-команды. Для команд, отличных от
	 * {@link TLCmdType#RUN} возвращается null. 
	 */
	public LocalDateTime getTime() {
		return time;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TLCmd.class ) {
			return false;
		}
		TLCmd o = (TLCmd) other;
		return new EqualsBuilder()
			.append(o.time, time)
			.append(o.type, type)
			.isEquals();
	}

}
