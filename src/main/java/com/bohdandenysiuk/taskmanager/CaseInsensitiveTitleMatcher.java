package com.bohdandenysiuk.taskmanager;

public class CaseInsensitiveTitleMatcher implements TaskMatcher {

	@Override
	public boolean matches(Task task, String title) {
		return task.getTitle().equalsIgnoreCase(title);
	}

}
