package com.example.commandmerge;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class TickScheduler {

    private record ScheduledTask(long runAtTick, String key, Runnable task) {}

    private static final List<ScheduledTask> TASKS = new ArrayList<>();
    private static final Set<String> PENDING_KEYS = new HashSet<>();

    private TickScheduler() {}

    public static synchronized void schedule(long runAtTick, String key, Runnable task) {
        if (key == null || task == null) {
            throw new IllegalArgumentException("Key and task cannot be null");
        }
        if (PENDING_KEYS.contains(key)) {
            return; // Prevent duplicate scheduling
        }
        PENDING_KEYS.add(key);
        TASKS.add(new ScheduledTask(runAtTick, key, task));
    }

    public static void onEndTick(MinecraftServer server) {
        List<Runnable> due = new ArrayList<>();
        
        synchronized (TickScheduler.class) {
            if (TASKS.isEmpty()) return;
            
            long now = server.getTickCount();
            Iterator<ScheduledTask> it = TASKS.iterator();
            
            while (it.hasNext()) {
                ScheduledTask t = it.next();
                if (t.runAtTick() <= now) {
                    due.add(t.task());
                    PENDING_KEYS.remove(t.key());
                    it.remove();
                }
            }
        }
        
        // Execute outside of synchronized block to prevent blocking
        for (Runnable r : due) {
            try {
                r.run();
            } catch (Exception e) {
                System.err.println("Error executing scheduled task: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
