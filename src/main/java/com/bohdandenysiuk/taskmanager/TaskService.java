package com.bohdandenysiuk.taskmanager;

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
}
