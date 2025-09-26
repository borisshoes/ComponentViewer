package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.common.NeoForge;

import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.control.Tooltip;

public class NeoForgeEventDispatcher implements EventDispatcher {

    @Override
    public void invokeStartRenderEvent() {
        NeoForge.EVENT_BUS.post(new MixinEvents.StartRenderEvent());
    }

    @Override
    public void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip) {
        NeoForge.EVENT_BUS.post(new MixinEvents.TooltipEvent(itemStack, tooltip));
    }

    @Override
    public void invokeKeyPressEvent(KeyEvent keyEvent) {
        NeoForge.EVENT_BUS.post(new MixinEvents.KeyPressEvent(keyEvent));
    }

    @Override
    public InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset) {
        return NeoForge.EVENT_BUS.post(new MixinEvents.MouseScrollEvent(xOffset, yOffset)).getResult();
    }

    @Override
    public void invokeClearToastManagerEvent() {
        NeoForge.EVENT_BUS.post(new MixinEvents.ClearToastManagerEvent());
    }

    @Override
    public void invokeCycleComponentEvent(CycleType cycleType) {
        NeoForge.EVENT_BUS.post(new KeyboardEvents.CycleComponentEvent(cycleType));
    }

    @Override
    public void invokeCopyActionEvent() {
        NeoForge.EVENT_BUS.post(new KeyboardEvents.CopyActionEvent());
    }
}
