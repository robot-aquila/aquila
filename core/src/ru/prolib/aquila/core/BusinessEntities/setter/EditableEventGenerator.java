package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Editable;
import ru.prolib.aquila.core.BusinessEntities.EditableObjectException;
import ru.prolib.aquila.core.BusinessEntities.FireEditableEvent;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.ValidatorStub;

/**
 * Генератор стандартных событий модифицируемого объекта.
 * <p>
 * Данный функционал реализован в рамках интерфейса модификатора и предназначен
 * для использования в качестве последнего модификатора в составе композитного
 * модификатора.
 * <p>
 * Модифицируемые объекты предусматривают два стандартных события: доступен
 * (добавлен) и изменен. Данный класс анализирует состояние объекта и
 * генерирует соответствующие события.
 * <p>
 * Если объект маркирован как недоступный (неинициализированный), то выполняется
 * анализ на предмет генерации события о доступности нового объекта. Проверка
 * выполняется дополнительным валидатором, передаваемым при создании экземпляра
 * класса. Валидатор используется для анализа сложных случаев. Например, при
 * последовательном поступлении данных о торговом счете, при котором каждый
 * раз получается значение только одного атрибута, необходимо подождать
 * накопления достаточного количества данных после которого объект торгового
 * счета можно сделать доступным для использования. В этом случае, анализ
 * внутреннего состояния объекта-счета реализуется в виде валидатора.
 * <p>
 * 2012-11-29<br>
 * $Id: EditableEventGenerator.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class EditableEventGenerator<T extends Editable> implements S<T> {
	private final Validator availability;
	private final FireEditableEvent available;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param availability валидатор доступности
	 * @param available генератор события о доступности
	 */
	public EditableEventGenerator(Validator availability,
			FireEditableEvent available)
	{
		super();
		this.availability = availability;
		this.available = available;
	}
	
	/**
	 * Конструктор с валидатором по-умолчанию.
	 * <p>
	 * @param available генератор события о доступности
	 */
	public EditableEventGenerator(FireEditableEvent available) {
		this(new ValidatorStub(true), available);
	}
	
	/**
	 * Получить валидатор доступности.
	 * <p>
	 * @return валидатор доступности
	 */
	public Validator getAvailabilityValidator() {
		return availability;
	}
	
	/**
	 * Получить генератор события о доступности.
	 * <p>
	 * @return генератор события
	 */
	public FireEditableEvent getFireAvailableEvent() {
		return available;
	}

	@Override
	public void set(T object, Object value) throws ValueException {
		try {
			if ( object.hasChanged() ) {
				if ( object.isAvailable() ) {
					object.fireChangedEvent();
				} else if ( availability.validate(object) ) {
					available.fireEvent(object);
					object.setAvailable(true);
				}
				object.resetChanges();
			}
		} catch ( ValidatorException e ) {
			throw new ValueException(e);
		} catch ( EditableObjectException e ) {
			throw new ValueException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) return false;
		if ( other.getClass() != getClass() ) return false;
		return fieldsEquals(other);
	}
	
	/**
	 * Сравнение атрибутов объектов.
	 * <p>
	 * Метод предназначен для реализации equlas в наследниках без знания
	 * архитектуры данного класса.
	 * <p>
	 * @param other объект
	 * @return результат сравнения
	 */
	protected boolean fieldsEquals(Object other) {
		EditableEventGenerator<?> o = (EditableEventGenerator<?>) other;
		return new EqualsBuilder()
			.append(availability, o.availability)
			.append(available, o.available)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121129, 194619)
			.append(availability)
			.append(available)
			.toHashCode();
	}

}
