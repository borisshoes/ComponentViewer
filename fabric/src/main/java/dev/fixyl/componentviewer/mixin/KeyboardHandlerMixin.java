package dev.fixyl.componentviewer.mixin;

import static org.lwjgl.glfw.GLFW.*;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.event.MixinEvents;

@Mixin(value = KeyboardHandler.class)
public final class KeyboardHandlerMixin {

    private KeyboardHandlerMixin() {}

    @Inject(method = "keyPress(JIIII)V", at = @At(value = "HEAD"))
    private void keyPress(long windowPointer, int key, int scancode, int action, int modifiers, CallbackInfo callback) {
        if (windowPointer != Minecraft.getInstance().getWindow().getWindow() || action == GLFW_RELEASE) {
            return;
        }

        MixinEvents.KEYBOARD_EVENT.invoker().onKeyPress(InputConstants.getKey(key, scancode), modifiers);
    }
}
