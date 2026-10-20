package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskService {

	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
			UserRepository userRepository) {

		this.taskRepository = taskRepository;
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	public boolean createTask(long id, String title, long projectId) {
		if (id <= 0 || projectId <= 0 || title == null || title.isBlank()
				|| projectRepository.findById(projectId) == null) {
			return false;
		}

		Task task = new Task(id, title, projectId);
		return taskRepository.save(task);

	}

	public boolean assignTask(long taskId, long userId) {
		if (taskId <= 0 || userId <= 0) {
			return false;
		}

		Task task = taskRepository.findById(taskId);
		User user = userRepository.findById(userId);

		if (task == null || user == null) {
			return false;
		}

		task.assignTo(userId);
		return true;

	}

	public boolean completeTask(long taskId) {
		if (taskId <= 0) {
			return false;
		}

		Task task = taskRepository.findById(taskId);

		if (task == null) {
			return false;
		}

		if (task.isDone()) {
			return false;
		}

		task.markDone();
		return true;
	}

	public boolean startTask(long taskId) {
		if (taskId <= 0) {
			return false;
		}

		Task task = this.taskRepository.findById(taskId);
		if (task != null) {
			return task.start();
		}
		return false;
	}

	public Map<TaskStatus, Integer> countTasksByStatus() {
		Map<TaskStatus, Integer> map = new HashMap<>();
		map.put(TaskStatus.TODO, 0);
		map.put(TaskStatus.DONE, 0);
		map.put(TaskStatus.IN_PROGRESS, 0);

		for (Task task : this.taskRepository.findAll()) {
			map.put(task.getStatus(), map.getOrDefault(task.getStatus(), 0) + 1);
		}

		return map;
	}

	public List<Task> findTasksByStatusSorted(TaskStatus status, Comparator<? super Task> order) {
		if (status == null || order == null) {
			throw new IllegalArgumentException("Status or order cannot be null");
		}

		List<Task> result = new ArrayList<>();
		for (Task task : this.taskRepository.findAll()) {
			if (task.getStatus() == status) {
				result.add(task);
			}
		}

		result.sort(order);
		return result;
	}

	public List<Task> findTasks(Predicate<? super Task> rule) {
		if (rule == null) {
			throw new IllegalArgumentException("rule is null");
		}

		List<Task> result = new ArrayList<>();

		for (Task task : taskRepository.findAll()) {
			if (rule.test(task)) {
				result.add(task);
			}
		}

		return result;
	}

	public List<String> findTitlesByStatusSorted(TaskStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("Status cannot be null");
		}

		List<String> result = this.taskRepository.findAll().stream().filter(task -> task.getStatus() == status)
				.map(Task::getTitle).sorted().collect(Collectors.toCollection(ArrayList::new));
		return result;
	}

	public Map<TaskStatus, List<Task>> groupTasksByStatus() {
		Map<TaskStatus, List<Task>> result = this.taskRepository.findAll().stream()
				.collect(Collectors.groupingBy(Task::getStatus));
		return result;
	}

	public List<String> getAllTaskHistory() {
		List<String> result = this.taskRepository.findAll().stream().flatMap(task -> task.getHistory().stream())
				.collect(Collectors.toCollection(ArrayList::new));
		return result;
	}

}
