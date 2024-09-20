package ru.tasks;

import java.time.Duration;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, Duration.ZERO);
        this.type = TaskType.EPIC;
    }

    public void addNewSubtaskId(int subtaskId) {
        if (subtaskIds.contains(subtaskId)) return;

        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void deleteSubtask(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void deleteSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return String.join(",", Integer.toString(id), "EPIC", name, status.toString(), description, startTime == null ? "null" : startTime.toString(), Long.toString(duration.toMinutes()), "");
    }
}
