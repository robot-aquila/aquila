package ru.prolib.aquila.core.sm;

/**
 * Автовыбор входного дескриптора невозможен по причине неоднозначности.
 * Данное исключение возникает когда состояние имеет более чем один вход.
 */
public class SMAmbiguousInputException extends SMException {
	private static final long serialVersionUID = -7426915738332963856L;
	
	public SMAmbiguousInputException() {
		super();
	}

}
