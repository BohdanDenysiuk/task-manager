package com.bohdandenysiuk.taskmanager;

public class User {

	private final long id;
	private final String name;

	public User(long id, String name) {
		if (id <= 0 || name == null || name.isBlank()) {
			throw new IllegalArgumentException("User id must be positive and name must not be blank");
		}

		this.id = id;
		this.name = name.trim();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
