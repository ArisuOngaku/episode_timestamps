package fr.arisu.episode_timestamps;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpisodeTimestamps implements ModInitializer {
    public static final String MODID = "episode_timestamps";

    private static final ExecutorService TIMESTAMP_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "Episode-Timestamps"));

    private static final Style CHAT_PREFIX_STYLE = new Style().setBold(true).setColor(Formatting.DARK_PURPLE);
    private static final Style CHAT_STYLE = new Style().setBold(false).setColor(Formatting.AQUA);

    public static Text formatMessage(String message) {
        return new LiteralText("[UHC] ").setStyle(CHAT_PREFIX_STYLE)
                .append(new LiteralText(message).setStyle(CHAT_STYLE));
    }

    private int interval = 20 * 60;
    private boolean running = false;
    private int startingOffset = 0;

    private long startTime = -1;
    private long timestampCount = 0;
    private MinecraftServer server;

    /**
     * Entry point
     */
    @Override
    public void onInitialize() {
        TIMESTAMP_EXECUTOR.execute(this::runTimestamp);
        ServerStopCallback.EVENT.register(server -> {
            TIMESTAMP_EXECUTOR.shutdownNow();
        });

        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            new TimestampCommand(this).register(dispatcher);
        });
    }

    public boolean setInterval(int interval) {
        if (interval > 0) {
            this.interval = interval;
            return true;
        }
        return false;
    }

    public boolean start(int startingOffset, MinecraftServer server) {
        this.server = server;
        if (startingOffset >= 0) {
            this.startingOffset = startingOffset;
            this.running = true;
            return true;
        }
        return false;
    }

    private void broadcast(String message) {
        server.getPlayerManager().broadcastChatMessage(formatMessage(message), false);
    }

    private void runTimestamp() {
        this.log("Starting episode timestamp runner");
        do {
            if (running) {
                if (startTime <= 0) {
                    startTime = System.currentTimeMillis();
                    this.broadcast("Start");
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                break;
            }
        } while (!Thread.interrupted());
        this.log("Episode timestamp runner stopped");
    }

    private void log(String message) {
        System.out.println("[Episode Timestamps] " + message);
    }
}
