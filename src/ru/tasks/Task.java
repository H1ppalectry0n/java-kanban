package ru.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected TaskType type;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.type = TaskType.TASK;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return this.type;
    }

    public Duration getDuration() {
        return duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime == null) {
            return Optional.empty();
        }

        return Optional.of(startTime.plus(duration));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.join(",", Integer.toString(id), "TASK", name, status.toString(), description, startTime == null ? "null" : startTime.toString(), Long.toString(duration.toMinutes()), "");
    }
}
