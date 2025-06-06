package github.ag777.util.lang.thread;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * 回调线程池CompletionService辅助类
 * 
 * @author ag777
 * @version  create on 2018年08月03日,last modify at 2025年05月31日
 */
public class CompletionServiceHelper<T, V> {
	private ExecutorService pool;
	private CompletionService<T> completionService;
	private final Map<Future<T>, V> taskInfoMap;
	private final List<Future<T>> taskList;

	public CompletionServiceHelper(int poolSize) {
		this(Executors.newFixedThreadPool(poolSize));
	}

	public CompletionServiceHelper(ExecutorService pool) {
		this.pool = pool;
		completionService = new ExecutorCompletionService<>(pool);
		taskInfoMap = new ConcurrentHashMap<>(10);
		taskList = Collections.synchronizedList(new LinkedList<>());
	}
	public ExecutorService getExecutorService() {
		return pool;
	}


	public CompletionService<T> getCompletionService() {
		return completionService;
	}

	/**
	 * 获取当前任务的数量。
	 * <p>该方法不接受任何参数，通过计算任务信息映射表中的条目数量来确定当前任务的总数。</p>
	 *
	 * @return 返回当前任务数量，类型为int。
	 */
	public int getTaskCount() {
		// 返回任务信息映射表的大小，即当前任务的数量
		return taskList.size();
	}

	/**
	 * 提交一个Callable任务到CompletionService中，并关联一个数据。
	 *
	 * @param task 要提交的Callable任务，任务执行后会返回一个结果。
	 * @param bindData 与该任务关联的数据，便于后续处理或查询。
	 * @return 返回CompletionServiceHelper实例，支持链式调用。
	 */
	public CompletionServiceHelper<T, V> submit(Callable<T> task, V bindData) {
		Future<T> myTask = completionService.submit(task); // 提交任务
		if (bindData != null) {
			taskInfoMap.put(myTask, bindData); // 将任务与关联数据绑定
		}
		taskList.add(myTask);
		whenTaskAdd(myTask, bindData);
		return this;
	}

	/**
	 * 提交一个Runnable任务到CompletionService中，并关联一个数据。
	 *
	 * @param task 要提交的Runnable任务，任务执行后不会返回结果。
	 * @param result 任务执行后要返回的默认结果，通常用于Runnable任务。
	 * @param bindData 与该任务关联的数据，便于后续处理或查询。
	 * @return 返回CompletionServiceHelper实例，支持链式调用。
	 */
	public CompletionServiceHelper<T, V> submit(Runnable task, T result, V bindData) {
		Future<T> myTask = completionService.submit(task, result); // 提交任务并指定结果
		if (bindData != null) {
			taskInfoMap.put(myTask, bindData); // 将任务与关联数据绑定
		}
		taskList.add(myTask);
		whenTaskAdd(myTask, bindData);
		return this;
	}

	/**
	 * 添加时任务时执行
	 * @param task 异步任务
	 */
	protected void whenTaskAdd(Future<T> task, V bindData) {

	}

	/**
	 * <p>阻塞线程，直至获取到结果
	 * @return 一个执行完成的任务
	 * @throws InterruptedException 等待中断
	 */
	public Task<T, V> take() throws InterruptedException {
		Future<T> task = completionService.take();
		taskList.remove(task);
		return new Task<>(task, taskInfoMap.remove(task));
	}

	/**
	 * <p>不会阻塞线程，如果当前没有任务结束，则返回null
	 * @return 一个执行完成的任务
	 */
	public Task<T, V> poll() {
		Future<T> task = completionService.poll();
		if (task == null) {
			return null;
		}
		taskList.remove(task);
		return new Task<>(task, taskInfoMap.remove(task));
	}

	/**
	 * <p>阻塞线程一段时间，如果当前没有任务结束，则返回null
	 * @param timeout 等待时间
	 * @param timeunit 等待的时间单位
	 * @return 一个执行完成的任务
	 * @throws InterruptedException 等待中断
	 */
	public Task<T, V> poll(long timeout, TimeUnit timeunit) throws InterruptedException {
		Future<T> task = completionService.poll(timeout, timeunit);
		if (task == null) {
			return null;
		}
		taskList.remove(task);
		return new Task<>(task, taskInfoMap.remove(task));
	}

