package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryUserRepository implements UserRepository {

	private final List<User> users = new ArrayList<>();

	@Override
	public boolean save(User user) {
		if (user == null || findById(user.getId()) != null) {
			return false;
		}

		users.add(user);
		return true;
	}

	@Override
	public User findById(long id) {
		for (User user : users) {
			if (id == user.getId()) {
				return user;
			}
		}
		return null;
	}

	@Override
	public List<User> findAll() {
		return new ArrayList<>(users);
	}

	@Override
	public boolean deleteById(long id) {
		Iterator<User> iterator = users.iterator();

		while (iterator.hasNext()) {
			User user = iterator.next();

			if (user.getId() == id) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
}
