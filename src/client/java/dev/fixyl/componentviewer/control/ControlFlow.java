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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.Component;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipInjectMethod;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.config.enums.TooltipKeepSelection;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.JsonFormatter;
import dev.fixyl.componentviewer.formatting.ObjectFormatter;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;

public final class ControlFlow {

    private final Configs configs;
    private final Clipboard clipboard;
    private final MinecraftClient client;

    private final Formatter snbtFormatter;
    private final Formatter jsonFormatter;
    private final Formatter objectFormatter;

    private long clientTick;

    private @Nullable HoveredItemStack hoveredItemStack;
    private @Nullable ItemStack previousItemStack;

    private long lastTimeItemStackHovered;
    private boolean isTooltipShown;
    private long lastTimeTooltipShown;

    public ControlFlow(Configs configs) {
        this.configs = configs;
        this.clipboard = new Clipboard();
        this.client = MinecraftClient.getInstance();

        this.snbtFormatter = new SnbtFormatter();
        this.jsonFormatter = new JsonFormatter();
        this.objectFormatter = new ObjectFormatter();

        this.clientTick = 0L;

        this.lastTimeItemStackHovered = -1L;
        this.isTooltipShown = false;
        this.lastTimeTooltipShown = -1L;
    }

    public void onClientTick() {
        this.clientTick++;
    }

    public void onTooltip(ItemStack itemStack, Tooltip tooltip) {
        if (this.hoveredItemStack == null || itemStack != this.previousItemStack) {
            HoveredItemStack newHoveredItemStack = new HoveredItemStack(itemStack, this.configs);

            if (
                this.configs.tooltipKeepSelection.getValue() != TooltipKeepSelection.NEVER
                && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS
                && this.hoveredItemStack != null
            ) {
                this.keepSelection(newHoveredItemStack);
            }

            this.hoveredItemStack = newHoveredItemStack;
            this.previousItemStack = itemStack;
        }

        this.lastTimeItemStackHovered = this.clientTick;

        this.isTooltipShown = this.shouldDisplayToolip();
        if (this.isTooltipShown) {
            this.lastTimeTooltipShown = this.clientTick;
        } else {
            return;
        }

        if (this.configs.tooltipInjectMethod.getValue() == TooltipInjectMethod.REPLACE) {
            tooltip.clear();
        }

        if (!tooltip.isEmpty()) {
            tooltip.addSpacer();
        }

        if (this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS) {
            this.handleComponentPurpose(tooltip);
        } else {
            this.handleItemStackPurpose(tooltip);
        }
    }

    public void onCycleComponent(Selection.CycleType cycleType) {
        if (this.isTooltipShown() && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS) {
            this.hoveredItemStack.getComponentSelection().ifPresent(selection ->
                selection.updateByCycling(cycleType)
            );
        }
    }

