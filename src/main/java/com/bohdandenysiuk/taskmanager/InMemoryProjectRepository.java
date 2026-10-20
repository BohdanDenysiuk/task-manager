package com.bohdandenysiuk.taskmanager;

public class InMemoryProjectRepository extends InMemoryRepository<Project> implements ProjectRepository {
	@Override
	protected Long getEntityId(Project entity) {
		return entity.getId();
	}

}
