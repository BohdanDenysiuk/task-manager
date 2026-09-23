package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

	@Test
	void newTaskHasCorrectInitialState() {
		Task newTask = new Task("Learn Java");

		assertEquals("Learn Java", newTask.getTitle());
		assertFalse(newTask.isDone());

	}

	@Test
	void markDoneChangesStatusToDone() {
		Task newTask = new Task("Learn Java");

		assertFalse(newTask.isDone());
		newTask.markDone();
		assertTrue(newTask.isDone());
	}

	@Test
	void taskTitleIsEqualNull() {
		assertThrows(IllegalArgumentException.class, () -> new Task(null));
	}

	@Test
	void taskTitleIsBlank() {
		assertThrows(IllegalArgumentException.class, () -> new Task("  "));
	}

	@Test
	void taskObjectIsSuccesfullyCreated() {
		Task task = new Task("Learning Java");

		assertFalse(task.isDone());
		assertEquals("Learning Java", task.getTitle());
	}
}