    public ActionResult onMouseScroll(double distance) {
        if (
            this.isTooltipShown()
            && this.configs.controlsAllowScrolling.getBooleanValue()
            && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS
        ) {
            this.hoveredItemStack.getComponentSelection().ifPresent(selection ->
                selection.updateByScrolling(distance)
            );

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public void onCopyAction() {
        if (!this.isItemStackHovered()) {
            return;
        }

        switch (this.configs.clipboardCopy.getValue()) {
            case ITEM_STACK -> this.copyItemStack(this.hoveredItemStack.getItemStack());
            case GIVE_COMMAND -> this.copyGiveCommand(this.hoveredItemStack.getItemStack());
            case ClipboardCopy copyType when (
                copyType == ClipboardCopy.COMPONENT_VALUE
                && this.isTooltipShown()
                && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS
            ) -> this.hoveredItemStack.getSelectedComponent().ifPresent(this::copyComponentValue);
            default -> { /* Default not needed, copying disabled */ }
        }
    }

    public boolean isItemStackHovered() {
        return this.lastTimeItemStackHovered == this.clientTick;
    }

    public boolean isTooltipShown() {
        return this.isTooltipShown && this.lastTimeTooltipShown == this.clientTick;
    }

    private boolean shouldDisplayToolip() {
        TooltipDisplay tooltipDisplay = this.configs.tooltipDisplay.getValue();
        if (tooltipDisplay == TooltipDisplay.NEVER || tooltipDisplay == TooltipDisplay.HOLD && !Screen.hasControlDown()) {
            return false;
        }

        return this.client.options.advancedItemTooltips || !this.configs.tooltipAdvancedTooltips.getBooleanValue();
    }

    private void keepSelection(HoveredItemStack newHoveredItemStack) {
        switch (this.configs.tooltipKeepSelection.getValue()) {
            case INDEX -> this.keepSelectionByIndex(newHoveredItemStack);
            case TYPE -> this.keepSelectionByType(newHoveredItemStack);
            case NEVER -> { /* Don't keep the selection */ }
        }
    }

    private void keepSelectionByIndex(HoveredItemStack newHoveredItemStack) {
        this.hoveredItemStack.getComponentSelection().ifPresent(currentSelection ->
            newHoveredItemStack.getComponentSelection().ifPresent(newSelection ->
                newSelection.updateByValue(currentSelection.getSelectedIndex())
            )
        );
    }

    private void keepSelectionByType(HoveredItemStack newHoveredItemStack) {
        this.hoveredItemStack.getSelectedComponent().ifPresent(component -> {
            int indexOfComponent = newHoveredItemStack.getComponents().indexOf(component.type());

            if (indexOfComponent < 0) {
                this.keepSelectionByIndex(newHoveredItemStack);
                return;
            }

            newHoveredItemStack.getComponentSelection().ifPresent(newSelection ->
                newSelection.updateByValue(indexOfComponent)
            );
        });
    }

    private void handleComponentPurpose(Tooltip tooltip) {
        tooltip.addComponentSelection(this.hoveredItemStack);

        boolean tooltipComponentValues = this.configs.tooltipComponentValues.getBooleanValue();
        if (this.hoveredItemStack.getComponents().isEmpty() || !tooltipComponentValues) {
            return;
        }

        Component<?> selectedComponent = this.hoveredItemStack.getSelectedComponent().orElseThrow();

        tooltip.addSpacer().addComponentValue(
            selectedComponent,
            this.getTooltipFormatter(selectedComponent),
            this.getTooltipIndentation(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );
    }

    private void handleItemStackPurpose(Tooltip tooltip) {
        tooltip.addItemStack(
            this.hoveredItemStack.getItemStack(),
            this.getTooltipFormatter(),
            this.configs.tooltipIndentation.getIntValue(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );
    }

    private <T> void copyComponentValue(Component<T> component) {
        this.clipboard.copyComponentValue(
            component,
            this.getClipboardFormatter(component),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyItemStack(ItemStack itemStack) {
        this.clipboard.copyItemStack(
            itemStack,
            this.getClipboardFormatter(),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyGiveCommand(ItemStack itemStack) {
        this.clipboard.copyGiveCommand(
            itemStack,
            this.getGiveCommandSelector(),
            this.configs.clipboardPrependSlash.getBooleanValue(),
            this.configs.clipboardIncludeCount.getBooleanValue(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private Formatter getTooltipFormatter() {
        return this.getTooltipFormatter(null);
    }

    private <T> Formatter getTooltipFormatter(@Nullable Component<T> component) {
        return switch (this.configs.tooltipFormatting.getValue()) {
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> (component != null && component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }

    private Formatter getClipboardFormatter() {
        return this.getClipboardFormatter(null);
    }

    private <T> Formatter getClipboardFormatter(@Nullable Component<T> component) {
        return switch (this.configs.clipboardFormatting.getValue()) {
            case SYNC -> this.getTooltipFormatter(component);
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> (component != null && component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }

    private int getTooltipIndentation() {
        return this.configs.tooltipIndentation.getIntValue();
    }

    private int getClipboardIndentation() {
        int clipboardIndentation = this.configs.clipboardIndentation.getIntValue();

        return (clipboardIndentation == -1) ? this.getTooltipIndentation() : clipboardIndentation;
    }

    private String getGiveCommandSelector() {
        return switch (this.configs.clipboardSelector.getValue()) {
            case EVERYONE -> "@a";
            case NEAREST -> "@p";
            case SELF -> "@s";
            case PLAYER -> this.client.getGameProfile().getName();
        };
    }
}
