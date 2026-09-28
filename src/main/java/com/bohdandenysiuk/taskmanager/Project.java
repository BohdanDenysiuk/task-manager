package com.bohdandenysiuk.taskmanager;

public class Project {

	private final long id;
	private final String name;
	private final long ownerId;

	public Project(long id, String name, long ownerId) {
		if (name == null || name.isBlank() || id <= 0 || ownerId <= 0) {
			throw new IllegalArgumentException("Project and owner ids must be positive and name must not be blank");
		}
		this.id = id;
		this.name = name.trim();
		this.ownerId = ownerId;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getOwnerId() {
		return ownerId;
	}
}
