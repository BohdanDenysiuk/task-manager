package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.List;

public final class TaskManagerSnapshot {
    private final List<User> users;
    private final List<Project> projects;
    private final List<Task> tasks;

    public TaskManagerSnapshot(List<User> users,
            List<Project> projects, List<Task> tasks) {
        if (users == null || projects == null || tasks == null) {
            throw new IllegalArgumentException("Lists cannot be null");
        }

        this.users = new ArrayList<>(users);
        this.projects = new ArrayList<>(projects);
        this.tasks = new ArrayList<>(tasks);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}
