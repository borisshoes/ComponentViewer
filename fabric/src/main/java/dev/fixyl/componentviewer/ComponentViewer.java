package dev.fixyl.componentviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.control.ControlFlow;
import dev.fixyl.componentviewer.event.KeyComboEvents;
import dev.fixyl.componentviewer.event.MixinEvents;
import dev.fixyl.componentviewer.keyboard.KeyBindings;
import dev.fixyl.componentviewer.keyboard.Keyboard;

public final class ComponentViewer implements ClientModInitializer {

    private static ComponentViewer instance;

    public final Logger logger;
    public final Configs configs;
    public final KeyBindings keyBindings;

    public ComponentViewer() {
        ComponentViewer.setInstance(this);

        this.logger = LoggerFactory.getLogger(this.getClass());
        this.configs = new Configs(FabricLoader.getInstance().getConfigDir(), this.logger);
        this.keyBindings = new KeyBindings(this.configs);
    }

    @Override
    public void onInitializeClient() {
        ControlFlow controlFlow = new ControlFlow(Minecraft.getInstance(), this.configs);

        this.configs.loadFromDisk();

        ClientTickEvents.END_CLIENT_TICK.register(this.keyBindings::onClientTick);

        ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> controlFlow.onClientTick());
        MixinEvents.TOOLTIP_EVENT.register(controlFlow::onTooltip);
        MixinEvents.MOUSE_EVENT.register((xOffset, yOffset) -> controlFlow.onMouseScroll(yOffset));
        KeyComboEvents.CYCLE_COMPONENT_EVENT.register(controlFlow::onCycleComponent);
        KeyComboEvents.COPY_ACTION_EVENT.register(controlFlow::onCopyAction);

        MixinEvents.KEYBOARD_EVENT.register((key, modifiers) -> Keyboard.onKeyPress(key));
        Keyboard.registerKeyBindings(this.keyBindings);
    }

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
}
