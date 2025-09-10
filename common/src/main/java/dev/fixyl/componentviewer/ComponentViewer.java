package dev.fixyl.componentviewer;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;

import dev.fixyl.componentviewer.config.Configs;

/**
 * This mod's singleton.
 * <p>
 * Holds the logger and configuration.
 * <p>
 * Must be extended to implement platform-specific
 * initialization logic, like registering event listeners.
 */
public abstract class ComponentViewer implements DisablableMod {

    private static ComponentViewer instance;

    /**
     * This mod's logger.
     */
    public final Logger logger;
    /**
     * This mod's configuration.
     */
    public final Configs configs;

    protected ComponentViewer(Path configDir) {
        ComponentViewer.setInstance(this);

        this.logger = LoggerFactory.getLogger(this.getClass());
        this.configs = new Configs(configDir, this.logger);
    }

    @Override
    public boolean isModDisabled() {
        return switch (this.configs.generalDisableMod.getValue()) {
            case NEVER -> false;
            case IN_SURVIVAL -> this.currentlyInSurvival();
            case ON_SERVER -> this.currentlyOnServer();
            case BOTH -> this.currentlyInSurvival() || this.currentlyOnServer();
        };
    }

    /**
     * Get this mod's instance.
     *
     * @return the mod's instance
     */
    public static ComponentViewer getInstance() {
        if (ComponentViewer.instance == null) {
            throw new IllegalStateException(String.format(
                "'%s' hasn't been instantiated yet!",
                ComponentViewer.class.getName()
            ));
        }

        return ComponentViewer.instance;
    }

    private static void setInstance(ComponentViewer instance) {
        if (ComponentViewer.instance != null) {
            throw new IllegalStateException(String.format(
                "Cannot instantiate '%s' twice!",
                ComponentViewer.class.getName()
            ));
        }

        ComponentViewer.instance = instance;
    }

    protected static Minecraft getMinecraftClient() {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (minecraftClient == null) {
            throw new IllegalStateException(
                "Minecraft hasn't been initialized yet, although it should!"
            );
        }

        return minecraftClient;
    }

    private boolean currentlyInSurvival() {
        LocalPlayer player = ComponentViewer.getMinecraftClient().player;
        return player != null && player.gameMode().isSurvival();
    }

    private boolean currentlyOnServer() {
        Minecraft minecraftClient = ComponentViewer.getMinecraftClient();
        IntegratedServer integratedServer = minecraftClient.getSingleplayerServer();

        return (
            minecraftClient.getCurrentServer() != null
            || integratedServer != null && integratedServer.isPublished()
        );
    }
}
