package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Tooltip;

/**
 * Defines an event dispatcher used to dispatch
 * events originating from mixins.
 */
public interface MixinEventDispatcher {

    void invokeStartRenderEvent();
    void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip);
    void invokeKeyPressEvent(KeyEvent keyEvent);
    InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset);
    void invokeClearToastManagerEvent();
}
