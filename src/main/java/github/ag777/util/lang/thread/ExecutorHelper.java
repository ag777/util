package github.ag777.util.lang.thread;

import github.ag777.util.lang.Console;

import java.util.List;
import java.util.concurrent.*;


/**
 * 线程池ExecutorHelper辅助类
 * <p>
 * 简单包装,起到例子类的作用
 * </p>
 * 
 * @author ag777
 * @version  create on 2017年10月10日,last modify at 2020年07月30日
 */
public class ExecutorHelper implements AutoCloseable {

	protected ExecutorService pool;

	/**
	 *
	 * @param size 线程池大小(决定同时执行的线程数量)
	 */
	public ExecutorHelper(int size) {
		this(Executors.newFixedThreadPool(size));
	}

	/**
	 *
	 * @param size 线程池大小(决定同时执行的线程数量)
	 * @param uncaughtExceptionHandler 异常捕获器,异步捕获线程池中抛出的异常
	 */
	public ExecutorHelper(int size, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this(
			Executors.newFixedThreadPool(size, r -> {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(
						(t1, e) -> {
							if(uncaughtExceptionHandler != null) {
								uncaughtExceptionHandler.uncaughtException(t, e);
							}
						});
				return t;
		}));
	}

	public ExecutorHelper(int corePoolSize,
						  int maximumPoolSize,
						  long keepAliveTime,
						  TimeUnit unit,
						  BlockingQueue<Runnable> workQueue,
						  ThreadFactory threadFactory,
						  RejectedExecutionHandler handler) {
		this(new ThreadPoolExecutor(
				corePoolSize,
				maximumPoolSize,
				keepAliveTime,
				unit,
				workQueue,
				threadFactory,
				handler
		));
	}

	public ExecutorHelper(ExecutorService pool) {
		this.pool = pool;
	}

	public ExecutorHelper add(Runnable command) {
		pool.execute(command);
		return this;
	}
	
	public <T>Future<T> add(Callable<T> task) {
		return pool.submit(task);
	}

	/**
	 * 等待子线程都结束
	 * <p>
	 * 	调用此方法后线程池不再接受新的任务,之后每100毫秒检查一次子线程是否都完成（阻塞当前线程）,如果任务均完成则可以继续执行后续代码
	 * </p>
	 * @throws InterruptedException InterruptedException
	 */
	public void waitFor() throws InterruptedException {
		waitFor(100, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 等待子线程都结束
	 * <p>
	 * 	调用此方法后线程池不再接受新的任务,之后每根据参数指定的时间间隔检查一次子线程是否都完成（阻塞当前线程）,如果任务均完成则可以继续执行后续代码
	 * </p>
	 * @param timeout timeout
	 * @param unit unit
	 * @throws InterruptedException InterruptedException
	 */
	public void waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		pool.shutdown();
		try {
			while(!pool.awaitTermination(timeout, unit)) {	//如果结束则关闭线程池
			}
		} catch (InterruptedException e) {
//			e.printStackTrace();
			Console.err("等待线程池关闭失败");
			throw e;
		}
	}
	
	/**
	 * 当调用shutdown()方法后返回为true
	 * @return 是否被关闭
	 */
	public boolean isShutdown() {
		if(pool == null) {
			throw new RuntimeException("线程池已被释放");
		}
		return pool.isShutdown();
	}
	
	/**
	 * 当调用shutdown()方法后，并且所有提交的任务完成后返回为true
	 * @return 是否被中断
	 */
	public boolean isTerminated() {
		if(pool == null) {
			throw new RuntimeException("线程池已被释放");
		}
		return pool.isTerminated();
	}
	
	/**
	 * 这个方法会平滑地关闭ExecutorService
	 * <p>
	 * 当我们调用这个方法时，ExecutorService 
	 * 1：停止接收任何新的任务,
	 * 2：等待已经提交的任务执行完成(已经提交的任务会分两类：一类是已经在执行的，另一类是还没有开始执行的)，
	 * 当所有已经提交的任务执行完毕后将会关闭ExecutorService。
	 * </p>
	 */
	public void shutdown() {
		if(pool == null) {
			throw new RuntimeException("线程池已被释放");
		}
		pool.shutdown();
	}
	
	/**
	 * 这个方法会强制关闭ExecutorService，
	 * 它将取消所有运行中的任务和在工作队列中等待的任务，
	 * 这个方法返回一个List列表，列表中返回的是等待在工作队列中的任务。
	 * @return 
	 */
	public List<Runnable> shutdownNow() {
		if(pool == null) {
			throw new RuntimeException("线程池已被释放");
		}
		return pool.shutdownNow();
	}
	
	/**
	 * 这个方法有两个参数，一个是timeout即超时时间，另一个是unit即时间单位。
	 * 这个方法会使线程等待timeout时长，当超过timeout时间后，会监测ExecutorService是否已经关闭，
	 * 若关闭则返回true，否则返回false。一般情况下会和shutdown方法组合使用。
	 * @param timeout timeout
	 * @param unit unit
	 * @return 
	 * @throws InterruptedException InterruptedException
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		if(pool == null) {
			throw new RuntimeException("线程池已被释放");
		}
		return pool.awaitTermination(timeout, unit);
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
			close();
		}
	}
	
	/**
	 * 等待任务执行结束并且关闭线程池
	 */
	public void waitForDisposeWithException() {
		if(pool == null) {
			return;
		}
		try {
			waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close();
		}
	}
	
	/**
	 * 释放资源
	 */
	@Override
	public void close() {
		if(pool == null) {
			return;
		}
		pool.shutdownNow();
		pool = null;
	}
	
}