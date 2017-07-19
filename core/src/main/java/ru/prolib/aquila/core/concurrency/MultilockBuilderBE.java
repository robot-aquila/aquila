package ru.prolib.aquila.core.concurrency;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;

public class MultilockBuilderBE {
	private final Set<BusinessEntity> objects;
	
	public MultilockBuilderBE() {
		objects = new HashSet<>();
	}
	
	public MultilockBuilderBE add(BusinessEntity object) {
		objects.add(object);
		return this;
	}
	
	public MultilockBuilderBE addAll(Collection<? extends BusinessEntity> collection) {
		objects.addAll(collection);
		return this;
	}
	
	public Set<BusinessEntity> getObjects() {
		return objects;
	}
	
	public Lockable buildLock() {
		return new EventSuppressor(objects);
	}
	
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MultilockBuilderBE.class ) {
			return false;
		}
		MultilockBuilderBE o = (MultilockBuilderBE) other;
		return new EqualsBuilder()
				.append(objects, o.objects)
				.isEquals();
	}

}
