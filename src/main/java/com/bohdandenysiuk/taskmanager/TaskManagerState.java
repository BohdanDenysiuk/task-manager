package com.bohdandenysiuk.taskmanager;

public final class TaskManagerState {
    private final UserRepository users;
    private final ProjectRepository projects;
    private final TaskRepository tasks;

    public TaskManagerState(UserRepository users,
            ProjectRepository projects, TaskRepository tasks) {
        if (users == null || projects == null || tasks == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }

        this.users = users;
        this.projects = projects;
        this.tasks = tasks;
    }

    public UserRepository getUserRepository() {
        return users;
    }

    public ProjectRepository getProjectRepository() {
        return projects;
    }

    public TaskRepository getTaskRepository() {
        return tasks;
    }
}
