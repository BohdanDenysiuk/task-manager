package com.bohdandenysiuk.taskmanager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SnapshotValidator {

	public static void validateSnapshot(TaskManagerSnapshot snapshot) {
		if (snapshot == null) {
			throw new IllegalArgumentException("Error");
		}

		Set<Long> userIds = new HashSet<>();
		List<User> users = snapshot.getUsers();
		for (User user : users) {
			if (user == null || user.getId() <= 0) {
				throw new IllegalArgumentException("Invalid user");
			}
			if (!userIds.add(user.getId())) {
				throw new IllegalArgumentException("Error");
			}
		}

		Set<Long> projectIds = new HashSet<>();
		List<Project> projects = snapshot.getProjects();
		for (Project project : projects) {
			if (project == null || project.getId() <= 0) {
				throw new IllegalArgumentException("Invalid project");
			}
			if (!projectIds.add(project.getId())) {
				throw new IllegalArgumentException("Error");
			}

			if (!(userIds.contains(project.getOwnerId()))) {
				throw new IllegalArgumentException("Error");
			}
		}

		Set<Long> taskIds = new HashSet<>();
		List<Task> tasks = snapshot.getTasks();
		for (Task task : tasks) {
			if (task == null || task.getId() == null || task.getId() <= 0 || task.getProjectId() == null
					|| task.getProjectId() <= 0) {
				throw new IllegalArgumentException("Invalid task");
			}
			if (!taskIds.add(task.getId())) {
				throw new IllegalArgumentException("Error");
			}

			if (!(projectIds.contains(task.getProjectId()))) {
				throw new IllegalArgumentException("Error");
			}

			if (task.getAssigneeId() != null && !userIds.contains(task.getAssigneeId())) {
				throw new IllegalArgumentException("Error");
			}
		}

	}
}
