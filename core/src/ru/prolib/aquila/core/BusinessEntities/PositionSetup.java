package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Спецификация позиции.
 */
public class PositionSetup {
	private static final PositionType CLOSE = PositionType.CLOSE;
	private static final PositionType BOTH = PositionType.BOTH;
	private static final PositionType LONG = PositionType.LONG;
	private static final PositionType SHORT = PositionType.SHORT;
	
	private final SecurityDescriptor descr;
	private Price quota; 
	private PositionType target;
	private PositionType allowed = PositionType.BOTH;
	
	public PositionSetup(SecurityDescriptor descr,
			Price quota, PositionType target)
	{
		super();
		this.descr = descr;
		this.quota = quota;
		this.target = target;
	}
	
	public PositionSetup(SecurityDescriptor descr, Price quota) {
		this(descr, quota, PositionType.CLOSE);
	}
	
	public PositionSetup(SecurityDescriptor descr) {
		this(descr, new Price(PriceUnit.PERCENT, 0.0d));
	}
	
	public PositionSetup(PositionSetup setup) {
		super();
		synchronized ( setup ) {
			descr = setup.getSecurityDescriptor();
			quota = setup.getQuota();
			target = setup.getTarget();
			allowed = setup.getAllowedType();
		}
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}

	/**
	 * Получить долю элемента в портфеле.
	 * <p>
	 * @return доля
	 */
	public synchronized Price getQuota() {
		return quota;
	}
	
	/**
	 * Установить долю элемента в портфеле.
	 * <p>
	 * @param value доля
	 */
	public synchronized void setQuota(Price value) {
		quota = value;
	}

	/**
	 * Получить целевой тип позиции.
	 * <p>
	 * @return целевой тип позиции
	 */
	public synchronized PositionType getTarget() {
		return target;
	}

	/**
	 * Установить целевой тип позиции.
	 * <p>
	 * @param value тип позиции
	 * @throws IllegalArgumentException указан тип {@link PositionType#BOTH}
	 */
	public synchronized void setTarget(PositionType value) {
		if ( value == PositionType.BOTH ) {
			throw new IllegalArgumentException(value.toString());
		}
		target = value;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		return other != null && other.getClass() == PositionSetup.class ?
			fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		PositionSetup o = (PositionSetup) other;
		return new EqualsBuilder()
			.append(descr, o.descr)
			.append(quota, o.quota)
			.append(target, o.target)
			.append(allowed, o.allowed)
			.isEquals();
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121231, 165329)
			.append(descr)
			.append(quota)
			.append(target)
			.append(allowed)
			.toHashCode();
	}
	
	@Override
	public synchronized PositionSetup clone() {
		return new PositionSetup(this);
	}

	/**
	 * Установить разрешенный тип позиции.
	 * <p>
	 * Подразумевается:<p>
	 * {@link PositionType#CLOSE} - запретить открытие позиций<br>
	 * {@link PositionType#LONG} - разрешить только длиные позиции<br>
	 * {@link PositionType#SHORT} - разрешить только короткие позиции<br>
	 * {@link PositionType#BOTH} - разрешить открытие позиций либого типа<br>
	 * <p>
	 * @param value разрешенный тип
	 */
	public synchronized void setAllowedType(PositionType value) {
		allowed = value;
	}

	/**
	 * Получить разрешенный тип позиции.
	 * <p>
	 * см. {@link PositionSetup#setAllowedType(PositionType)}.
	 * <p>
	 * @return разрешенный тип позиции
	 */
	public synchronized PositionType getAllowedType() {
		return allowed;
	}

	/**
	 * Проверить разрешение цели.
	 * <p>
	 * Для проверки используется настройка разрешенного типа позиции.
	 * <p>
	 * @param target целевой тип для проверки
	 * @return true - разрешено открыть позицию данного типа, false - запрещено
	 */
	public synchronized boolean isTargetAllowed(PositionType target) {
		return (target == CLOSE )
			|| (target == LONG && (allowed == LONG || allowed == BOTH))
			|| (target == SHORT && (allowed == SHORT || allowed == BOTH));
	}

	/**
	 * Проверить необходимость закрытия текущей позиции.
	 * <p>
	 * Проверяет нужно-ли закрыть позицию в соответствии с текущим и
	 * целевым типами позиции.
	 * <p>
	 * @param current тип текущей позиции
	 * @return true - следует закрыть позицию, false - ничего делать не нужно
	 */
	public synchronized boolean shouldClose(PositionType current) {
		return current != target && (current == LONG || current == SHORT) &&
			(target == CLOSE || ! isTargetAllowed(target));
	}
	
	/**
	 * Проверить необходимость открытия позиции.
	 * <p>
	 * Проверяет, нужно-ли открыть позицию в соответствии с настройками.
	 * Учитывает текущий, целевой и разрешенный типы позиции, а так же
	 * установленный размер доли.
	 * <p>
	 * @param current тип текущей позиции
	 * @return true - следует открыть позицию, false - ничего делать не нужно
	 */
	public synchronized boolean shouldOpen(PositionType current) {
		return current == CLOSE && quota.getValue() > 0
			&& isTargetAllowed(target)
			&& (target == LONG || target == SHORT);
	}
	
	/**
	 * Проверить необходимость разворота позиции.
	 * <p>
	 * Проверяет, нужно-ли развернуть позицию в соответствии с настройками.
	 * Учитывает текущий, целевой и разрешенный типы позиции. Разворот нужно
	 * выполнять только если текущая и целевая противоположны, разворот не
	 * запрещен и размер доли отличен от нуля.
	 * <p>
	 * @param current тип текущей позиции
	 * @return true - следует развернуть позицию, false - ничего делать не нужно
	 */
	public synchronized boolean shouldSwap(PositionType current) {
		return quota.getValue() > 0 && isTargetAllowed(target)
			&& ((current == LONG && target == SHORT)
			 || (current == SHORT && target == LONG));
	}
	
	/**
	 * Проверить соответствие целей.
	 * <p>
	 * @param other сетап для сравнения
	 * @return true - цели совпадают, false - не совпадают
	 */
	public synchronized boolean isDifferentTarget(PositionSetup other) {
		return target != other.target;
	}
	
	/**
	 * Проверить соответствие долей.
	 * <p>
	 * @param other сетап для сравнения
	 * @return true - доли совпадают, false - не совпадают 
	 */
	public synchronized boolean isDifferentQuota(PositionSetup other) {
		return ! quota.equals(other.quota);
	}

}
