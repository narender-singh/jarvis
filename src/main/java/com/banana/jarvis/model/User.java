package com.banana.jarvis.model;

public class User {

	private int id;
	private String name;
	private String department;

	public User() {

	}

	public User(int id, String name, String dep) {
		this.id = id;
		this.name = name;
		this.department = dep;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
