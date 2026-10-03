package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.List;

public class Task {

	private final String title;
	private TaskStatus status;
	private final Long id;
	private final Long projectId;
	private Long assigneeId;
	private final List<String> history;

	public Task(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Title cannot be empty or equal null!");
		}
		this.id = null;
		this.projectId = null;
		this.assigneeId = null;
		this.status = TaskStatus.TODO;
		this.title = title;
		this.history = new ArrayList<>();
		this.history.add("CREATED");
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
		this.status = TaskStatus.TODO;
		this.history = new ArrayList<>();
		this.history.add("CREATED");
	}

	public List<String> getHistory() {
		return new ArrayList<>(history);
	}

	public String getTitle() {
		return title;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public boolean isDone() {
		return this.status == TaskStatus.DONE;
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
		if (!isDone()) {
			this.status = TaskStatus.DONE;
			history.add("COMPLETED");
		}
	}

	public boolean isAssigned() {
		return assigneeId != null;
	}

	public void assignTo(long userId) {
		if (userId <= 0) {
			throw new IllegalArgumentException("User id must be positive");
		}

		if (assigneeId != null && userId == assigneeId) {
			return;
		}

		this.assigneeId = userId;
		this.history.add("ASSIGNED: " + userId);

	}

	public void unassign() {
		if (this.assigneeId != null) {
			this.assigneeId = null;
			this.history.add("UNASSIGNED");
		}
	}

	public boolean start() {
		if (this.status == TaskStatus.TODO) {
			status = TaskStatus.IN_PROGRESS;
			this.history.add("STARTED");
			return true;
		}

		return false;
	}
}
