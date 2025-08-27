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

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.event.KeyComboEvents;
import dev.fixyl.componentviewer.event.MixinEvents.KeyboardEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class Keyboard {

    private Keyboard() {}

    @SubscribeEvent
    public static void onKeyPress(KeyboardEvent keyboardEvent) {
        // TODO: Make this primitive once primitive pattern matching is a thing
        Event event = switch (Integer.valueOf(keyboardEvent.key.getValue())) {
            case GLFW_KEY_DOWN, GLFW_KEY_RIGHT -> new KeyComboEvents.CycleComponentEvent(NEXT);
            case GLFW_KEY_UP, GLFW_KEY_LEFT -> new KeyComboEvents.CycleComponentEvent(PREVIOUS);
            case GLFW_KEY_HOME -> new KeyComboEvents.CycleComponentEvent(FIRST);
            case GLFW_KEY_END -> new KeyComboEvents.CycleComponentEvent(LAST);
            case Integer keyCode when (
                keyCode == GLFW_KEY_C
                && Screen.hasControlDown()
            ) -> new KeyComboEvents.CopyActionEvent();
            default -> null;
        };

        if (event != null) {
            NeoForge.EVENT_BUS.post(event);
        }
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        KeyBindings keyBindings = ComponentViewer.getInstance().keyBindings;

        for (KeyMapping keyBinding : keyBindings.getBindings()) {
            event.register(keyBinding);
        }
    }
}
