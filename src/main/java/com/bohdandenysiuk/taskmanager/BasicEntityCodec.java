package com.bohdandenysiuk.taskmanager;

public final class BasicEntityCodec {
	private BasicEntityCodec() {
	}

	public static String formatUser(User user) {
		if (user == null || user.getId() <= 0) {
			throw new IllegalArgumentException("Invalid user");
		}

		requireText(user.getName());
		return "USER;" + user.getId() + ";" + user.getName();
	}

	public static String formatProject(Project project) {
		if (project == null || project.getId() <= 0 || project.getOwnerId() <= 0) {
			throw new IllegalArgumentException("Invalid project");
		}

		requireText(project.getName());
		return "PROJECT;" + project.getId() + ";" + project.getName() + ";" + project.getOwnerId();
	}

	private static void requireText(String text) {
		if (text == null || text.isBlank() || text.contains(";") || text.contains("\n") || text.contains("\r")) {
			throw new IllegalArgumentException("Invalid text");
		}
	}

	public static User parseUser(String line) {
		if (line == null || line.isBlank() || line.contains("\n") || line.contains("\r")) {
			throw new IllegalArgumentException("Invalid user record");
		}

		String[] fields = line.split(";", -1);

		if (fields.length != 3 || !fields[0].equals("USER")) {
			throw new IllegalArgumentException("Invalid user record");
		}

		return new User(Long.parseLong(fields[1]), fields[2]);
	}

	public static Project parseProject(String line) {
		if (line == null || line.isBlank() || line.contains("\n") || line.contains("\r")) {
			throw new IllegalArgumentException("Invalid project record");
		}

		String[] fields = line.split(";", -1);

		if (fields.length != 4 || !fields[0].equals("PROJECT")) {
			throw new IllegalArgumentException("Invalid p record");
		}

		return new Project(Long.parseLong(fields[1]), fields[2], Long.parseLong(fields[3]));
	}
}
