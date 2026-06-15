package com.example.commandmerge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Entry point for the Command Merge & Wait mod.
 *
 * <p>This mod lets a single command block run a chain of commands without
 * needing chain command blocks, using two pieces of syntax inside the
 * command block's command field:</p>
 *
 * <pre>
 *   say Hello MERGE say HelloMerged2
 * </pre>
 * <p>Runs "say Hello" then immediately "say HelloMerged2" in the same tick.</p>
 *
 * <pre>
 *   say Hello MERGE WAIT_TICK:20 say HelloWaited
 * </pre>
 * <p>Runs "say Hello" immediately, then waits 20 game ticks (1 second) and
 * runs "say HelloWaited".</p>
 *
 * <p>You can chain as many " MERGE " separated commands as you like, and any
 * segment can start with "WAIT_TICK:&lt;number&gt; " to delay it (and every
 * segment after it) relative to the previous segment.</p>
 */
public class CommandMergeMod implements ModInitializer {

	public static final String MOD_ID = "commandmerge";

	@Override
	public void onInitialize() {
		// Drives the WAIT_TICK delayed-command queue.
		ServerTickEvents.END_SERVER_TICK.register(TickScheduler::onEndTick);
	}
}
