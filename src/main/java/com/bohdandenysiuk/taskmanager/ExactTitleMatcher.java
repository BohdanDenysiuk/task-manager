package com.bohdandenysiuk.taskmanager;

public class ExactTitleMatcher implements TaskMatcher {

	@Override
	public boolean matches(Task task, String title) {
		return task.getTitle().equals(title);
	}

}
