package ru.prolib.aquila.probe.storage.model;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

public class SymbolEntity {
	private Long id;
	private SecurityDescriptor descr;

	public SymbolEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public SecurityDescriptor getDescriptor() {
		return descr;
	}
	
	public void setDescriptor(SecurityDescriptor descr) {
		this.descr = descr;
	}

}
