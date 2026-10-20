package com.bohdandenysiuk.taskmanager;

public final class TaskReportRow {
    private final String title;
    private final TaskStatus status;

    public TaskReportRow(String title, TaskStatus status) {
        if (title == null || status == null) {
            throw new IllegalArgumentException("Title and status cannot be null");
        }
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
