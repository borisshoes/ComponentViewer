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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.util.ResultCache;

public class Components {

    private static final Comparator<TypedDataComponent<?>> COMPARATOR = Comparator.comparing(component -> {
        ResourceLocation resourceLocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type());
        return (resourceLocation == null) ? "" : resourceLocation.toString();
    });

    private static final ResultCache<Components> COMPONENTS_CACHE = new ResultCache<>();

    private final TooltipComponents componentsType;
    private final List<TypedDataComponent<?>> componentsList;
    private final int startOfRemovedComponents;

    private Components(TooltipComponents componentsType, Set<TypedDataComponent<?>> regularComponents, Set<TypedDataComponent<?>> removedComponents) {
        this.componentsType = componentsType;

        this.componentsList = Stream.concat(regularComponents.stream().sorted(COMPARATOR), removedComponents.stream().sorted(COMPARATOR)).toList();

        this.startOfRemovedComponents = regularComponents.size();
    }

    private Components(TooltipComponents componentsType, Set<TypedDataComponent<?>> regularComponents) {
        this(componentsType, regularComponents, new HashSet<>());
    }

    public TooltipComponents componentsType() {
        return this.componentsType;
    }

    public int size() {
        return this.componentsList.size();
    }

    public boolean isEmpty() {
        return this.componentsList.isEmpty();
    }

    // Suppress the generic wildcard warning for SonarQube
    // since the actual component type doesn't matter
    // and is not known
    @SuppressWarnings("java:S1452")
    public TypedDataComponent<?> get(int index) {
        return this.componentsList.get(index);
    }

    /**
     * Get the index of any {@link TypedDataComponent} based on a given {@link DataComponentType}.
     * <p>
     * If the component type is {@code null}, it is considered non-existent.
     *
     * @param <T> the type of the component
     * @param componentType the instance representing a data component type
     * @return the index of the corresponding component, {@code -1} if no such component exists
     */
    public <T> int indexOf(@Nullable DataComponentType<T> componentType) {
        if (componentType == null) {
            return -1;
        }

        Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        ResourceLocation resourceLocation = registry.getKey(componentType);

        if (resourceLocation == null) {
            return -1;
        }

        return IntStream.range(0, this.componentsList.size())
            .filter(index -> resourceLocation.equals(registry.getKey(this.componentsList.get(index).type())))
            .findFirst()
            .orElse(-1);
    }

    public boolean isRemovedComponent(int index) {
        return index >= this.startOfRemovedComponents;
    }

    public static Components getComponentsBasedOnType(ItemStack itemStack, TooltipComponents componentsType) {
        return switch (componentsType) {
            case ALL -> getAllComponents(itemStack);
            case DEFAULT -> getDefaultComponents(itemStack);
            case CHANGES -> getChangedComponents(itemStack);
        };
    }

    public static Components getAllComponents(ItemStack itemStack) {
        return COMPONENTS_CACHE.cache(() -> {
            Set<TypedDataComponent<?>> regularComponents = Components.createComponentSet(itemStack.getComponents());

            return new Components(TooltipComponents.ALL, regularComponents);
        }, itemStack, TooltipComponents.ALL);
    }

    public static Components getDefaultComponents(ItemStack itemStack) {
        return COMPONENTS_CACHE.cache(() -> {
            Set<TypedDataComponent<?>> defaultComponents = Components.createComponentSet(itemStack.getPrototype());

            return new Components(TooltipComponents.DEFAULT, defaultComponents);
        }, itemStack, TooltipComponents.DEFAULT);
    }

    public static Components getChangedComponents(ItemStack itemStack) {
        return COMPONENTS_CACHE.cache(() -> {
            Set<TypedDataComponent<?>> regularComponents = Components.createComponentSet(itemStack.getComponents());
            Set<TypedDataComponent<?>> defaultComponents = Components.createComponentSet(itemStack.getPrototype());

            Set<TypedDataComponent<?>> changedComponents = new HashSet<>(regularComponents);
            changedComponents.removeAll(defaultComponents);

            Set<TypedDataComponent<?>> removedComponents = new HashSet<>(defaultComponents);
            Set<DataComponentType<?>> componentTypes = regularComponents.stream().map(TypedDataComponent::type).collect(Collectors.toSet());
            removedComponents.removeIf(defaultComponent -> componentTypes.contains(defaultComponent.type()));

            return new Components(TooltipComponents.CHANGES, changedComponents, removedComponents);
        }, itemStack, TooltipComponents.CHANGES);
    }

    private static Set<TypedDataComponent<?>> createComponentSet(DataComponentMap componentMap) {
        Set<TypedDataComponent<?>> componentSet = new HashSet<>();

        for (TypedDataComponent<?> component : componentMap) {
            componentSet.add(component);
        }

        return componentSet;
    }
}
