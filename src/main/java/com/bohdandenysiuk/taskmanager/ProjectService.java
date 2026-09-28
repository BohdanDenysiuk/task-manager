package com.bohdandenysiuk.taskmanager;

public class ProjectService {

	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;

	public ProjectService(UserRepository userRepository, ProjectRepository projectRepository) {
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
	}

	public boolean createProject(long id, String name, long ownerId) {
		if (id <= 0 || ownerId <= 0 || name == null || name.isBlank()) {
			return false;
		}

		if (userRepository.findById(ownerId) == null) {
			return false;
		}

		Project project = new Project(id, name, ownerId);
		return projectRepository.save(project);
	}

}
