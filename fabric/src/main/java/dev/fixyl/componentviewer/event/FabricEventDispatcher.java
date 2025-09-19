package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.control.Tooltip;

public class FabricEventDispatcher implements EventDispatcher {

    @Override
    public void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip) {
        MixinEvents.TOOLTIP_EVENT.invoker().onTooltip(itemStack, tooltip);
    }

    @Override
    public void invokeKeyPressEvent(KeyEvent keyEvent) {
        MixinEvents.KEY_PRESS_EVENT.invoker().onKeyPress(keyEvent);
    }

    @Override
    public InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset) {
        return MixinEvents.MOUSE_SCROLL_EVENT.invoker().onMouseScroll(xOffset, yOffset);
    }

    @Override
    public void invokeClearToastManagerEvent() {
        MixinEvents.CLEAR_TOAST_MANAGER_EVENT.invoker().onClearToastManager();
    }

    @Override
    public void invokeCycleComponentEvent(CycleType cycleType) {
        KeyboardEvents.CYCLE_COMPONENT_EVENT.invoker().onCycleComponent(cycleType);
    }

    @Override
    public void invokeCopyActionEvent() {
        KeyboardEvents.COPY_ACTION_EVENT.invoker().onCopyAction();
    }
}
