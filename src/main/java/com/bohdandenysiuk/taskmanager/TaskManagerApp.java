package com.bohdandenysiuk.taskmanager;

import java.util.Scanner;

public class TaskManagerApp {

	public static void main(String[] args) {
		System.out.println("Task Manager started");

		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		String[] tasks = new String[10];
		int taskCount = 0;

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
				taskCount = createTask(scanner, tasks, taskCount);
				break;
			case "list":
				listTasks(tasks, taskCount);
				break;
			default:
				System.out.println("Unknown command");
				break;
			}

		}

		scanner.close();

	}

	static void listTasks(String[] tasks, int taskCount) {
		if (taskCount == 0) {
			System.out.println("No tasks found");
			return;
		}

		System.out.println("Tasks:");
		for (int i = 0; i < taskCount; ++i) {
			System.out.println((i + 1) + ". " + tasks[i]);
		}
	}

	static int createTask(Scanner scanner, String[] tasks, int taskCount) {
		if (taskCount == tasks.length) {
			System.out.println(" Task storage is full");
			return taskCount;
		}

		System.out.print(" Enter task title: ");
		String title = scanner.nextLine().trim();

		while (title.isEmpty()) {
			System.out.print(" Task title cannot be an empty line! \n Try again: ");
			title = scanner.nextLine().trim();
		}

		tasks[taskCount] = title;
		System.out.println(" Task created: " + tasks[taskCount]);

		taskCount++;
		System.out.println(" Task count: " + taskCount);
		return taskCount;
	}

	static void printMenu() {
		System.out.println();
		System.out.println("Available commands:");
		System.out.println("create - create a task");
		System.out.println("list   - show all tasks");
		System.out.println("exit   - close the program");
	}

}
