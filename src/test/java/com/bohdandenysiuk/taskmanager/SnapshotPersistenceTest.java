package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Assistant-supplied synchronization regressions, not student exercise answers. */
class SnapshotPersistenceTest {
    @TempDir
    Path directory;

    @Test
    void taskRecordRoundTripsFullState() {
        Task original = new Task(10, "Learn Java", 3);
        original.assignTo(7);
        original.start();
        original.markDone();

        Task loaded = TakCodec.parseTask(TakCodec.formatTask(original));

        assertNotSame(original, loaded);
        assertEquals(original.getId(), loaded.getId());
        assertEquals(original.getTitle(), loaded.getTitle());
        assertEquals(original.getProjectId(), loaded.getProjectId());
        assertEquals(original.getAssigneeId(), loaded.getAssigneeId());
        assertEquals(TaskStatus.DONE, loaded.getStatus());
        assertEquals(original.getHistory(), loaded.getHistory());
    }

    @Test
    void taskWithoutAssigneeUsesAnEmptyField() {
        Task original = new Task(10, "Java", 3);
        String record = TakCodec.formatTask(original);

        assertEquals("", record.split(";", -1)[5]);
        assertNull(TakCodec.parseTask(record).getAssigneeId());
    }

    @Test
    void restoreCopiesHistoryWithoutAddingAnotherCreatedEvent() {
        List<String> history = new ArrayList<>(List.of("CREATED", "STARTED"));
        Task restored = Task.restore(10, "Java", 3, TaskStatus.IN_PROGRESS, null, history);

        history.clear();
        restored.getHistory().clear();

        assertEquals(List.of("CREATED", "STARTED"), restored.getHistory());
        assertEquals(TaskStatus.IN_PROGRESS, restored.getStatus());
    }

    @Test
    void taskParserRejectsIncorrectHistoryCount() {
        assertThrows(IllegalArgumentException.class,
                () -> TakCodec.parseTask("TASK;10;Java;3;TODO;;2;CREATED"));
        assertThrows(IllegalArgumentException.class,
                () -> TakCodec.parseTask("TASK;10;Java;3;TODO;;1;CREATED;STARTED"));
    }

    @Test
    void savingAndImportingReconstructsIndependentRepositories() throws IOException {
        Path file = directory.resolve("snapshot.txt");
        User user = new User(7, "Анна");
        Project project = new Project(3, "Backend", 7);
        Task task = new Task(10, "Вивчити Java", 3);
        task.assignTo(7);
        task.start();

        TakCodec.saveSnapshot(file, List.of(user), List.of(project), List.of(task));
        TaskManagerState imported = SnapshotImporter.importFrom(file);

        assertEquals("TASK_MANAGER;1", Files.readAllLines(file, StandardCharsets.UTF_8).get(0));
        assertEquals("Анна", imported.getUserRepository().findById(7).getName());
        assertEquals(7L, imported.getProjectRepository().findById(3).getOwnerId());
        Task restored = imported.getTaskRepository().findById(10);
        assertNotSame(task, restored);
        assertEquals(TaskStatus.IN_PROGRESS, restored.getStatus());
        assertEquals(7L, restored.getAssigneeId());
        assertEquals(task.getHistory(), restored.getHistory());
        imported.getTaskRepository().deleteById(10);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void savingSmallerSnapshotTruncatesPreviousContent() throws IOException {
        Path file = directory.resolve("snapshot.txt");
        TakCodec.saveSnapshot(file, List.of(new User(7, "Anna")),
                List.of(new Project(3, "Backend", 7)), List.of(new Task(10, "Java", 3)));

        TakCodec.saveSnapshot(file, List.of(new User(8, "Mark")), List.of(), List.of());
        TaskManagerState imported = SnapshotImporter.importFrom(file);

        assertEquals(List.of("TASK_MANAGER;1", "USER;8;Mark"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
        assertEquals(1, imported.getUserRepository().findAll().size());
        assertEquals(0, imported.getProjectRepository().findAll().size());
        assertEquals(0, imported.getTaskRepository().findAll().size());
    }

    @Test
    void invalidPayloadIsRejectedBeforeOpeningExistingFile() throws IOException {
        Path file = directory.resolve("snapshot.txt");
        Files.writeString(file, "keep", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class,
                () -> TakCodec.saveSnapshot(file, List.of(new User(7, "Anna;extra")),
                        List.of(), List.of()));

        assertEquals("keep", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void importRejectsTaskWithMissingProject() throws IOException {
        Path file = directory.resolve("invalid.txt");
        Files.writeString(file, "TASK_MANAGER;1\nTASK;10;Java;99;TODO;;1;CREATED\n",
                StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> SnapshotImporter.importFrom(file));
    }

    @Test
    void importRejectsDuplicateIds() throws IOException {
        Path file = directory.resolve("duplicate.txt");
        Files.writeString(file, "TASK_MANAGER;1\nUSER;7;Anna\nUSER;7;Mark\n",
                StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> SnapshotImporter.importFrom(file));
    }

    @Test
    void snapshotReaderRejectsUnknownRecord() throws IOException {
        Path file = directory.resolve("unknown.txt");
        Files.writeString(file, "TASK_MANAGER;1\nUNKNOWN;7;Anna\n", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> TakCodec.loadSnapshot(file));
    }
}
