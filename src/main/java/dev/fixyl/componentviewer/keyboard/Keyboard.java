/*
 * MIT License
 *
 * Copyright (c) 2025 fixyldev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
