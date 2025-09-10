package dev.fixyl.componentviewer.control.keyboard;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.keymapping.KeyMappings;
import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.event.KeyComboEvents;

/**
 * The platform-specific keyboard logic for Fabric.
 * <p>
 * This class implements event dispatching and the registration
 * of key mappings for the Fabric platform.
 */
public class FabricKeyboard extends Keyboard {

    public FabricKeyboard(Minecraft minecraftClient, DisablableMod disablableMod, Configs configs) {
        super(
            minecraftClient,
            disablableMod,
            configs,
            configs.controlsAlternativeCopyModifierKey,
            configs.controlsAllowCyclingOptionsWhileInScreen
        );

        FabricKeyboard.registerKeyMappings(configs);
    }

    @Override
    protected void invokeCycleComponentEvent(CycleType cycleType) {
        KeyComboEvents.CYCLE_COMPONENT_EVENT.invoker().onCycleComponent(cycleType);
    }

    @Override
    protected void invokeCopyActionEvent() {
        KeyComboEvents.COPY_ACTION_EVENT.invoker().onCopyAction();
    }

    private static void registerKeyMappings(KeyMappings keyMappings) {
        for (KeyMapping keyMapping : keyMappings.getKeyMappings()) {
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }
    }
}
