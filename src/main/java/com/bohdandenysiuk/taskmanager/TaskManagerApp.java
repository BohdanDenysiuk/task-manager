package com.bohdandenysiuk.taskmanager;

import java.util.Scanner;

public class TaskManagerApp {

	public static void main(String[] args) {
		System.out.println("Task Manager started");

		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		TaskManager taskManager = new TaskManager();

		printMenu();

		while (running) {
			System.out.print("Enter command: ");
			String command = scanner.nextLine().trim().toLowerCase();
			if (command.isEmpty()) {
				System.out.println("Command cannot be empty");
				continue;
			}

			switch (command) {
			case "exit":
				System.out.println("You exited the program");
				running = false;
				break;
			case "create":
				createTask(scanner, taskManager);
				break;
			case "list":
				listTasks(taskManager);
				break;
			case "delete":
				deleteTask(scanner, taskManager);
				break;
			case "complete":
				System.out.println("Enter the task title to mark as done: ");
				String title = scanner.nextLine().strip();
				boolean completed = taskManager.completeTask(title);

				if (completed) {
					System.out.println("Task completed");
				} else {
					System.out.println("Task not found or already completed");
				}
				break;
			default:
				System.out.println("Unknown command");
				break;
			}

		}

		scanner.close();

	}

	static void printMenu() {
		System.out.println();
		System.out.println("Available commands:");
		System.out.println("create - create a task");
		System.out.println("list - show all tasks");
		System.out.println("delete - delete a task");
		System.out.println("complete - mark a task as done");
		System.out.println("exit - close the program");
	}

	static void listTasks(TaskManager taskManager) {
		if (taskManager.getTaskCount() == 0) {
			System.out.println("No tasks found");
			return;
		}

		System.out.println("Tasks:");
		for (int i = 0; i < taskManager.getTaskCount(); ++i) {
			Task task = taskManager.getTask(i);
			System.out.println((i + 1) + ". [" + (task.isDone() ? '×' : ' ') + "] " + task.getTitle());
		}
	}

	static void createTask(Scanner scanner, TaskManager taskManager) {
		System.out.print(" Enter task title: ");
		String title = scanner.nextLine().trim();

		while (title.isEmpty()) {
			System.out.print(" Task title cannot be an empty line! \n Try again: ");
			title = scanner.nextLine().trim();
		}

		if (taskManager.addTask(title)) {
			System.out.println(" Task created: " + title);
			System.out.println(" Task count: " + taskManager.getTaskCount());
		} else {
			System.out.println("Task could not be created");
		}
	}

	static void deleteTask(Scanner scanner, TaskManager taskManager) {
		if (taskManager.getTaskCount() == 0) {
			System.out.println("There are no tasks to delete");
			return;
		}

		System.out.print("Please enter the task name: ");
		String taskName = scanner.nextLine().strip();
		if (taskManager.removeTask(taskName)) {
			System.out.println("Task: " + taskName + " was successfully removed");
		} else {
			System.out.println("Task could not be removed");
		}
	}

}