	/**
	 * 取消剩余任务
	 * @param mayInterruptIfRunning 是否强制中断
	 */
	public void cancel(boolean mayInterruptIfRunning) {
		for (Future<T> task : taskInfoMap.keySet()) {
			task.cancel(mayInterruptIfRunning);
		}
	}

	/**
	 * 遍历任务信息映射中的所有任务，并对每个任务应用提供的双参数消费者。
	 *
	 * @param consumer 一个接受Future<T>任务和与之关联的V类型值的双参数消费者。该消费者将在每个任务上执行。
	 *                 第一个参数是任务的Future对象，第二个参数是该任务对应的值。
	 * @see BiConsumer 一个函数式接口，表示可以接受两个参数并执行操作的消费者。
	 */
	public void forEachTask(BiConsumer<Future<T>, V> consumer) {
		// 遍历任务映射表的所有任务，对每个任务应用提供的consumer
		for (Future<T> task : taskInfoMap.keySet()) {
			consumer.accept(task, taskInfoMap.get(task));
		}
	}


	/**
	 * 释放资源
	 */
	public void dispose() {
		if(pool == null) {
			return;
		}
		pool.shutdownNow();
		pool = null;
		completionService = null;
		taskInfoMap.clear();
		taskList.clear();
	}

	/**
	 * 等待任务执行结束并且关闭线程池
	 */
	public void waitForDispose() {
		if(pool == null) {
			return;
		}
		try {
			waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			dispose();
		}
	}

	/**
	 * 等待任务执行结束并且关闭线程池
	 */
	public void waitForDisposeWithException() throws InterruptedException {
		if(pool == null) {
			return;
		}
		try {
			waitFor();
		} finally {
			dispose();
		}
	}

	/**
	 * 等待所有已提交的任务执行完成并关闭线程池。
	 * <p>
	 * 此方法会先调用线程池的shutdown()方法停止接受新任务，
	 * 然后以指定的时间间隔轮询检查线程池是否已完全终止。
	 * 这是一个阻塞方法，会一直等待直到所有任务完成或线程被中断。
	 * </p>
	 *
	 * @param timeout 轮询检查的时间间隔
	 * @param unit 时间单位
	 * @throws InterruptedException 如果当前线程在等待时被中断
	 * @throws IllegalStateException 如果线程池已被释放或为null
	 * @throws IllegalArgumentException 如果时间单位为null
	 */
	private void waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		if (pool == null) {
			throw new IllegalStateException("线程池已被释放或未初始化");
		}
		if (unit == null) {
			throw new IllegalArgumentException("时间单位不能为null");
		}

		// 停止接受新任务
		pool.shutdown();

		// 等待所有任务完成
		while (!pool.awaitTermination(timeout, unit)) {
			// 轮询检查线程池是否已终止
		}
	}

	/**
	 * 等待所有已提交的任务执行完成并关闭线程池。
	 * <p>
	 * 此方法是{@link #waitFor(long, TimeUnit)}的便捷方法，
	 * 使用默认的100毫秒作为轮询检查的时间间隔。
	 * </p>
	 *
	 * @throws InterruptedException 如果当前线程在等待时被中断
	 * @throws IllegalStateException 如果线程池已被释放或为null
	 */
	private void waitFor() throws InterruptedException {
		waitFor(100, TimeUnit.MILLISECONDS);
	}

	/**
	 * 详见线程池的使用方法
	 */
	public void shutdown() {
		if(pool != null) {
			pool.shutdown();
		}
	}

	/**
	 * 详见线程池的使用方法
	 */
	public void shutdownNow() {
		if(pool != null) {
			pool.shutdownNow();
		}
	}

	/**
	 * 详见线程池的使用方法
	 * @return 是否执行过shutdown
	 */
	public boolean isShutdown() {
		if(pool != null) {
			return pool.isShutdown();
		}
		return true;
	}

	/**
	 * 详见线程池的使用方法
	 * @return 是否完全停止
	 */
	public boolean isTerminated() {
		if(pool != null) {
			return pool.isTerminated();
		}
		return true;
	}

	public static class Task<T, V> implements Future<T>{
		private Future<T> task;
		private V data;
		public Task(Future<T> task, V data) {
			this.task = task;
			this.data = data;
		}

		public V getData() {
			return data;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return task.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return task.isCancelled();
		}

		@Override
		public boolean isDone() {
			return task.isDone();
		}

		@Override
		public T get() throws InterruptedException, ExecutionException {
			return task.get();
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return task.get(timeout, unit);
		}
	}

}