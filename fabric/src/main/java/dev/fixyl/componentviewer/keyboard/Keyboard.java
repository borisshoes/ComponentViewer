package dev.fixyl.componentviewer.keyboard;

import static org.lwjgl.glfw.GLFW.*;

import static dev.fixyl.componentviewer.control.Selection.CycleType.*;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;

import dev.fixyl.componentviewer.event.KeyComboEvents;

public final class Keyboard {

    private Keyboard() {}

    public static void onKeyPress(Key key) {
        KeyComboEvents.CycleComponentCallback cycleInvoker = KeyComboEvents.CYCLE_COMPONENT_EVENT.invoker();
        KeyComboEvents.CopyActionCallback copyInvoker = KeyComboEvents.COPY_ACTION_EVENT.invoker();

        // TODO: Make this primitive once primitive pattern matching is a thing
        switch (Integer.valueOf(key.getValue())) {
            case GLFW_KEY_DOWN, GLFW_KEY_RIGHT -> cycleInvoker.onCycleComponent(NEXT);
            case GLFW_KEY_UP, GLFW_KEY_LEFT -> cycleInvoker.onCycleComponent(PREVIOUS);
            case GLFW_KEY_HOME -> cycleInvoker.onCycleComponent(FIRST);
            case GLFW_KEY_END -> cycleInvoker.onCycleComponent(LAST);
            case Integer keyCode when (
                keyCode == GLFW_KEY_C
                && Screen.hasControlDown()
            ) -> copyInvoker.onCopyAction();
            default -> { /* Default not needed, skip all other keys */ }
        }
    }

    public static void registerKeyBindings(KeyBindings keyBindings) {
        for (KeyMapping keyBinding : keyBindings.getBindings()) {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }
}
