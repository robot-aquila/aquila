package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.quik.api.QUIKMainHandler;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;

/**
 * Базовый обработчик данных QUIK API.
 * <p>
 * Реализует выполнение процедуры установки/разрыва соединения с QUIK API.
 * Направляет входящие данные соответствующим методам сборщика объектов. 
 */
public class MainHandler implements QUIKMainHandler {
	private final EditableTerminal terminal;
	private final Assembler asm;

	public MainHandler(EditableTerminal terminal, Assembler asm) {
		super();
		this.terminal = terminal;
		this.asm = asm;
	}
	
	EditableTerminal getTerminal() {
		return terminal;
	}
	
	Assembler getAssembler() {
		return asm;
	}

	@Override
	public void connectionStatus(T2QConnStatus status) {
		if ( status == T2QConnStatus.DLL_CONN ) {
			terminal.fireTerminalConnectedEvent();
		} else if ( status == T2QConnStatus.DLL_DISC ) {
			terminal.fireTerminalDisconnectedEvent();
		}
	}

	@Override
	public void orderStatus(T2QOrder order) {
		asm.assemble(order);
	}

	@Override
	public void tradeStatus(T2QTrade trade) {
		asm.assemble(trade);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MainHandler.class ) {
			return false;
		}
		MainHandler o = (MainHandler) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.asm, asm)
			.isEquals();
	}

}
