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

package dev.fixyl.componentviewer.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

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
            listener.onKeyboard(key, modifiers);
        }
    });

    public static final Event<MouseScrollCallback> MOUSE_EVENT = EventFactory.createArrayBacked(MouseScrollCallback.class, listeners -> (horizontal, vertical) -> {
        for (MouseScrollCallback listener : listeners) {
            ActionResult result = listener.onMouseScroll(horizontal, vertical);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    @FunctionalInterface
    public static interface TooltipCallback {
        public void onTooltip(ItemStack itemStack, Tooltip tooltip);
    }

    @FunctionalInterface
    public static interface KeyboardCallback {
        public void onKeyboard(Key key, int modifiers);
    }

    @FunctionalInterface
    public static interface MouseScrollCallback {
        public ActionResult onMouseScroll(double horizontal, double vertical);
    }
}
