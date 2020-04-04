package ru.prolib.aquila.core.BusinessEntities;

import java.io.IOException;

public class OrderDefinitionProviderStub implements OrderDefinitionProvider {
	private CloseableIterator<OrderDefinition> definitions;
	
	public OrderDefinitionProviderStub(CloseableIterator<OrderDefinition> definitions) {
		this.definitions = definitions;
	}
	
	public OrderDefinitionProviderStub() {
		this(new CloseableIteratorStub<>());
	}

	@Override
	public OrderDefinition getNextDefinition() throws IOException {
		if ( definitions.next() ) {
			return definitions.item();
		} else {
			definitions.close();
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		definitions.close();
	}

}
