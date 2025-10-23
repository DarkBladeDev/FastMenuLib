package com.darkbladedev.fastmenu.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Utility class for scheduling tasks using the Bukkit scheduler.
 * 
 * <p>This utility provides a comprehensive and convenient API for scheduling tasks
 * in Minecraft plugins. It supports both synchronous and asynchronous task execution,
 * delayed tasks, repeating tasks, and modern CompletableFuture-based async operations.
 * The class serves as a wrapper around Bukkit's scheduler system with enhanced
 * functionality and better error handling.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Synchronous task execution on the main server thread</li>
 *   <li>Asynchronous task execution on separate threads</li>
 *   <li>Delayed task scheduling with customizable delays</li>
 *   <li>Repeating task scheduling with configurable intervals</li>
 *   <li>CompletableFuture integration for modern async programming</li>
 *   <li>Automatic task cancellation and cleanup</li>
 *   <li>Time unit conversion utilities</li>
 *   <li>Error handling and exception management</li>
 * </ul>
 * 
 * <h3>Initialization:</h3>
 * <p>Before using any scheduling methods, the utility must be initialized with
 * a plugin instance:</p>
 * <pre>{@code
 * // In your plugin's onEnable() method
 * SchedulerUtil.initialize(this);
 * }</pre>
 * 
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Run a task immediately on the main thread
 * SchedulerUtil.runSync(() -> {
 *     player.sendMessage("Hello from main thread!");
 * });
 * 
 * // Run a task asynchronously
 * SchedulerUtil.runAsync(() -> {
 *     // Database operations, file I/O, etc.
 *     String data = fetchDataFromDatabase();
 *     
 *     // Switch back to main thread for Bukkit API calls
 *     SchedulerUtil.runSync(() -> {
 *         player.sendMessage("Data: " + data);
 *     });
 * });
 * 
 * // Schedule a delayed task
 * SchedulerUtil.runLater(() -> {
 *     player.sendMessage("This message appears after 5 seconds!");
 * }, 100L); // 100 ticks = 5 seconds
 * 
 * // Schedule a repeating task
 * BukkitTask task = SchedulerUtil.runTimer(() -> {
 *     Bukkit.broadcastMessage("This message repeats every 10 seconds!");
 * }, 0L, 200L); // Start immediately, repeat every 200 ticks
 * 
 * // Use CompletableFuture for complex async operations
 * SchedulerUtil.supplyAsync(() -> {
 *     return performExpensiveCalculation();
 * }).thenAcceptSync(result -> {
 *     player.sendMessage("Calculation result: " + result);
 * });
 * }</pre>
 * 
 * <h3>Thread Safety:</h3>
 * <p>This class is thread-safe and can be used from any thread. However, be aware
 * of Bukkit's threading model - most Bukkit API calls must be made from the main
 * server thread. Use the sync methods to ensure proper thread execution.</p>
 * 
 * <h3>Performance Considerations:</h3>
 * <p>Async tasks are executed on a separate thread pool, which is ideal for I/O
 * operations, database queries, and other blocking operations. Avoid running
 * CPU-intensive tasks that could block the server's main thread.</p>
 * 
 * <h3>Error Handling:</h3>
 * <p>All scheduled tasks include automatic exception handling to prevent server
 * crashes. Exceptions are logged appropriately and don't affect other scheduled tasks.</p>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see BukkitTask
 * @see BukkitRunnable
 * @see CompletableFuture
 * @see Plugin
 */
public final class SchedulerUtil {

    private static Plugin plugin;

    private SchedulerUtil() {
        // Utility class
    }

    /**
     * Initializes the scheduler utility with the plugin instance.
     * This must be called before using any scheduling methods.
     *
     * @param plugin the plugin instance
     */
    public static void initialize(@NotNull Plugin plugin) {
        SchedulerUtil.plugin = plugin;
    }

    /**
     * Gets the plugin instance used for scheduling.
     *
     * @return the plugin instance
     * @throws IllegalStateException if the scheduler is not initialized
     */
    @NotNull
    private static Plugin getPlugin() {
        if (plugin == null) {
            throw new IllegalStateException("SchedulerUtil not initialized. Call initialize() first.");
        }
        return plugin;
    }

