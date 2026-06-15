package com.example.commandmerge;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A very small "run this later" queue, driven by the server tick loop.
 */
public final class TickScheduler {

	private record ScheduledTask(long runAtTick, Runnable task) {
	}

	private static final List<ScheduledTask> TASKS = new ArrayList<>();

	private TickScheduler() {
	}

	public static synchronized void schedule(long runAtTick, Runnable task) {
		TASKS.add(new ScheduledTask(runAtTick, task));
	}

	public static void onEndTick(MinecraftServer server) {
		List<Runnable> due;

		synchronized (TickScheduler.class) {
			if (TASKS.isEmpty()) {
				return;
			}

			long now = server.getTickCount();
			due = new ArrayList<>();

			Iterator<ScheduledTask> it = TASKS.iterator();
			while (it.hasNext()) {
				ScheduledTask t = it.next();
				if (t.runAtTick() <= now) {
					due.add(t.task());
					it.remove();
				}
			}
		}

		for (Runnable r : due) {
			r.run();
		}
	}
}
