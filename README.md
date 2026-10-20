# Task Manager

Java learning project synchronized with the local TaskManager implementation.
Requires **JDK 26** and Maven. JUnit Jupiter is used for automated tests.

## Build and run

```sh
mvn clean verify
java -cp target/task-manager-0.1.0-SNAPSHOT.jar com.bohdandenysiuk.taskmanager.TaskManagerApp
```

The ordinary JAR does not declare a Main-Class, so use the classpath command above,
not `java -jar`. The application currently has no external runtime dependencies.
GitHub Actions runs `clean verify` and uploads the built JAR.

## Current architecture

Two application paths currently coexist:

```text
TaskManagerApp -> TaskManager -> Task[]                 (legacy console)

ProjectService / TaskService -> repository interfaces   (service API)
                              -> InMemoryRepository<T>
                                 -> User / Project / Task repositories

SnapshotImporter -> TakCodec.loadSnapshot -> SnapshotValidator
                 -> new repositories -> TaskManagerState

List<TaskReportRow> -> ReportPractice -> report lines / text file
```

- Generic repositories share CRUD and sorted-copy behavior without sharing storage
  between repository instances. List copies retain references to the same entities.
- TaskService supports lifecycle operations, filtering, sorted titles, grouping by
  status, status counters and flattened task history.
- Task.restore reconstructs ids, status, assignee and history without replaying
  lifecycle commands. It copies the supplied history.
- BasicEntityCodec and **TakCodec** preserve the existing local class names.
  Snapshot files use UTF-8, a `TASK_MANAGER;1` header, and USER/PROJECT/TASK records.
  Semicolons and embedded line breaks are not escaped and are rejected by encoders.
- SnapshotValidator checks duplicate ids and entity references. SnapshotImporter
  reads and validates before filling new repositories; the caller receives a new
  TaskManagerState only after success. Existing live repositories are not replaced
  automatically by the importer.
- TaskReportRow stores immutable title/status values. ReportPractice submits the
  status-count and completed-title calculations to a two-thread executor, then
  assembles sections in an explicit order.

## Scope and limitations

The console still exposes create/list/delete/complete through the legacy manager.
It does **not** yet expose users/projects, repository-backed services, snapshot
import/export or reports. Synchronizing these classes is not a full console rewrite.

Snapshot containers copy lists, not mutable Task objects. A report row captures
values, but callers must themselves arrange consistent capture if tasks are being
mutated concurrently. In-memory repositories are not thread-safe.

File exporters prepare/compute content before opening the destination, protecting
existing content from preparation failures. Writes themselves are **not atomic**:
an I/O failure after opening/truncation may leave an incomplete file. The standalone
safe/atomic-export teaching example is not integrated into this local application.

The PR preserves local application behavior and adds regression tests for snapshot
round trips/validation, service queries, report results and pre-write failures.
New synchronization regression tests were supplied by the assistant; they are not
presented as independently written student tests or completion of the Mockito lesson.
