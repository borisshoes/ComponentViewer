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

import java.util.List;
import java.util.Map;

import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.FormattingException;

/**
 * A {@link Tooltip} is a wrapper around a {@link List} holding {@link Text} instances.
 * The main purpose is to provide easy methods for altering a tooltip's content
 * without having raw access to the entire {@link List} instance.
 * <p>
 * So a {@link Toolip} doesn't represent a "real" tooltip. All operations, no matter
 * how complex, operate on the {@link List} instance directly.
 * Therefore, any {@link List} implementation, not supporting operations like
 * {@code add}, {@code addAll}, {@code clear} and so on, will throw an
 * {@link UnsupportedOperationException} or similar, depending on the actual implementation.
 */
public class Tooltip {

    private static final Style HEADER_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

    private static final Style COMPONENT_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
    private static final Style SELECTED_COMPONENT_STYLE = Style.EMPTY.withColor(Formatting.DARK_GREEN);
    private static final Style REMOVED_COMPONENT_STYLE = Style.EMPTY.withStrikethrough(true);
    private static final Style NOT_REGISTERED_COMPONENT_STYLE = Style.EMPTY.withItalic(true);

    private static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);

    private static final String CONTENT_INDENTATION = " ";

    private static final String NOT_REGISTERED_TRANSLATION_KEY = "componentviewer.tooltip.not_registered";

    private static final Map<TooltipComponents, String> COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes"
    );

    private static final Map<TooltipComponents, String> EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all.empty",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default.empty",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes.empty"
    );

    private final List<Text> lines;

    /**
     * Construct a new {@link Tooltip} instance using the provided {@link List}
     * of {@link Text} elements.
     *
     * @param lines the lines of the tooltip as a list of text
     */
    public Tooltip(List<Text> lines) {
        this.lines = lines;
    }

    /**
     * Get the amount of lines this tooltip currently holds.
     *
     * @return the amount of lines
     */
    public int size() {
        return this.lines.size();
    }

    /**
     * Check whether the tooltip holds any text.
     *
     * @return {@code true} if empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    /**
     * Clear the tooltip's content. This effectively removes any text,
     * currently held by the tooltip.
     *
     * @return the same tooltip instance
     */
    public Tooltip clear() {
        this.lines.clear();

        return this;
    }

    /**
     * Add an empty line to the tooltip.
     *
     * @return the same tooltip instance
     */
    public Tooltip addSpacer() {
        this.lines.add(Text.empty());

        return this;
    }

    /**
     * Add a header, provided by the translation key, to the tooltip.
     *
     * @param translationKey the key to grab translated text from
     * @return the same tooltip instance
     */
    public Tooltip addHeader(String translationKey) {
        this.lines.add(Text.translatable(translationKey).fillStyle(HEADER_STYLE));

        return this;
    }

    /**
     * Add a section which displays all components of the given {@link HoveredItemStack}
     * and the currently selected component.
     * If the hovered item stack doesn't have any components associated with it,
     * a single-line notice stating that is displayed instead.
     * <p>
     * The {@code showSelectedComponent} parameter can be used to toggle whether
     * the selected component is even displayed.
     *
     * @param hoveredItemStack the hovered item stack to grab the selection and components from
     * @param showSelectedComponent whether the currently selected component is shown
     * @return the same tooltip instance
     */
    public Tooltip addComponentSelection(HoveredItemStack hoveredItemStack, boolean showSelectedComponent) {
        Components components = hoveredItemStack.getComponents();

        if (components.isEmpty()) {
            this.addHeader(EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS.get(components.componentsType()));
            return this;
        }

        this.addHeader(COMPONENT_SELECTION_TRANSLATION_KEYS.get(components.componentsType()));

        // Double the indentation if more than one component needs to be displayed
        String indentationOfSelected = CONTENT_INDENTATION.repeat(Math.min(components.size(), 2));

        int indexOfSelected = (
            (showSelectedComponent)
                ? hoveredItemStack.getComponentSelection().orElseThrow().getSelectedIndex()
                : -1
        );

        // Add all component types
        for (int index = 0; index < components.size(); index++) {
            Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(components.get(index).type());
            MutableText componentTypeText = (
                (identifier == null)
                    ? Text.translatable(NOT_REGISTERED_TRANSLATION_KEY)
                        .fillStyle(COMPONENT_STYLE)
                        .fillStyle(NOT_REGISTERED_COMPONENT_STYLE)
                    : Text.literal(identifier.toString())
                        .fillStyle(COMPONENT_STYLE)
            );

            if (index == indexOfSelected) {
                componentTypeText.fillStyle(SELECTED_COMPONENT_STYLE);
            }

            if (components.isRemovedComponent(index)) {
                componentTypeText.fillStyle(REMOVED_COMPONENT_STYLE);
            }

            this.lines.add(Text.literal((index == indexOfSelected) ? indentationOfSelected : CONTENT_INDENTATION).append(componentTypeText));
        }

        return this;
    }

    /**
     * Add a {@link Component}'s value, formatted using the specified {@link Formatter},
     * to the tooltip.
     * <p>
     * If formatting fails, meaning the formatter throws a {@link FormattingException},
     * a warning stating this is displayed instead of the value.
     *
     * @param <T> the type of the component
     * @param component the component with the value to add
     * @param formatter a formatter implementation to use for formatting the value
     * @param formattingIndentation the size of indentation used for multi-line formatting
     * @param coloredFormatting whether the formatter is instructed to format using colors
     * @return the same tooltip instance
     */
    public <T> Tooltip addComponentValue(Component<T> component, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.components.value");

        try {
            this.lines.addAll(formatter.componentToText(component, formattingIndentation, coloredFormatting, CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    /**
     * Add an {@link ItemStack}'s data, formatted using the specified {@link Formatter},
     * to the toolip.
     * <p>
     * This is usually the same kind of data a player gets when using the {@code /data get} in-game
     * command. Although the exact behaviour depends on the {@link Formatter}'s implementation.
     * <p>
     * If formatting fails, meaning the formatter throws a {@link FormattingException},
     * a warning stating this is displayed instead of the item stack.
     *
     * @param itemStack the item stack to add
     * @param formatter a formatter implementation to use for formatting the item stack
     * @param formattingIndentation the size of indentation used for multi-line formatting
     * @param coloredFormatting whether the formatter is instructed to format using colors
     * @return the same tooltip instance
     */
    public Tooltip addItemStack(ItemStack itemStack, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.item_stack");

        try {
            this.lines.addAll(formatter.itemStackToText(itemStack, formattingIndentation, coloredFormatting, CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    private void addFormattingException() {
        this.lines.add(Text.literal(CONTENT_INDENTATION).append(Text.translatable("componentviewer.tooltip.formatting_exception").fillStyle(ERROR_STYLE)));
    }
}
