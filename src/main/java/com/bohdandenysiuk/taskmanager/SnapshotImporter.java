package com.bohdandenysiuk.taskmanager;

import java.io.IOException;
import java.nio.file.Path;

public class SnapshotImporter {

	public static TaskManagerState importFrom(Path path) throws IOException {
		TaskManagerSnapshot snapshot = TakCodec.loadSnapshot(path);
		SnapshotValidator.validateSnapshot(snapshot);

		UserRepository newUsers = new InMemoryUserRepository();
		ProjectRepository newProjects = new InMemoryProjectRepository();
		TaskRepository newTasks = new InMemoryTaskRepository();

		for (User user : snapshot.getUsers()) {
			if (!newUsers.save(user)) {
				throw new IllegalArgumentException("Cannot import user");
			}
		}

		for (Project project : snapshot.getProjects()) {
			if (!newProjects.save(project)) {
				throw new IllegalArgumentException("Cannot import project");
			}
		}

		for (Task task : snapshot.getTasks()) {
			if (!newTasks.save(task)) {
				throw new IllegalArgumentException("Cannot import task");
			}
		}

		return new TaskManagerState(newUsers, newProjects, newTasks);

	}

}
