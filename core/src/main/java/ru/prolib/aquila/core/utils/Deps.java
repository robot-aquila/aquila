package ru.prolib.aquila.core.utils;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Реализация набора зависимостей.
 * <p>
 * Внимание: не является потокобезопасной.
 * <p>
 * @param <T> - тип объектов, между которыми устанавливаются зависимости
 * <p>
 * 2012-11-07<br>
 * $Id: Deps.java 307 2012-11-07 14:34:39Z whirlwind $
 */
public class Deps<T> implements Dependencies<T> {
	
	/**
	 * Карта кто зависит -> от кого зависит 
	 */
	private final Map<T, Set<T>> dependencies;
	
	/**
	 * Карта от кого зависит -> кто зависит
	 */
	private final Map<T, Set<T>> dependents;
	
	public Deps() {
		super();
		dependencies = new Hashtable<T, Set<T>>();
		dependents = new Hashtable<T, Set<T>>();
	}

	@Override
	public boolean hasDependentTo(T subj) {
		Set<T> set = dependents.get(subj);
		return set != null && set.size() > 0;
	}

	@Override
	public boolean hasDependency(T subj) {
		Set<T> set = dependencies.get(subj);
		return set != null && set.size() > 0;
	}
	
	@Override
	public boolean hasDependency(T subj, T dependentTo) {
		Set<T> set = dependencies.get(subj);
		return set != null && set.contains(dependentTo);
	}

	@Override
	public Set<T> getDependentsTo(T subj) {
		return hasDependentTo(subj) ?
			new HashSet<T>(dependents.get(subj)) : new HashSet<T>();
	}

	@Override
	public Set<T> getDependencies(T subj) {
		return hasDependency(subj) ?
			new HashSet<T>(dependencies.get(subj)) : new HashSet<T>();
	}

	@Override
	public Dependencies<T> setDependency(T subj, T dependentTo) {
		if ( subj == null || dependentTo == null ) {
			throw new NullPointerException();
		}
		Set<T> set = dependencies.get(subj);
		if ( set == null ) {
			set = new HashSet<T>();
			dependencies.put(subj, set);
		}
		set.add(dependentTo);
		
		set = dependents.get(dependentTo);
		if ( set == null ) {
			set = new HashSet<T>();
			dependents.put(dependentTo, set);
		}
		set.add(subj);
		return this;
	}

	@Override
	public Dependencies<T> dropDependency(T subj, T dependentTo) {
		if ( hasDependency(subj, dependentTo) ) {
			dependencies.get(subj).remove(dependentTo);
			dependents.get(dependentTo).remove(subj);
		}
		return this;
	}

	@Override
	public Dependencies<T> dropDependencies(T subj) {
		for ( T dependentTo : getDependencies(subj) ) {
			dropDependency(subj, dependentTo);
		}
		return this;
	}

	@Override
	public Dependencies<T> dropDependentsTo(T dependentTo) {
		for ( T subj : getDependentsTo(dependentTo) ) {
			dropDependency(subj, dependentTo);
		}
		return this;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 173637)
			.append(dependencies)
			.append(dependents)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof Deps ) {
			Deps<?> o = (Deps<?>) other;
			return new EqualsBuilder()
				.append(dependencies, o.dependencies)
				.append(dependents, o.dependents)
				.isEquals();
		} else {
			return false;
		}
	}

}
