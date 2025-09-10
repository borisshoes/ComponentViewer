package dev.fixyl.componentviewer.control.keyboard;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.keymapping.AdvancedKeyMapping;
import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.event.KeyComboEvents.CopyActionEvent;
import dev.fixyl.componentviewer.event.KeyComboEvents.CycleComponentEvent;

/**
 * The platform-specific keyboard logic for NeoForge.
 * <p>
 * This class implements event dispatching and the registration
 * of key mappings for the NeoForge platform.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class NeoForgeKeyboard extends Keyboard {

    public NeoForgeKeyboard(Minecraft minecraftClient, DisablableMod disablableMod, Configs configs) {
        super(
            minecraftClient,
            disablableMod,
            configs,
            configs.controlsAlternativeCopyModifierKey,
            configs.controlsAllowCyclingOptionsWhileInScreen
        );
    }

    @Override
    protected void invokeCycleComponentEvent(CycleType cycleType) {
        NeoForge.EVENT_BUS.post(new CycleComponentEvent(cycleType));
    }

    @Override
    protected void invokeCopyActionEvent() {
        NeoForge.EVENT_BUS.post(new CopyActionEvent());
    }

    @SubscribeEvent
    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        Configs configs = ComponentViewer.getInstance().configs;

        for (AdvancedKeyMapping keyMapping : configs.getKeyMappings()) {
            keyMapping.setKeyConflictContext(switch (keyMapping.getConfictContext()) {
                case UNIVERSAL -> KeyConflictContext.UNIVERSAL;
                case IN_SCREEN -> KeyConflictContext.GUI;
                case IN_GAME -> KeyConflictContext.IN_GAME;
            });

            event.register(keyMapping);
        }
    }
}
