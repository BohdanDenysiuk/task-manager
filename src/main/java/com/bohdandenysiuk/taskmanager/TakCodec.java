package com.bohdandenysiuk.taskmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TakCodec {

	public static String formatTask(Task task) {
		if (task == null || task.getId() == null || task.getProjectId() == null || task.getId() <= 0
				|| task.getProjectId() <= 0 || task.getTitle().contains(";") || task.getTitle().contains("\n")
				|| task.getTitle().contains("\r")) {
			throw new IllegalArgumentException("Error");
		}

		StringJoiner row = new StringJoiner(";");
		row.add("TASK");
		row.add(String.valueOf(task.getId()));
		row.add(task.getTitle());
		row.add(String.valueOf(task.getProjectId()));
		row.add(task.getStatus().name());
		Long assigneeId = task.getAssigneeId();
		row.add(assigneeId == null ? "" : String.valueOf(assigneeId));
		row.add(String.valueOf(task.getHistory().size()));
		for (String occasion : task.getHistory()) {
			if (occasion.contains(";") || occasion.contains("\n") || occasion.contains("\r")) {
				throw new IllegalArgumentException("Error");
			} else {
				row.add(occasion);
			}
		}

		return row.toString();
	}

	public static Task parseTask(String line) {
		if (line == null || line.isBlank() || line.contains("\n") || line.contains("\r")) {
			throw new IllegalArgumentException("Error");
		}

		String[] fields = line.split(";", -1);
		if (fields.length < 8 || !fields[0].equals("TASK")) {
			throw new IllegalArgumentException("Invalid task record");
		}

		Long id = Long.parseLong(fields[1]);
		TaskStatus status = TaskStatus.valueOf(fields[4]);
		Long projectid = Long.parseLong(fields[3]);
		Long assigneeId = (fields[5].equals("")) ? null : Long.parseLong(fields[5]);
		Integer historyCount = Integer.parseInt(fields[6]);
		if (historyCount <= 0 || historyCount != fields.length - 7) {
			throw new IllegalArgumentException("Invalid history count");
		}
		List<String> history = new ArrayList<>();
		for (int i = 7; i < 7 + historyCount; ++i) {
			history.add(fields[i]);
		}

		Task task = Task.restore(id, fields[2], projectid, status, assigneeId, history);
		return task;
	}

	public static void saveSnapshot(Path path, List<User> users, List<Project> projects, List<Task> tasks)
			throws IOException {

		if (path == null || users == null || projects == null || tasks == null) {
			throw new IllegalArgumentException("Error");
		}

		List<String> list = new ArrayList<>();
		list.add("TASK_MANAGER;1");
		for (User user : users) {
			list.add(BasicEntityCodec.formatUser(user));
		}
		for (Project project : projects) {
			list.add(BasicEntityCodec.formatProject(project));
		}
		for (Task task : tasks) {
			list.add(TakCodec.formatTask(task));
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (String value : list) {
				writer.write(value);
				writer.newLine();
			}
		}

	}

	public static TaskManagerSnapshot loadSnapshot(Path path) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException("Error");
		}

		List<User> userList = new ArrayList<>();
		List<Task> taskList = new ArrayList<>();
		List<Project> projectList = new ArrayList<>();

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			if (!("TASK_MANAGER;1".equals(reader.readLine()))) {
				throw new IllegalArgumentException("Error");
			}

			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(";", -1);
				if (fields[0].equals("TASK")) {
					taskList.add(parseTask(line));
				} else if (fields[0].equals("USER")) {
					userList.add(BasicEntityCodec.parseUser(line));
				} else if (fields[0].equals("PROJECT")) {
					projectList.add(BasicEntityCodec.parseProject(line));
				} else {
				    throw new IllegalArgumentException("Invalid snapshot record");
				}
			}

			return new TaskManagerSnapshot(userList, projectList, taskList);
		}
	}

}
