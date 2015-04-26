package ru.prolib.aquila.probe.storage.model;

import javax.persistence.Entity;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

@Entity
public class SecurityId {
	private long id;
	private SecurityDescriptor descr;
	
	public long getId() {
		return id;
	}
	
	public SecurityDescriptor getDescriptor() {
		return descr;
	}
	
	public void setDescriptor(SecurityDescriptor descr) {
		this.descr = descr;
	}

}
