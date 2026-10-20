package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Assistant-supplied synchronization regressions. */
class ReportPracticeTest {
    @TempDir
    Path directory;

    @Test
    void reportHasExplicitSectionOrderAndKeepsDuplicateDoneTitles() throws Exception {
        List<TaskReportRow> rows = List.of(new TaskReportRow("SQL", TaskStatus.DONE),
                new TaskReportRow("Java", TaskStatus.DONE),
                new TaskReportRow("SQL", TaskStatus.DONE),
                new TaskReportRow("Git", TaskStatus.TODO));

        assertEquals(List.of("TODO: 1", "IN_PROGRESS: 0", "DONE: 3", "", "Completed:",
                "Java", "SQL", "SQL"), ReportPractice.buildReport(rows));
        assertEquals(4, rows.size());
    }

    @Test
    void emptyReportContainsAllThreeZeroCounters() throws Exception {
        assertEquals(List.of("TODO: 0", "IN_PROGRESS: 0", "DONE: 0", "", "Completed:"),
                ReportPractice.buildReport(List.of()));
    }

    @Test
    void reportRowKeepsCapturedStatusAfterOriginalTaskChanges() {
        Task task = new Task(10, "Java", 3);
        TaskReportRow row = new TaskReportRow(task.getTitle(), task.getStatus());

        task.start();

        assertEquals(TaskStatus.TODO, row.getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void nullRowsAreRejectedAndWorkerFailureRetainsCause() {
        assertThrows(IllegalArgumentException.class, () -> ReportPractice.buildReport(null));
        ExecutionException failure = assertThrows(ExecutionException.class,
                () -> ReportPractice.buildReport(Collections.singletonList(null)));

        assertInstanceOf(NullPointerException.class, failure.getCause());
    }

    @Test
    void reportComputationFailurePreservesExistingFile() throws Exception {
        Path file = directory.resolve("report.txt");
        Files.writeString(file, "old report", StandardCharsets.UTF_8);

        assertThrows(ExecutionException.class,
                () -> ReportPractice.exportReport(file, Collections.singletonList(null)));

        assertEquals("old report", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void exportWritesComputedReportAsUtf8Lines() throws Exception {
        Path file = directory.resolve("report.txt");
        List<TaskReportRow> rows = List.of(new TaskReportRow("Вивчити Java", TaskStatus.DONE));

        ReportPractice.exportReport(file, rows);

        assertEquals(List.of("TODO: 0", "IN_PROGRESS: 0", "DONE: 1", "", "Completed:",
                "Вивчити Java"), Files.readAllLines(file, StandardCharsets.UTF_8));
    }
}
