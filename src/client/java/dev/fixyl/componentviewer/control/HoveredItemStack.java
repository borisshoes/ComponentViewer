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

import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

public class HoveredItemStack {

    private final ItemStack itemStack;
    private final Configs configs;

    private Components components;
    private Selection componentSelection;

    public HoveredItemStack(ItemStack itemStack, Configs configs) {
        this.itemStack = itemStack;
        this.configs = configs;
    }

    /**
     * Get the {@link ItemStack} instance associated with this {@link HoveredItemStack}.
     *
     * @return the item stack of this instance
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Get a {@link Components} instance which holds all currently relevant
     * components of the item stack based on the player's {@link TooltipComponents}
     * config.
     * <p>
     * It is not guaranteed that this is always the same object.
     *
     * @return all currently relevant components
     */
    public Components getComponents() {
        TooltipComponents componentsTypeFromConfig = this.configs.tooltipComponents.getValue();

        if (this.components == null || this.components.componentsType() != componentsTypeFromConfig) {
            this.components = Components.getComponentsBasedOnType(this.itemStack, componentsTypeFromConfig);
        }

        return this.components;
    }

    /**
     * Get the {@link Component} instance which is currently selected by the player.
     * <p>
     * This is equivalent to
     * {@code hoveredItemStack.getComponents().get(hoveredItemStack.getComponentSelection().getIndexOfSelected());}.
     * This means that if no components are currently relevant and therefore the {@link Components} instance is empty,
     * an {@link IndexOutOfBoundsException} is thrown.
     *
     * @implNote
     * SonarQube warning for returning a wildcard generic in non-private methods is suppressed.
     * The component type isn't known, even for the method itself, and doesn't matter at all.
     *
     * @return the currently selected component
     * @throws IndexOutOfBoundsException if no components are currently relevant to this hovered item stack
     */
    @SuppressWarnings("java:S1452")
    public Component<?> getSelectedComponent() {
        return this.getComponents().get(this.getComponentSelection().getSelectedIndex());
    }

    /**
     * Get a {@link Selection} instance representing the currently selected component.
     * To update the selection based on user input, use
     * {@link Selection#updateByCycling(Selection.CycleType) updateByCycling(CycleType)} and
     * {@link Selection#updateByScrolling(double) updateByScrolling(double)}.
     *
     * @return the component selection
     */
    public Selection getComponentSelection() {
        if (this.componentSelection == null) {
            this.componentSelection = new Selection(this.getComponents().size());
        } else {
            this.componentSelection.setAmount(this.getComponents().size());
        }

        return this.componentSelection;
    }
}
