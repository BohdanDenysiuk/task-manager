package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryProjectRepository implements ProjectRepository {

	private final List<Project> projects = new ArrayList<>();

	@Override
	public boolean save(Project project) {
		if (project == null || findById(project.getId()) != null) {
			return false;
		}
		projects.add(project);
		return true;
	}

	@Override
	public Project findById(long id) {
		for (Project project : projects) {
			if (id == project.getId()) {
				return project;
			}
		}
		return null;
	}

	@Override
	public List<Project> findAll() {
		return new ArrayList<>(projects);
	}

	@Override
	public boolean deleteById(long id) {
		Iterator<Project> iterator = projects.iterator();

		while (iterator.hasNext()) {
			Project project = iterator.next();

			if (id == project.getId()) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

}
