import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history =  new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }

        // Копирование задачи в массив
        history.add(new Task(task));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