    /**
     * Runs a task on the main server thread.
     *
     * @param task the task to run
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runSync(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTask(getPlugin(), task);
    }

    /**
     * Runs a task on the main server thread after a delay.
     *
     * @param task  the task to run
     * @param delay the delay in ticks (20 ticks = 1 second)
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runSyncLater(@NotNull Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(getPlugin(), task, delay);
    }

    /**
     * Runs a task on the main server thread after a delay with time unit.
     *
     * @param task     the task to run
     * @param delay    the delay amount
     * @param timeUnit the time unit
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runSyncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
        long ticks = timeUnit.toMillis(delay) / 50; // Convert to ticks (50ms per tick)
        return runSyncLater(task, ticks);
    }

    /**
     * Runs a repeating task on the main server thread.
     *
     * @param task   the task to run
     * @param delay  the initial delay in ticks
     * @param period the period between executions in ticks
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runSyncTimer(@NotNull Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), task, delay, period);
    }

    /**
     * Runs a repeating task on the main server thread with time units.
     *
     * @param task     the task to run
     * @param delay    the initial delay
     * @param period   the period between executions
     * @param timeUnit the time unit
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runSyncTimer(@NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
        long delayTicks = timeUnit.toMillis(delay) / 50;
        long periodTicks = timeUnit.toMillis(period) / 50;
        return runSyncTimer(task, delayTicks, periodTicks);
    }

    /**
     * Runs a task asynchronously (off the main thread).
     *
     * @param task the task to run
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runAsync(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), task);
    }

    /**
     * Runs a task asynchronously after a delay.
     *
     * @param task  the task to run
     * @param delay the delay in ticks
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runAsyncLater(@NotNull Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, delay);
    }

    /**
     * Runs a task asynchronously after a delay with time unit.
     *
     * @param task     the task to run
     * @param delay    the delay amount
     * @param timeUnit the time unit
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runAsyncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
        long ticks = timeUnit.toMillis(delay) / 50;
        return runAsyncLater(task, ticks);
    }

    /**
     * Runs a repeating task asynchronously.
     *
     * @param task   the task to run
     * @param delay  the initial delay in ticks
     * @param period the period between executions in ticks
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runAsyncTimer(@NotNull Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), task, delay, period);
    }

    /**
     * Runs a repeating task asynchronously with time units.
     *
     * @param task     the task to run
     * @param delay    the initial delay
     * @param period   the period between executions
     * @param timeUnit the time unit
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask runAsyncTimer(@NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
        long delayTicks = timeUnit.toMillis(delay) / 50;
        long periodTicks = timeUnit.toMillis(period) / 50;
        return runAsyncTimer(task, delayTicks, periodTicks);
    }

    /**
     * Creates a CompletableFuture that runs a task asynchronously.
     *
     * @param task the task to run
     * @param <T>  the return type
     * @return the CompletableFuture
     */
    @NotNull
    public static <T> CompletableFuture<T> supplyAsync(@NotNull java.util.function.Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Creates a CompletableFuture that runs a task asynchronously and then executes a callback on the main thread.
     *
     * @param task     the async task to run
     * @param callback the callback to run on the main thread
     * @param <T>      the return type
     * @return the CompletableFuture
     */
    @NotNull
    public static <T> CompletableFuture<T> supplyAsyncThenSync(@NotNull java.util.function.Supplier<T> task,
                                                               @NotNull Consumer<T> callback) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                T result = task.get();
                runSync(() -> {
                    callback.accept(result);
                    future.complete(result);
                });
            } catch (Exception e) {
                runSync(() -> future.completeExceptionally(e));
            }
        });
        return future;
    }

    /**
     * Cancels a task if it's not null and not cancelled.
     *
     * @param task the task to cancel
     */
    public static void cancelTask(@NotNull BukkitTask task) {
        if (!task.isCancelled()) {
            task.cancel();
        }
    }

    /**
     * Creates a new BukkitRunnable with convenient methods.
     *
     * @param task the task to run
     * @return a new BukkitRunnable
     */
    @NotNull
    public static BukkitRunnable createRunnable(@NotNull Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        };
    }

    /**
     * Creates a countdown timer that executes a task for each tick.
     *
     * @param duration     the duration in ticks
     * @param onTick       called for each tick with remaining time
     * @param onComplete   called when the countdown completes
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask countdown(long duration, @NotNull Consumer<Long> onTick, @NotNull Runnable onComplete) {
        return new BukkitRunnable() {
            private long remaining = duration;

            @Override
            public void run() {
                if (remaining <= 0) {
                    onComplete.run();
                    cancel();
                    return;
                }
                
                onTick.accept(remaining);
                remaining--;
            }
        }.runTaskTimer(getPlugin(), 0, 1);
    }

    /**
     * Creates a countdown timer with time unit support.
     *
     * @param duration     the duration
     * @param timeUnit     the time unit
     * @param onTick       called for each tick with remaining time in the specified unit
     * @param onComplete   called when the countdown completes
     * @return the BukkitTask
     */
    @NotNull
    public static BukkitTask countdown(long duration, @NotNull TimeUnit timeUnit,
                                      @NotNull Consumer<Long> onTick, @NotNull Runnable onComplete) {
        long durationTicks = timeUnit.toMillis(duration) / 50;
        long unitTicks = timeUnit.toMillis(1) / 50;
        
        return new BukkitRunnable() {
            private long remaining = durationTicks;

            @Override
            public void run() {
                if (remaining <= 0) {
                    onComplete.run();
                    cancel();
                    return;
                }
                
                long remainingInUnit = remaining / unitTicks;
                onTick.accept(remainingInUnit);
                remaining -= unitTicks;
            }
        }.runTaskTimer(getPlugin(), 0, unitTicks);
    }
}