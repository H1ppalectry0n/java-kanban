package ru.tasks;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskUtil {
    public static Task fromString(final String value) throws IOException {
        String[] split = value.split(",");
        switch (TaskType.valueOf(split[1])) {
            case EPIC -> {
                Epic epic = new Epic(split[2], split[4]);
                epic.setStatus(Status.valueOf(split[3]));
                epic.setId(Integer.parseInt(split[0]));

                final LocalDateTime startTime = split[5].equals("null") ? null : LocalDateTime.parse(split[5]);

                epic.setStartTime(startTime);
                epic.setDuration(Duration.ofMinutes(Long.parseLong(split[6])));
                return epic;
            }
            case SUBTASK -> {
                final String name = split[2];
                final String description = split[4];
                final Status status = Status.valueOf(split[3]);
                final LocalDateTime startTime = split[5].equals("null") ? null : LocalDateTime.parse(split[5]);
                final Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));
                final int epicId = Integer.parseInt(split[7]);

                Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);
                subtask.setId(Integer.parseInt(split[0]));

                return subtask;
            }
            case TASK -> {
                final String name = split[2];
                final String description = split[4];
                final Status status = Status.valueOf(split[3]);
                final LocalDateTime startTime = split[5].equals("null") ? null : LocalDateTime.parse(split[5]);
                final Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));

                Task task = new Task(name, description, status, startTime, duration);
                task.setId(Integer.parseInt(split[0]));

                return task;
            }
            default -> {
                throw new IOException("Неверный формат записи.");
            }
        }
    }
}
