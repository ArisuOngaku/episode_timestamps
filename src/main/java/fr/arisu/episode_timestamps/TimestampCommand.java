package fr.arisu.episode_timestamps;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
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
                                .then(argument("interval", integer())
                                        .executes(context -> setInterval(context, getInteger(context, "interval")))
                                )
                        )
                )
                .then(literal("start")
                        .executes(context -> start(context, 0))
                        .then(argument("startingOffset", integer())
                                .executes(context -> start(context, getInteger(context, "startingOffset")))
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

    private int start(CommandContext<ServerCommandSource> context, int startingOffset) {
        if (this.mod.start(startingOffset, context.getSource().getMinecraftServer())) {
            context.getSource().sendFeedback(formatMessage("Episode timestamps started with offset " + startingOffset), true);
            return 1;
        }
        return 0;
    }
}
