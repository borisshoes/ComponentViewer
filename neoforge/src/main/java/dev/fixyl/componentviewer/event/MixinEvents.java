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

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.Event;

import dev.fixyl.componentviewer.control.Tooltip;

public final class MixinEvents {

    private MixinEvents() {}

    public static class TooltipEvent extends Event {

        public final ItemStack itemStack;
        public final Tooltip tooltip;

        public TooltipEvent(ItemStack itemStack, Tooltip tooltip) {
            this.itemStack = itemStack;
            this.tooltip = tooltip;
        }
    }

    public static class KeyboardEvent extends Event {

        public final Key key;
        public final int modifiers;

        public KeyboardEvent(Key key, int modifiers) {
            this.key = key;
            this.modifiers = modifiers;
        }
    }

    public static class MouseScrollEvent extends InteractionResultEvent {

        public final double xOffset;
        public final double yOffset;

        public MouseScrollEvent(double xOffset, double yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
