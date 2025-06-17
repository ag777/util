package github.ag777.util.lang.thread;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 异步任务管理器
 * @author ag777 <837915770@vip.qq.com>
 * @version 2025/6/4 上午9:08
 */
public class AsyncTaskManager<R, C extends CompletableFuture<R>> {
    private final Map<String, C> taskMap;

    public AsyncTaskManager() {
        this.taskMap = new ConcurrentHashMap<>();
    }

    public Map<String, C> getTaskMap() {
        return taskMap;
    }

    /**
     * 添加任务
     * @param taskId 任务编号
     * @param task 任务
     * @return 任务
     */
    public C addTask(String taskId, C task) {
        taskMap.put(taskId, task);
        // 任务完成时从map中移除
        task.whenComplete((r, e)-> taskMap.remove(taskId));
        return task;
    }

    /**
     * 获取任务
     * @param taskId 任务编号
     * @return 任务
     */
    public Optional<C> getTask(String taskId) {
        return Optional.ofNullable(taskMap.get(taskId));
    }

    /**
     * 完成任务
     * @param taskId 任务编号
     * @param result 任务结果
     */
    public void complete(String taskId, R result) {
        Optional<C> task = getTask(taskId);
        if (task.isPresent()) {
            task.get().complete(result);
        }
    }

    /**
     * 完成任务异常
     * @param taskId 任务编号
     * @param ex 异常
     */
    public void completeExceptionally(String taskId, Throwable ex) {
        Optional<C> task = getTask(taskId);
        if (task.isPresent()) {
            task.get().completeExceptionally(ex);
        }
    }

    /**
     * 取消任务
     * @param taskId 任务编号
     * @return 任务
     */
    public Optional<C> cancel(String taskId) {
        Optional<C> task = getTask(taskId);
        if (task.isPresent()) {
            task.get().cancel(true);
        }
        return task;
    }

    /**
     * 根据条件取消任务
     * @param predicate 条件
     */
    public void cancelIf(Predicate<C> predicate) {
        // ConcurrentHashMap的forEach方法本身是线程安全的，它在遍历时不会抛出ConcurrentModificationException
        taskMap.forEach((taskId, task) -> {
            if (predicate.test(task)) {
                task.cancel(true);
            }
        });
    }

    /**
     * 取消所有任务
     */
    public void cancelAll() {
        cancelIf(task->true);
    }

    /**
     * 遍历任务Map中的所有任务，并对每个任务应用提供的消费者函数
     * 
     * @param consumer 接受任务ID和任务对象的消费者函数
     */
    public void forEachTask(BiConsumer<String, C> consumer) {
        // ConcurrentHashMap的forEach方法是线程安全的
        taskMap.forEach(consumer);
    }
}
