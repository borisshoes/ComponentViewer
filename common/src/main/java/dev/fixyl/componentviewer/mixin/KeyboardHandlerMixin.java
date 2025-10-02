package dev.fixyl.componentviewer.mixin;

import static org.lwjgl.glfw.GLFW.*;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;

@Mixin(value = KeyboardHandler.class)
public final class KeyboardHandlerMixin {

    private KeyboardHandlerMixin() {}

    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At(value = "HEAD"))
    private void keyPress(long windowHandle, int action, KeyEvent keyEvent, CallbackInfo callback) {
        if (windowHandle != Minecraft.getInstance().getWindow().handle() || action == GLFW_RELEASE) {
            return;
        }

        ComponentViewer.dispatchEventSafely(dispatcher ->
            dispatcher.invokeKeyPressEvent(keyEvent)
        );
    }
}
