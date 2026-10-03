package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

}
