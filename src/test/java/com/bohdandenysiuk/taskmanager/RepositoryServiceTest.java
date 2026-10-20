package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class RepositoryServiceTest {

	@Test
	void userRepositoryUsesIdAsUniqueKey() {
		UserRepository repository = new InMemoryUserRepository();
		User anna = new User(1, "Anna");

		assertTrue(repository.save(anna));
		assertFalse(repository.save(new User(1, "Another Anna")));
		assertSame(anna, repository.findById(1));
		assertNull(repository.findById(999));
	}

	@Test
	void findAllReturnsIndependentList() {
		UserRepository repository = new InMemoryUserRepository();
		repository.save(new User(1, "Anna"));
		repository.save(new User(2, "Mark"));

		List<User> snapshot = repository.findAll();
		snapshot.clear();

		assertEquals(2, repository.findAll().size());
	}

	@Test
	void deleteByIdRemovesOnlyMatchingEntity() {
		TaskRepository repository = new InMemoryTaskRepository();
		repository.save(new Task(1, "First", 100));
		repository.save(new Task(2, "Second", 100));

		assertTrue(repository.deleteById(1));
		assertFalse(repository.deleteById(999));
		assertNull(repository.findById(1));
		assertEquals(1, repository.findAll().size());
	}

	@Test
	void projectServiceRequiresExistingOwner() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		ProjectService service = new ProjectService(users, projects);

		assertFalse(service.createProject(100, "Backend", 1));

		users.save(new User(1, "Anna"));

		assertTrue(service.createProject(100, "Backend", 1));
		assertFalse(service.createProject(100, "Duplicate", 1));
		assertEquals("Backend", projects.findById(100).getName());
	}

	@Test
	void taskServiceCreatesAndAssignsValidTask() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);

		users.save(new User(1, "Anna"));
		projects.save(new Project(100, "Backend", 1));

		assertTrue(service.createTask(1000, "Implement repository", 100));
		assertTrue(service.assignTask(1000, 1));
		assertEquals(1L, tasks.findById(1000).getAssigneeId());
		assertFalse(service.assignTask(1000, 999));
	}

	@Test
	void completeTaskChangesStateOnlyOnce() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);

		projects.save(new Project(100, "Backend", 1));
		tasks.save(new Task(1000, "Implement repository", 100));

		assertTrue(service.completeTask(1000));
		assertTrue(tasks.findById(1000).isDone());
		assertFalse(service.completeTask(1000));
		assertFalse(service.completeTask(999));
	}

	@Test
	void serviceLifecycleRecordsHistory() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);
		users.save(new User(1L, "Oleksii"));
		projects.save(new Project(15L, "PR1", 1L));

		assertTrue(service.createTask(28L, "Learn Maven", 15L));
		assertTrue(service.assignTask(28L, 1L));
		assertTrue(service.startTask(28L));
		assertTrue(service.completeTask(28L));
		assertEquals(TaskStatus.DONE, tasks.findById(28L).getStatus());
		assertEquals(List.of("CREATED", "ASSIGNED: 1", "STARTED", "COMPLETED"), tasks.findById(28L).getHistory());
	}

	@Test
	void failedServiceOperationsDoNotChangeTask() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);
		users.save(new User(1L, "Oleksii"));
		projects.save(new Project(15L, "PR1", 1L));
		assertTrue(service.createTask(28L, "Learn Maven", 15L));
		assertFalse(service.startTask(0));
		assertFalse(service.startTask(-1));
		assertFalse(service.startTask(999));
		assertFalse(service.assignTask(28L, 30L));
		assertEquals(TaskStatus.TODO, tasks.findById(28L).getStatus());
		assertFalse(tasks.findById(28L).isAssigned());
		assertEquals(List.of("CREATED"), tasks.findById(28L).getHistory());
	}

	@Test
	void taskWithoutId() {
		TaskRepository tasks = new InMemoryTaskRepository();
		assertFalse(tasks.save(null));
		assertFalse(tasks.save(new Task("Legacy task")));
		assertEquals(0, tasks.findAll().size());
	}

	@Test
	void repositoryIndependance() {
		UserRepository users1 = new InMemoryUserRepository();
		UserRepository users2 = new InMemoryUserRepository();

		User user = new User(5, "Task");
		users1.save(user);
		assertSame(user, users1.findById(5));
		assertNull(users2.findById(5));
	}

	@Test
	void sortingAndIndependanceOfList() {
		UserRepository users1 = new InMemoryUserRepository();
		Comparator<User> byId = (left, right) -> Long.compare(left.getId(), right.getId());

		User user1 = new User(9, "Task");
		User user2 = new User(2, "Task");
		User user3 = new User(5, "Task");
		users1.save(user1);
		users1.save(user2);
		users1.save(user3);

		List<User> sorted = users1.findAllSorted(byId);

		assertEquals(List.of(user2, user3, user1), sorted);
		assertEquals(List.of(user1, user2, user3), users1.findAll());

		assertSame(user2, sorted.get(0));

		sorted.clear();

		assertTrue(sorted.isEmpty());
		assertEquals(List.of(user1, user2, user3), users1.findAll());

	}

	@Test
	void emptyRepository() {
		UserRepository users1 = new InMemoryUserRepository();
		Comparator<User> byId = (left, right) -> Long.compare(left.getId(), right.getId());

		assertEquals(List.of(), users1.findAllSorted(byId));
	}

	@Test
	void absentComparator() {
		UserRepository users1 = new InMemoryUserRepository();
		assertThrows(IllegalArgumentException.class, () -> users1.findAllSorted(null));
	}

	@Test
	void emptyStatusRepository() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);

		Map<TaskStatus, Integer> map = service.countTasksByStatus();

		assertEquals(0, map.get(TaskStatus.DONE));
		assertEquals(0, map.get(TaskStatus.IN_PROGRESS));
		assertEquals(0, map.get(TaskStatus.TODO));
	}

	@Test
	void differentStatus() {
		UserRepository users = new InMemoryUserRepository();
		ProjectRepository projects = new InMemoryProjectRepository();
		TaskRepository tasks = new InMemoryTaskRepository();
		TaskService service = new TaskService(tasks, projects, users);

		Task user1 = new Task(5L, "Learn Java", 9L);
		Task user2 = new Task(7L, "Learn Java", 9L);
		Task user3 = new Task(9L, "Learn Java", 9L);
		Task user4 = new Task(11L, "Learn Java", 9L);

		tasks.save(user1);
		tasks.save(user2);
		tasks.save(user3);
		tasks.save(user4);

		user1.markDone();
		user2.start();

		Map<TaskStatus, Integer> map1 = service.countTasksByStatus();

		assertEquals(1, map1.get(TaskStatus.DONE));
		assertEquals(1, map1.get(TaskStatus.IN_PROGRESS));
		assertEquals(2, map1.get(TaskStatus.TODO));

		user3.start();

		Map<TaskStatus, Integer> map2 = service.countTasksByStatus();

		assertEquals(1, map2.get(TaskStatus.DONE));
		assertEquals(2, map2.get(TaskStatus.IN_PROGRESS));
		assertEquals(1, map2.get(TaskStatus.TODO));

		map2.clear();

		Map<TaskStatus, Integer> map3 = service.countTasksByStatus();

		assertEquals(1, map3.get(TaskStatus.DONE));
		assertEquals(2, map3.get(TaskStatus.IN_PROGRESS));
		assertEquals(1, map3.get(TaskStatus.TODO));
	}

}
