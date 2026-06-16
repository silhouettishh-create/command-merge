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
        if (PENDING_KEYS.contains(key)) return;
        PENDING_KEYS.add(key);
        TASKS.add(new ScheduledTask(runAtTick, key, task));
    }

    public static void onEndTick(MinecraftServer server) {
        List<Runnable> due;
        synchronized (TickScheduler.class) {
            if (TASKS.isEmpty()) return;
            long now = server.getTickCount();
            due = new ArrayList<>();
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
        for (Runnable r : due) r.run();
    }
}
