package com.bohdandenysiuk.taskmanager;

public class Task {

	private final String title;
	private boolean done;
	private final Long id;
	private final Long projectId;
	private Long assigneeId;

	public Task(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Title cannot be empty or equal null!");
		}
		this.id = null;
		this.projectId = null;
		this.assigneeId = null;
		this.done = false;
		this.title = title;
	}

	public Task(long id, String title, long projectId) {
		if (id <= 0 || projectId <= 0 || title == null || title.isBlank()) {
			throw new IllegalArgumentException(
					"Task and project ids must be positive " + "and title must not be blank");
		}

		this.id = id;
		this.title = title.trim();
		this.projectId = projectId;
		this.assigneeId = null;
		this.done = false;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDone() {
		return done;
	}

	public Long getId() {
		return id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Long getAssigneeId() {
		return assigneeId;
	}

	public void markDone() {
		this.done = true;
	}

	public boolean isAssigned() {
		return assigneeId != null;
	}

	public void assignTo(long userId) {
		if (userId <= 0) {
			throw new IllegalArgumentException("User id must be positive");
		}

		this.assigneeId = userId;
	}

	public void unassign() {
		this.assigneeId = null;
	}

}
