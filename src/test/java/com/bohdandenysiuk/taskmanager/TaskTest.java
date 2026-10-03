package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

	@Test
	void startChangesTodoToInProgress() {
		Task task = new Task("Learning Java");

		assertEquals(TaskStatus.TODO, task.getStatus());
		assertTrue(task.start());
		assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
		assertFalse(task.isDone());
		assertEquals(List.of("CREATED", "STARTED"), task.getHistory());
	}

	@Test
	void repeatedStartDoesNotChangeStatus() {
		Task task = new Task("Learning Java");

		assertTrue(task.start());
		assertFalse(task.start());
		assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
		assertEquals(List.of("CREATED", "STARTED"), task.getHistory());
	}

	@Test
	void completedTaskCannotStart() {
		Task task = new Task("Learning Java");
		task.markDone();

		assertFalse(task.start());
		assertEquals(TaskStatus.DONE, task.getStatus());
		assertTrue(task.isDone());
	}

	@Test
	void bothConstructorsRecordCreation() {
		Task task1 = new Task("Learning Java");
		Task task2 = new Task(28L, "Learning Java", 1L);

		assertEquals(TaskStatus.TODO, task1.getStatus());
		assertEquals(TaskStatus.TODO, task2.getStatus());
		assertEquals(List.of("CREATED"), task1.getHistory());
		assertEquals(List.of("CREATED"), task2.getHistory());
	}

	@Test
	void repeatedCompletionDoesNotDuplicateHistory() {
		Task task1 = new Task("Learning Java");
		task1.markDone();
		task1.markDone();

		assertEquals(TaskStatus.DONE, task1.getStatus());
		assertEquals(List.of("CREATED", "COMPLETED"), task1.getHistory());
	}

	@Test
	void assignmentHistoryContainsOnlyChanges() {
		Task task1 = new Task("Learning Java");
		task1.assignTo(1);
		task1.assignTo(1);
		task1.assignTo(2);
		task1.unassign();
		task1.unassign();

		assertEquals(List.of("CREATED", "ASSIGNED: 1", "ASSIGNED: 2", "UNASSIGNED"), task1.getHistory());
		assertEquals(TaskStatus.TODO, task1.getStatus());
		assertFalse(task1.isAssigned());
	}

	@Test
	void historyIsIndependentSnapshot() {
		Task task1 = new Task("Learning Java");
		assertEquals(List.of("CREATED"), task1.getHistory());
		task1.getHistory().clear();
		assertEquals(List.of("CREATED"), task1.getHistory());
		List<String> testHistory = task1.getHistory();
		task1.start();
		assertEquals(List.of("CREATED"), testHistory);
		assertEquals(List.of("CREATED", "STARTED"), task1.getHistory());
	}

	@Test
	void assignToIdThrowsException() {
		Task task1 = new Task("Learning Java");
		task1.assignTo(7L);
		assertEquals(TaskStatus.TODO, task1.getStatus());
		assertEquals(List.of("CREATED", "ASSIGNED: 7"), task1.getHistory());
		assertThrows(IllegalArgumentException.class, () -> task1.assignTo(0));
		assertEquals(List.of("CREATED", "ASSIGNED: 7"), task1.getHistory());
		assertEquals(7L, task1.getAssigneeId());
		assertEquals(TaskStatus.TODO, task1.getStatus());
	}

}
