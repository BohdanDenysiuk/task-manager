package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TaskManagerTest {

	@Test
	void defaultConstructorCreatesCapacityTen() {
		TaskManager test = new TaskManager();

		assertEquals(10, test.getCapacity());
		assertEquals(0, test.getTaskCount());
	}

	@Test
	void negativeCapacityBecomesZero() {
		TaskManager test = new TaskManager(-5);

		assertEquals(0, test.getCapacity());
		assertFalse(test.addTask("Task"));
	}

	@Test
	void addTaskAddsValidTask() {
		TaskManager test = new TaskManager();

		assertTrue(test.addTask("Learn Java"));
		assertEquals(1, test.getTaskCount());
		assertEquals("Learn Java", test.getTask(0).getTitle());
	}

	@Test
	void addTaskRejectsNullAndBlankTitles() {
		TaskManager test = new TaskManager();

		assertFalse(test.addTask(null));
		assertFalse(test.addTask(""));
		assertFalse(test.addTask("  "));
		assertEquals(0, test.getTaskCount());
	}

	@Test
	void findTaskReturnsExistingTask() {
		TaskManager manager = new TaskManager();
		manager.addTask("Learn Java");

		assertSame(manager.getTask(0), manager.findTask("Learn Java"));
	}

	@Test
	void addTaskRejectsTaskWhenStorageIsFull() {
		TaskManager test = new TaskManager(1);

		assertTrue(test.addTask("First"));
		assertFalse(test.addTask("Second"));

		assertEquals(1, test.getTaskCount());
		assertNull(test.findTask("Second"));
	}

	@Test
	void findTaskReturnsNullForMissingTask() {
		TaskManager test = new TaskManager();

		assertEquals(null, test.findTask("Missing"));
		assertEquals(null, test.findTask(null));
		assertEquals(null, test.findTask("   "));
	}

	@Test
	void completeTaskMarksExistingTaskAsDone() {
		TaskManager test = new TaskManager();
		test.addTask("Learn Java");

		assertTrue(test.completeTask("Learn Java"));
		assertTrue(test.findTask("Learn Java").isDone());
	}

	@Test
	void completeTaskRejectsRepeatedCompletion() {
		TaskManager test = new TaskManager();

		test.addTask("Learn Java");

		assertTrue(test.completeTask("Learn Java"));
		assertFalse(test.completeTask("Learn Java"));
		assertEquals(1, test.countDoneTasks());

	}

	@Test
	void removeTaskFromFullStorage() {
		TaskManager test = new TaskManager(2);

		test.addTask("A");
		test.addTask("B");

		assertTrue(test.removeTask("A"));
		assertEquals(1, test.getTaskCount());
		assertEquals("B", test.getTask(0).getTitle());
		assertEquals(null, test.getTask(1));
	}

	@Test
	void removeMiddleTaskShiftsFollowingTasks() {
		TaskManager test = new TaskManager();

		test.addTask("A");
		test.addTask("B");
		test.addTask("C");

		assertTrue(test.removeTask("B"));

		assertEquals(2, test.getTaskCount());
		assertEquals("A", test.getTask(0).getTitle());
		assertEquals("C", test.getTask(1).getTitle());
		assertNull(test.getTask(2));

	}

	@Test
	void removeMissingTaskDoesNotChangeManager() {
		TaskManager test = new TaskManager(2);

		test.addTask("A");
		test.addTask("B");

		Task first = test.getTask(0);
		Task second = test.getTask(1);

		assertFalse(test.removeTask("Missing"));

		assertEquals(2, test.getTaskCount());
		assertSame(first, test.getTask(0));
		assertSame(second, test.getTask(1));

	}

	@Test
	void countDoneTasksReturnsNumberOfCompletedTasks() {
		TaskManager test = new TaskManager();

		test.addTask("A");
		test.addTask("B");
		test.addTask("C");

		test.getTask(0).markDone();
		test.getTask(1).markDone();

		assertEquals(2, test.countDoneTasks());
	}

	@Test
	void defaultMatcherUsesExactComparison() {
		TaskManager manager = new TaskManager();
		manager.addTask("Learn Java");

		assertSame(manager.getTask(0), manager.findTask("Learn Java"));
		assertNull(manager.findTask("learn java"));
	}

	@Test
	void caseInsensitiveMatcherIgnoresLetterCase() {
		TaskManager manager = new TaskManager(10, new CaseInsensitiveTitleMatcher());
		manager.addTask("Learn Java");
		Task original = manager.getTask(0);

		assertSame(original, manager.findTask("LEARN JAVA"));

	}

	@Test
	void completeTaskUsesConfiguredMatcher() {
		TaskManager manager = new TaskManager(10, new CaseInsensitiveTitleMatcher());
		manager.addTask("Learn Java");
		assertTrue(manager.completeTask("LEARN JAVA"));
		assertTrue(manager.getTask(0).isDone());
	}

	@Test
	void removeTaskUsesConfiguredMatcher() {
		TaskManager manager = new TaskManager(10, new CaseInsensitiveTitleMatcher());
		manager.addTask("Learn Java");
		assertTrue(manager.removeTask("LEARN JAVA"));

		assertNull(manager.getTask(0));
		assertEquals(0, manager.getTaskCount());

	}

}
