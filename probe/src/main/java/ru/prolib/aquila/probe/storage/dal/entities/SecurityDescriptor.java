package ru.prolib.aquila.probe.storage.dal.entities;

import javax.persistence.*;

@Entity
@Table(name = "security_descriptors")
public class SecurityDescriptor {
	@Id
	@Column(name="id", unique=true, nullable=false)
	private Integer id;
	
	@Column(name="code", nullable=false, length=16)
	private String code;
	
	@Column(name="class_code", nullable=false, length=16)
	private String classCode;
	
	@Column(name="currency", nullable=false, length=3)
	private String currency;
	
	@Column(name="type", nullable=false, length=4)
	private String type;
	
	public Integer getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getClassCode() {
		return classCode;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public String getType() {
		return type;
	}

}
