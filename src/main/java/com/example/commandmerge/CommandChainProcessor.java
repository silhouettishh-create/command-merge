package com.example.commandmerge;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicLong;

public final class CommandChainProcessor {

    private static final Pattern MERGE_SPLIT = Pattern.compile("\\s+MERGE\\s+");
    private static final Pattern WAIT_PREFIX =
            Pattern.compile("^WAIT_TICK:(\\d+)\\s+(.*)$", Pattern.DOTALL);
    
    private static final AtomicLong TASK_COUNTER = new AtomicLong(0);

    private CommandChainProcessor() {}

    public static boolean needsProcessing(String command) {
        return command.contains(" MERGE ") || WAIT_PREFIX.matcher(command).matches();
    }

    public static void process(Commands commands, CommandSourceStack source, String fullCommand) {
        MinecraftServer server = source.getServer();
        if (server == null) return;
        
        String[] parts = MERGE_SPLIT.split(fullCommand.trim());
        long cumulativeDelay = 0;

        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) continue;

            Matcher waitMatch = WAIT_PREFIX.matcher(part);
            if (waitMatch.matches()) {
                try {
                    long waitTicks = Long.parseLong(waitMatch.group(1));
                    String cmd = waitMatch.group(2).trim();
                    cumulativeDelay += waitTicks;
                    runOrSchedule(commands, source, cmd, server, cumulativeDelay);
                } catch (NumberFormatException e) {
                    source.sendFailure(net.minecraft.network.chat.Component.literal(
                            "Invalid WAIT_TICK value: " + waitMatch.group(1)));
                }
            } else {
                runOrSchedule(commands, source, part, server, cumulativeDelay);
            }
        }
    }

    private static void runOrSchedule(Commands commands, CommandSourceStack source,
                                       String command, MinecraftServer server, long delayTicks) {
        if (delayTicks <= 0) {
            commands.performPrefixedCommand(source, command);
        } else {
            long targetTick = server.getTickCount() + delayTicks;
            String key = "cmd_" + TASK_COUNTER.incrementAndGet();
            
            TickScheduler.schedule(targetTick, key,
                    () -> commands.performPrefixedCommand(source, command));
        }
    }
}
