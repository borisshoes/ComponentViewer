package dev.fixyl.componentviewer.control.component;

import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

final class PatchedItemStackComponents extends ItemStackComponents {

    private final Supplier<DataComponentPatch> dataComponentPatch;

    PatchedItemStackComponents(ItemStack itemStack, Supplier<DataComponentPatch> dataComponentPatch, TooltipComponents componentContext) {
        super(itemStack, componentContext);

        this.dataComponentPatch = dataComponentPatch;
    }

    @Override
    public int size() {
        return this.dataComponentPatch.get().size();
    }

    @Override
    public boolean isEmpty() {
        return this.dataComponentPatch.get().isEmpty();
    }

    @Override
    public List<DataComponentType<?>> getComponentTypes() {
        return this.dataComponentPatch.get().entrySet()
            .stream()
            .<DataComponentType<?>>map(Entry::getKey)
            .sorted(COMPARATOR)
            .toList();
    }

    // Suppress SonarQube warning for not-handling Optionals the way
    // you should (null check can be made useless).
    // This doesn't work here because `DataComponentPatch.get`
    // can return `null` nonetheless. We don't have control over this.
    @SuppressWarnings("java:S2789")
    @Override
    public <T> @NullPermitted T getValue(DataComponentType<T> dataComponentType) {
        Optional<? extends T> optionalValue = this.dataComponentPatch.get().get(dataComponentType);

        if (optionalValue == null) {
            return null;
        } else if (optionalValue.isEmpty()) {
            return itemStack.getPrototype().get(dataComponentType);
        }

        return optionalValue.orElseThrow();
    }

    @SuppressWarnings("java:S2789")
    @Override
    public <T> boolean wasRemoved(DataComponentType<T> dataComponentType) {
        Optional<? extends T> optionalValue = this.dataComponentPatch.get().get(dataComponentType);

        if (optionalValue == null) {
            return false;
        }

        return optionalValue.isEmpty();
    }
}
