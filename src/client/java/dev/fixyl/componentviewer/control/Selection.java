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

package dev.fixyl.componentviewer.control;

import net.minecraft.client.input.Scroller;

public class Selection {

    private int amount;
    private int selectedIndex;

    public Selection(int amount) {
        this.amount = amount;
        this.selectedIndex = 0;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Selection amount must be >= 0");
        }

        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    /**
     * Get the currently selected index.
     * Will return {@code -1} when the selection amount is {@code 0}.
     *
     * @return the selected index
     */
    public int getSelectedIndex() {
        return clampWithFallback(this.selectedIndex, 0, this.amount - 1, -1);
    }

    /**
     * Updates the selected index based on a mouse scroll distance.
     * Only the scroll direction (sign) is relevant though.
     *
     * @param scrollDistance the distance scrolled
     */
    public void updateByScrolling(double scrollDistance) {
        if (this.amount > 0) {
            this.selectedIndex = Scroller.scrollCycling(scrollDistance, this.selectedIndex, this.amount);
        }
    }

    public void updateByCycling(CycleType cycleType) {
        int newIndex = switch (cycleType) {
            case NEXT -> this.selectedIndex + 1;
            case PREVIOUS -> this.selectedIndex - 1;
            case FIRST -> 0;
            case LAST -> this.amount - 1;
        };

        if (newIndex >= this.amount) {
            this.selectedIndex = 0;
        } else if (newIndex < 0) {
            this.selectedIndex = this.amount - 1;
        } else {
            this.selectedIndex = newIndex;
        }
    }

    public void updateByValue(int newIndex) {
        this.selectedIndex = clampWithFallback(newIndex, 0, this.amount - 1, 0);
    }

    private static int clampWithFallback(long value, int min, int max, int fallback) {
        try {
            return Math.clamp(value, min, max);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public enum CycleType {
        NEXT,
        PREVIOUS,
        FIRST,
        LAST
    }
}
