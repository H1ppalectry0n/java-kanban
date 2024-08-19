import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Task> history = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        // Исключение повторяюшихся элементов
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }

        // Копирование задачи в массив
        history.put(task.getId(), new Task(task));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<Task>(history.values());
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}
