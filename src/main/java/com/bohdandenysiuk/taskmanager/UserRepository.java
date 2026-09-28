package com.bohdandenysiuk.taskmanager;

import java.util.List;

public interface UserRepository {

	boolean save(User user);

	User findById(long id);

	List<User> findAll();

	boolean deleteById(long id);

}
