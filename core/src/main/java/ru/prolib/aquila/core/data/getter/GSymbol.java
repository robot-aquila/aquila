package ru.prolib.aquila.core.data.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер дескриптора инструмента.
 * <p>
 * Использует два геттера для доступа к коду инструмента и коду класса. В
 * случае отсутствия возможности создать дескриптор, возвращает null.  
 */
@Deprecated
public class GSymbol implements G<Symbol> {
	private final G<String> gCode;
	private final G<String> gClass;
	private final G<String> gCurr;
	private final G<SymbolType> gType;
	
	/**
	 * Создать геттер.
	 * <p>
	 * @param gCode геттер кода инструмента
	 * @param gClass геттер класса инструмента
	 * @param gCurr геттер кода валюты
	 * @param gType геттер типа инструмента
	 */
	public GSymbol(G<String> gCode, G<String> gClass,
			G<String> gCurr, G<SymbolType> gType)
	{
		super();
		this.gCode = gCode;
		this.gClass = gClass;
		this.gCurr = gCurr;
		this.gType = gType;
	}
	
	/**
	 * Получить геттер кода инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCodeGetter() {
		return gCode;
	}
	
	/**
	 * Получить геттер кода класса.
	 * <p>
	 * @return геттер
	 */
	public G<String> getClassGetter() {
		return gClass;
	}
	
	/**
	 * Получить геттер кода валюты.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCurrencyGetter() {
		return gCurr;
	}
	
	/**
	 * Получить геттер типа инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<SymbolType> getTypeGetter() {
		return gType;
	}

	@Override
	public Symbol get(Object source) throws ValueException {
		Symbol symbol = new Symbol(gCode.get(source),
				gClass.get(source), gCurr.get(source), gType.get(source));
		return symbol.isValid() ? symbol : null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GSymbol ) {
			GSymbol o = (GSymbol) other;
			return new EqualsBuilder()
				.append(gCode, o.gCode)
				.append(gClass, o.gClass)
				.append(gCurr, o.gCurr)
				.append(gType, o.gType)
				.isEquals();			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, 135927)
			.append(gCode)
			.append(gClass)
			.append(gCurr)
			.append(gType)
			.toHashCode();
	}

}
