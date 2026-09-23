package com.bohdandenysiuk.taskmanager;

public class Task {

	private final String title;
	private boolean done;

	public Task(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Title cannot be empty or equal null!");
		}
		this.done = false;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDone() {
		return done;
	}

	public void markDone() {
		this.done = true;
	}

}
