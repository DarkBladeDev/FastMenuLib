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
 * Provides convenient methods for running tasks synchronously and asynchronously.
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