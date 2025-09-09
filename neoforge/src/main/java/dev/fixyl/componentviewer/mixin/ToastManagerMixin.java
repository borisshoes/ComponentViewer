package dev.fixyl.componentviewer.mixin;

import net.minecraft.client.gui.components.toasts.ToastManager;

import net.neoforged.neoforge.common.NeoForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.event.MixinEvents.ClearToastManagerEvent;

@Mixin(value = ToastManager.class)
public final class ToastManagerMixin {

    private ToastManagerMixin() {}

    @Inject(method = "clear()V", at = @At(value = "HEAD"))
    public void clear(CallbackInfo callback) {
        NeoForge.EVENT_BUS.post(new ClearToastManagerEvent());
    }
}
