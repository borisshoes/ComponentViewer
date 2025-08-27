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

package dev.fixyl.componentviewer.config.option;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

public abstract class AdvancedOption<T> {

    private static final String ID_REGEX = "^[a-z]++(?:_[a-z]++)*+(?:\\.[a-z]++(?:_[a-z]++)*+)*+$";

    protected OptionInstance<T> option;

    protected final String id;
    protected final T defaultValue;
    protected final String translationKey;
    protected final OptionInstance.TooltipSupplier<T> tooltipSupplier;
    protected final @Nullable Function<T, String> translationKeyOverwrite;
    protected final @Nullable BooleanSupplier dependencyFulfillmentSupplier;
    protected final Consumer<T> changeCallback;

    protected AdvancedOption(AdvancedOptionBuilder<T, ?, ?> builder) {
        Objects.requireNonNull(builder.id, "Option id not specified");

        if (!builder.id.matches(ID_REGEX)) {
            throw new IllegalArgumentException(String.format("Invalid option id '%s'", builder.id));
        }

        this.id = builder.id;
        this.defaultValue = Objects.requireNonNull(builder.defaultValue, "Default value not specified");
        this.translationKey = Objects.toString(builder.translationKey);
        this.tooltipSupplier = (
            (builder.descriptionTranslationKey == null)
                ? OptionInstance.noTooltip()
                : OptionInstance.cachedConstantTooltip(Component.translatable(builder.descriptionTranslationKey))
        );
        this.translationKeyOverwrite = builder.translationKeyOverwrite;
        this.dependencyFulfillmentSupplier = builder.dependencyFulfillmentSupplier;
        this.changeCallback = Objects.requireNonNullElse(builder.changeCallback, value -> {});
    }

    protected final void postConstruct() {
        this.option = this.createOptionInstance(
            this.translationKey,
            this.tooltipSupplier,
            AdvancedOption.createCaptionBasedToString(this.translationKeyOverwrite, this::getDefaultCaptionBasedToString),
            this.defaultValue,
            this.changeCallback
        );
    }

    public Type getType() {
        Type type = this.getClass().getGenericSuperclass();

        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[0];
        }

        throw new IllegalStateException("AdvancedOption doesn't have a type although it's necessary?");
    }

    public String getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public T getValue() {
        return this.option.get();
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void setValue(@Nullable T value) {
        if (value == null) {
            this.resetValue();
            return;
        }

        this.option.set(value);
    }

    public void resetValue() {
        this.option.set(this.defaultValue);
    }

    public Tooltip getTooltip() {
        return this.tooltipSupplier.apply(this.getValue());
    }

    public AbstractWidget createWidget(int x, int y, int width, Consumer<T> changeCallback) {
        return this.option.createButton(
            Minecraft.getInstance().options,
            x,
            y,
            width,
            changeCallback
        );
    }

    public boolean isDependent() {
        return this.dependencyFulfillmentSupplier != null;
    }

    public boolean isDependencyFulfilled() {
        return !this.isDependent() || this.dependencyFulfillmentSupplier.getAsBoolean();
    }

    protected abstract OptionInstance<T> createOptionInstance(String translationkey, OptionInstance.TooltipSupplier<T> tooltipSupplier, OptionInstance.CaptionBasedToString<T> captionBasedToString, T defaultValue, Consumer<T> changeCallback);

    protected abstract OptionInstance.CaptionBasedToString<T> getDefaultCaptionBasedToString();

    private static <T> OptionInstance.CaptionBasedToString<T> createCaptionBasedToString(@Nullable Function<T, String> translationKeyOverwrite, Supplier<OptionInstance.CaptionBasedToString<T>> defaultSupplier) {
        if (translationKeyOverwrite == null) {
            return defaultSupplier.get();
        }

        return (optionText, value) -> {
            String translationKey = translationKeyOverwrite.apply(value);

            if (translationKey == null) {
                return Component.literal(Objects.toString(null));
            }

            return Component.translatable(translationKey, value);
        };
    }

    public abstract static class AdvancedOptionBuilder<T, O extends AdvancedOption<T>, B extends AdvancedOptionBuilder<T, O, B>> {

        protected String id;
        protected T defaultValue;
        protected @Nullable String translationKey;
        protected @Nullable String descriptionTranslationKey;
        protected @Nullable Function<T, String> translationKeyOverwrite;
        protected @Nullable BooleanSupplier dependencyFulfillmentSupplier;
        protected @Nullable Consumer<T> changeCallback;

        protected AdvancedOptionBuilder(String id) {
            this.id = id;
        }

        public B setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this.self();
        }

        public B setTranslationKey(@Nullable String translationKey) {
            this.translationKey = translationKey;
            return this.self();
        }

        public B setDescriptionTranslationKey(@Nullable String descriptionTranslationKey) {
            this.descriptionTranslationKey = descriptionTranslationKey;
            return this.self();
        }

        public B setTranslationKeyOverwrite(@Nullable Function<T, String> translationKeyOverwrite) {
            this.translationKeyOverwrite = translationKeyOverwrite;
            return this.self();
        }

        public B setDependency(@Nullable BooleanSupplier dependencyFulfillmentSupplier) {
            this.dependencyFulfillmentSupplier = dependencyFulfillmentSupplier;
            return this.self();
        }

        public B setChangeCallback(@Nullable Consumer<T> changeCallback) {
            this.changeCallback = changeCallback;
            return this.self();
        }

        public abstract O build();

        protected abstract B self();
    }
}
