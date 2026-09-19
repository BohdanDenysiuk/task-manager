package com.bohdandenysiuk.taskmanager;

public interface TaskMatcher {

	boolean matches(Task task, String title);
}
