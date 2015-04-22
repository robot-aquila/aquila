package ru.prolib.aquila.probe.storage.dal;

import java.util.List;

import ru.prolib.aquila.probe.storage.dal.entities.SecurityDescriptor;

public interface DataProvider {
	
	public List<SecurityDescriptor> getSecurityDescriptors();

}
