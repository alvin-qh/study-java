package alvin.study.se.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * {@link Future} 接口表示一个会在"将来"执行的异步任务,
 * 通过一系列方法可以得知任务执行的情况和任务执行的结果, 包括:
 * <ul>
 * <li>
 * {@link Future#isDone()} 方法返回任务是否完成
 * </li>
 * <li>
 * {@link Future#isCancelled()} 方法返回任务是否被取消
 * </li>
 * <li>
 * {@link Future#get()} 方法返回任务的执行结果, 该方法只针对已完成的任务,
 * 如果任务未执行完毕或已被取消, 则会抛出异常
 * </li>
 * <li>
 * {@link Future#get(long, TimeUnit)} 方法返回任务的执行结果,
 * 对于未完成的任务, 该方法进行等待, 直到任务完成或超时
 * </li>
 * </ul>
 * {@link java.util.concurrent.FutureTask FutureTask} 类型是
 * {@link Future} 接口的一个实现, 其同时也实现了 {@link Runnable}
 * 接口
 * </p>
 *
 * <p>
 * {@code schedule} 方法返回 {@link java.util.concurrent.ScheduledFuture
 * ScheduledFuture} 类型对象, 用于查看任务执行情况, 获取任务执行结果, 包括:
 * <ul>
 * <li>
 * 通过 {@link java.util.concurrent.ScheduledFuture#isDone()
 * ScheduledFuture.isDone()} 方法查看任务是否执行完毕
 * </li>
 * <li>
 * 通过 {@link java.util.concurrent.ScheduledFuture#get()
 * ScheduledFuture.get()} 方法获取任务执行结果
 * </li>
 * <li>
 * 通过 {@link java.util.concurrent.ScheduledFuture#getDelay(TimeUnit)
 * ScheduledFuture.getDelay(TimeUnit)} 方法获取任务剩余延时时间
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * {@code scheduleAtFixedRate} 方法以第一次任务执行时间作为后续任务执行的基准,
 * 即经过 {@code delay} 参数延迟后的时间, 但由于每次任务执行后才会追加下一次任务,
 * 所以某次任务的阻塞仍有可能会影响下次任务 (例如阻塞时间超过了 {@code period})
 * 参数设定的间隔时间, 但下一次任务会尽可能的快速执行以弥补耽搁的时间, 所以从宏观上看,
 * {@code scheduleAtFixedRate} 方法仍可以认为是基于固定频率的
 * </p>
 *
 * <p>
 * 通过 {@link java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(
 * Runnable, long, long, TimeUnit)
 * ScheduledExecutorService.scheduleWithFixedDelay(Runnable, long,
 * long, TimeUnit)} 方法可以按一个固定的频率重复执行某个任务,
 * 后三个参数用于表示任务第一次执行的延迟时间和之后每次执行的间隔时间
 * </p>
 *
 * <p>
 * {@code scheduleWithFixedDelay} 方法是以上一次任务执行时间来计算下一次任务执行的时间的,
 * 即 {@code delay} 参数表示的是两次任务的间隔时间, 所以如果一次任务的执行时间超过了
 * {@code delay} 参数, 则后续的任务都会受到影响
 * </p>
 */
class FutureTest {

}
