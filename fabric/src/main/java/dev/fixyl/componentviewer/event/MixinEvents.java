package dev.fixyl.componentviewer.event;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;

import dev.fixyl.componentviewer.control.Tooltip;

public final class MixinEvents {

    private MixinEvents() {}

    public static final Event<TooltipCallback> TOOLTIP_EVENT = EventFactory.createArrayBacked(TooltipCallback.class, listeners -> (itemStack, tooltip) -> {
        for (TooltipCallback listener : listeners) {
            listener.onTooltip(itemStack, tooltip);
        }
    });

    public static final Event<KeyboardCallback> KEYBOARD_EVENT = EventFactory.createArrayBacked(KeyboardCallback.class, listeners -> (key, modifiers) -> {
        for (KeyboardCallback listener : listeners) {
            listener.onKeyPress(key, modifiers);
        }
    });

    public static final Event<MouseScrollCallback> MOUSE_EVENT = EventFactory.createArrayBacked(MouseScrollCallback.class, listeners -> (xOffset, yOffset) -> {
        for (MouseScrollCallback listener : listeners) {
            InteractionResult result = listener.onMouseScroll(xOffset, yOffset);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return InteractionResult.PASS;
    });

    public static final Event<ClearToastManagerCallback> CLEAR_TOAST_MANAGER_EVENT = EventFactory.createArrayBacked(ClearToastManagerCallback.class, listeners -> () -> {
        for (ClearToastManagerCallback listener : listeners) {
            listener.onClearToastManager();
        }
    });

    @FunctionalInterface
    public static interface TooltipCallback {
        void onTooltip(ItemStack itemStack, Tooltip tooltip);
    }

    @FunctionalInterface
    public static interface KeyboardCallback {
        void onKeyPress(Key key, int modifiers);
    }

    @FunctionalInterface
    public static interface MouseScrollCallback {
        InteractionResult onMouseScroll(double xOffset, double yOffset);
    }

    @FunctionalInterface
    public static interface ClearToastManagerCallback {
        void onClearToastManager();
    }
}
