import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> history =  new ArrayList<>();

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
        return history;
    }
}
