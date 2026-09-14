package com.bohdandenysiuk.taskmanager;

public class TaskManager {

	private static final int DEFAULT_CAPACITY = 10;
	private final Task[] tasks;
	private int taskCount;

	public TaskManager(int capacity) {
		if (capacity < 0) {
			capacity = 0;
		}
		this.tasks = new Task[capacity];
	}

	public TaskManager() {
		this(DEFAULT_CAPACITY);
	}

	public int getTaskCount() {
		return taskCount;
	}

	public int getCapacity() {
		return tasks.length;
	}

	public boolean addTask(String title) {
		if (title == null || title.isBlank() || tasks.length <= taskCount) {
			return false;
		}

		Task newTask = new Task(title);
		this.tasks[taskCount] = newTask;
		taskCount++;
		return true;
	}

	public Task getTask(int index) {
		if (index < 0 || index >= taskCount) {
			return null;
		}

		return tasks[index];
	}

	public Task findTask(String title) {
		int index = findTaskIndex(title);

		if (index == -1) {
			return null;
		}

		return tasks[index];
	}

	public boolean completeTask(String title) {
		Task taskToFind = findTask(title);

		if (taskToFind == null || taskToFind.isDone()) {
			return false;
		}

		taskToFind.markDone();
		return true;
	}

	public int countDoneTasks() {
		int doneTasks = 0;
		for (int i = 0; i < taskCount; ++i) {
			if (tasks[i].isDone()) {
				doneTasks++;
			}
		}

		return doneTasks;
	}

	private int findTaskIndex(String title) {
		if (title == null || title.isBlank()) {
			return -1;
		}

		for (int i = 0; i < taskCount; ++i) {
			if (tasks[i].getTitle().equals(title)) {
				return i;
			}
		}

		return -1;
	}

	public boolean removeTask(String title) {
		int index = findTaskIndex(title);

		if (index == -1) {
			return false;
		}

		for (int i = index; i < taskCount - 1; i++) {
			tasks[i] = tasks[i + 1];
		}

		tasks[taskCount - 1] = null;
		taskCount--;
		return true;
	}

}
