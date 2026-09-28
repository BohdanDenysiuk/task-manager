package com.bohdandenysiuk.taskmanager;

import java.util.List;

public interface TaskRepository {

	boolean save(Task task);

	Task findById(long id);

	List<Task> findAll();

	boolean deleteById(long id);
}
