package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryTaskRepository implements TaskRepository {

	private final List<Task> tasks = new ArrayList<>();

	@Override
	public boolean save(Task task) {
		if (task == null || task.getId() == null || findById(task.getId()) != null) {
			return false;
		}
		tasks.add(task);
		return true;
	}

	@Override
	public Task findById(long id) {
		for (Task task : tasks) {
			Long taskId = task.getId();

			if (taskId != null && taskId.longValue() == id) {
				return task;
			}
		}

		return null;
	}

	@Override
	public List<Task> findAll() {
		return new ArrayList<>(tasks);
	}

	@Override
	public boolean deleteById(long id) {
		Iterator<Task> iterator = tasks.iterator();

		while (iterator.hasNext()) {
			Task task = iterator.next();
			Long taskId = task.getId();

			if (taskId != null && taskId.longValue() == id) {
				iterator.remove();
				return true;
			}
		}

		return false;
	}

}
