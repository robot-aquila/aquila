package ru.prolib.aquila.quik.assembler;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.dde.*;

/**
 * Конструктор фасада сборщика объектов.
 */
public class AssemblerBuilder {
	
	public AssemblerBuilder() {
		super();
	}
	
	/**
	 * Собрать фасад для терминала и кэша.
	 * <p>
	 * @param terminal терминал
	 * @param cache кэш данных
	 * @return фасад сборщика объектов
	 */
	public Assembler createAssembler(EditableTerminal terminal, Cache cache) {
		return new Assembler(terminal, cache,
			new AssemblerHighLvl(terminal, cache,
				new AssemblerMidLvl(terminal, cache,
					new AssemblerLowLvl(terminal, cache))));
	}

}
