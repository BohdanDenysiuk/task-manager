package com.bohdandenysiuk.taskmanager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ReportPractice {
	private ReportPractice() {
	}

	public static Map<TaskStatus, Integer> countStatuses(List<TaskReportRow> rows) {
		Map<TaskStatus, Integer> counts = new HashMap<>();
		counts.put(TaskStatus.TODO, 0);
		counts.put(TaskStatus.IN_PROGRESS, 0);
		counts.put(TaskStatus.DONE, 0);
		for (TaskReportRow row : rows) {
			counts.put(row.getStatus(), counts.get(row.getStatus()) + 1);
		}
		return counts;
	}

	public static List<String> doneTitles(List<TaskReportRow> rows) {
		List<String> titles = new ArrayList<>();
		for (TaskReportRow row : rows) {
			if (row.getStatus() == TaskStatus.DONE) {
				titles.add(row.getTitle());
			}
		}
		titles.sort(String::compareTo);
		return titles;
	}

	public static List<String> toLines(Map<TaskStatus, Integer> counts, List<String> titles) {
		List<String> lines = new ArrayList<>();
		lines.add("TODO: " + counts.get(TaskStatus.TODO));
		lines.add("IN_PROGRESS: " + counts.get(TaskStatus.IN_PROGRESS));
		lines.add("DONE: " + counts.get(TaskStatus.DONE));
		lines.add("");
		lines.add("Completed:");
		lines.addAll(titles);
		return lines;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		List<TaskReportRow> rows = List.of(new TaskReportRow("SQL", TaskStatus.TODO),
				new TaskReportRow("Java", TaskStatus.DONE), new TaskReportRow("Git", TaskStatus.DONE));

		if (args.length > 0 && args[0].equals("helpers")) {
			List<String> lines = toLines(countStatuses(rows), doneTitles(rows));
			List<String> expected = List.of("TODO: 1", "IN_PROGRESS: 0", "DONE: 2", "", "Completed:", "Git", "Java");
			if (!lines.equals(expected)) {
				throw new AssertionError(lines);
			}
			lines.forEach(System.out::println);
			System.out.println("Mentor helpers PASS; buildReport remains TODO");
			return;
		}

		List<String> report = buildReport(rows);
		report.forEach(System.out::println);
	}

	public static List<String> buildReport(List<TaskReportRow> rows) throws InterruptedException, ExecutionException {
		if (rows == null) {
			throw new IllegalArgumentException("Error");
		}

		try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
			Future<Map<TaskStatus, Integer>> countsFuture = executor.submit(() -> countStatuses(rows));
			Future<List<String>> titlesFuture = executor.submit(() -> doneTitles(rows));

			List<String> result = toLines(countsFuture.get(), titlesFuture.get());
			return result;
		}
	}

	public static void exportReport(Path path, List<TaskReportRow> rows)
			throws IOException, InterruptedException, ExecutionException {

		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}

		List<String> lines = buildReport(rows);

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}
}
