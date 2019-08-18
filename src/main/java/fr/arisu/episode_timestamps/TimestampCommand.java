package fr.arisu.episode_timestamps;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static fr.arisu.episode_timestamps.EpisodeTimestamps.formatMessage;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TimestampCommand {
    private final EpisodeTimestamps mod;

    public TimestampCommand(EpisodeTimestamps mod) {
        this.mod = mod;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("timestamp")
                .then(literal("set")
                        .then(literal("interval")
                                .then(argument("interval", integer(1))
                                        .executes(context -> setInterval(context, getInteger(context, "interval")))
                                )
                        )
                )
                .then(literal("start")
                        .executes(context -> start(context, 0, -1))
                        .then(argument("timestampCount", integer())
                                .then(argument("timeOffset", longArg(0))
                                        .executes(context -> start(context, getInteger(context, "timestampCount"), getLong(context, "timeOffset")))
                                )
                        )
                )
        );
    }

    private int setInterval(CommandContext<ServerCommandSource> context, int interval) {
        if (this.mod.setInterval(interval)) {
            context.getSource().sendFeedback(formatMessage("Interval set to " + interval + " second(s)"), true);
            return 1;
        }
        return 0;
    }

    private int start(CommandContext<ServerCommandSource> context, int timestampCount, long timeOffset) {
        if (this.mod.start(timestampCount, timeOffset)) {
            context.getSource().sendFeedback(formatMessage("Episode timestamps started with count=" + timestampCount + " and timeOffset=" + timeOffset), true);
            return 1;
        }
        return 0;
    }
}
