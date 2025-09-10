package dev.fixyl.componentviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;

import dev.fixyl.componentviewer.control.ControlFlow;
import dev.fixyl.componentviewer.control.keyboard.FabricKeyboard;
import dev.fixyl.componentviewer.control.keyboard.Keyboard;
import dev.fixyl.componentviewer.event.KeyComboEvents;
import dev.fixyl.componentviewer.event.MixinEvents;

/**
 * This mod's entry point and singleton for the Fabric platform.
 * <p>
 * Handles initialization logic specific to Fabric.
 *
 * @see ComponentViewer
 */
public final class FabricComponentViewer extends ComponentViewer implements ClientModInitializer {

    public FabricComponentViewer() {
        super(FabricLoader.getInstance().getConfigDir());
    }

    @Override
    public void onInitializeClient() {
        Minecraft minecraftClient = ComponentViewer.getMinecraftClient();

        this.configs.loadFromDisk();

        ControlFlow controlFlow = new ControlFlow(minecraftClient, this, this.configs);
        Keyboard keyboard = new FabricKeyboard(minecraftClient, this, this.configs);

        ClientTickEvents.START_CLIENT_TICK.register(client -> controlFlow.onStartClientTick());
        MixinEvents.TOOLTIP_EVENT.register(controlFlow::onTooltip);
        MixinEvents.MOUSE_EVENT.register((xOffset, yOffset) -> controlFlow.onMouseScroll(yOffset));
        KeyComboEvents.CYCLE_COMPONENT_EVENT.register(controlFlow::onCycleComponent);
        KeyComboEvents.COPY_ACTION_EVENT.register(controlFlow::onCopyAction);

        ClientTickEvents.END_CLIENT_TICK.register(client -> keyboard.onEndClientTick());
        MixinEvents.KEYBOARD_EVENT.register((key, modifiers) -> keyboard.onKeyPress(key));
        MixinEvents.CLEAR_TOAST_MANAGER_EVENT.register(keyboard::clearAllOptionCycleToasts);
    }
}
