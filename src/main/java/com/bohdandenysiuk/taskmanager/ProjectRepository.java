package com.bohdandenysiuk.taskmanager;

import java.util.List;

public interface ProjectRepository {

	boolean save(Project project);

	Project findById(long id);

	List<Project> findAll();

	boolean deleteById(long id);
}
