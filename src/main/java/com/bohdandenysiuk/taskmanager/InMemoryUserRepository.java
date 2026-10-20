package com.bohdandenysiuk.taskmanager;

public class InMemoryUserRepository extends InMemoryRepository<User> implements UserRepository {

	@Override
	protected Long getEntityId(User entity) {
		return entity.getId();

	}

}
