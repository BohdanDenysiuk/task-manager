package com.bohdandenysiuk.taskmanager;

public class InMemoryTaskRepository extends InMemoryRepository<Task> implements TaskRepository {

	@Override
	protected Long getEntityId(Task entity) {
		return entity.getId();
	}

}
