package com.bohdandenysiuk.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** Assistant-supplied synchronization regressions for the existing query API. */
class TaskServiceQueryTest {
    private final TaskRepository tasks = new InMemoryTaskRepository();
    private final TaskService service = new TaskService(tasks,
            new InMemoryProjectRepository(), new InMemoryUserRepository());

    @Test
    void sortedTitlesFilterByStatusAndKeepDuplicates() {
        tasks.save(new Task(1, "SQL", 3));
        tasks.save(new Task(2, "Java", 3));
        tasks.save(new Task(3, "SQL", 3));
        Task done = new Task(4, "Git", 3);
        done.markDone();
        tasks.save(done);

        List<String> result = service.findTitlesByStatusSorted(TaskStatus.TODO);

        assertEquals(List.of("Java", "SQL", "SQL"), result);
        result.clear();
        assertEquals(4, tasks.findAll().size());
    }

    @Test
    void groupedTaskSharesEntityButDoesNotAutomaticallyChangeKeys() {
        Task task = new Task(10, "Java", 3);
        tasks.save(task);
        Map<TaskStatus, List<Task>> groups = service.groupTasksByStatus();

        task.start();

        assertSame(task, groups.get(TaskStatus.TODO).get(0));
        assertEquals(TaskStatus.IN_PROGRESS, groups.get(TaskStatus.TODO).get(0).getStatus());
        assertNull(groups.get(TaskStatus.IN_PROGRESS));
        Map<TaskStatus, List<Task>> regrouped = service.groupTasksByStatus();
        assertFalse(regrouped.containsKey(TaskStatus.TODO));
        assertSame(task, regrouped.get(TaskStatus.IN_PROGRESS).get(0));
    }

    @Test
    void flattenedHistoryIncludesEveryTaskNotOnlyCompletedTasks() {
        Task todo = new Task(1, "Java", 3);
        Task done = new Task(2, "SQL", 3);
        done.markDone();
        tasks.save(todo);
        tasks.save(done);

        List<String> history = service.getAllTaskHistory();

        assertEquals(List.of("CREATED", "CREATED", "COMPLETED"), history);
        history.clear();
        assertEquals(List.of("CREATED", "COMPLETED"), done.getHistory());
    }

    @Test
    void filteredListIsIndependentButItsTasksRemainShared() {
        Task task = new Task(10, "Java", 3);
        tasks.save(task);
        List<Task> result = service.findTasks(t -> t.getStatus() == TaskStatus.TODO);

        result.get(0).start();
        result.clear();

        assertEquals(1, tasks.findAll().size());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.findById(10).getStatus());
        assertEquals(List.of("CREATED", "STARTED"), tasks.findById(10).getHistory());
    }
}
