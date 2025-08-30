package dev.fixyl.componentviewer.control;

import java.util.Optional;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

public class HoveredItemStack {

    private final ItemStack itemStack;
    private final Configs configs;

    private Components components;
    private @Nullable Selection componentSelection;

    private boolean componentTypeChanged;
    private @Nullable DataComponentType<?> previousSelectedComponentType;

    public HoveredItemStack(ItemStack itemStack, Configs configs) {
        this.itemStack = itemStack;
        this.configs = configs;

        this.componentTypeChanged = false;
        this.previousSelectedComponentType = null;
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
     * It is not guaranteed that this is always the same instance.
     *
     * @implNote
     * This method and {@link HoveredItemStack#getComponentSelection() getComponentSelection()}
     * follow a hierarchical order, where calling {@code getComponentSelection()} from
     * within this method is strictly forbidden since it might result in an unintended infinite
     * recursion. This is because both associated fields are "lazy", meaning they will only
     * be updated when one of either method is called. Therefore, {@code getComponentSelection()}
     * depends on this method to update the components.
     *
     * @return all currently relevant components
     */
    public Components getComponents() {
        TooltipComponents componentsTypeFromConfig = this.configs.tooltipComponents.getValue();

        if (this.components == null) {
            this.components = Components.getComponentsBasedOnType(this.itemStack, componentsTypeFromConfig);
        } else if (this.components.componentsType() != componentsTypeFromConfig) {
            this.componentTypeChanged = true;
            this.previousSelectedComponentType = (
                (this.componentSelection == null)
                    ? null
                    : this.components.get(this.componentSelection.getSelectedIndex()).type()
            );

            this.components = Components.getComponentsBasedOnType(this.itemStack, componentsTypeFromConfig);
        }

        return this.components;
    }

    /**
     * Get a {@link Selection} instance wrapped in an {@link Optional} representing
     * the currently selected component. To update the selection based on user input, use
     * {@link Selection#updateByCycling(Selection.CycleType) updateByCycling(CycleType)} and
     * {@link Selection#updateByScrolling(double) updateByScrolling(double)}
     * on the {@link Selection} instance.
     * <p>
     * It is not guaranteed that this is always the same instance.
     * <p>
     * An empty {@link Optional} is returned when no {@link Selection} instance is associated
     * with this {@link HoveredItemStack}. This is the case when the associated {@link Components}
     * instance has no components.
     *
     * @return the component selection, if present
     */
    public Optional<Selection> getComponentSelection() {
        int amountOfComponents = this.getComponents().size();

        if (amountOfComponents <= 0) {
            this.componentSelection = null;
        } else if (this.componentSelection == null) {
            this.componentSelection = new Selection(amountOfComponents);
        } else if (this.componentTypeChanged) {
            this.componentSelection.setAmount(amountOfComponents);

            int selectedIndex = this.componentSelection.getSelectedIndex();
            this.componentSelection.updateByValue(switch (this.configs.tooltipKeepSelection.getValue()) {
                case INDEX -> selectedIndex;
                case TYPE -> {
                    int indexOfType = this.components.indexOf(this.previousSelectedComponentType);

                    yield (indexOfType < 0) ? selectedIndex : indexOfType;
                }
                case NEVER -> 0;
            });

            this.componentTypeChanged = false;
        }

        return Optional.ofNullable(this.componentSelection);
    }

    /**
     * Get the {@link TypedDataComponent} instance wrapped in an {@link Optional}
     * which is currently selected by the player.
     * <p>
     * The {@link Optional} will be empty if no component selection exists, meaning
     * {@link HoveredItemStack#getComponentSelection() getComponentSelection()}
     * returns an empty {@link Optional} as well.
     *
     * @implNote
     * SonarQube warning for returning a wildcard generic in non-private methods is suppressed.
     * The component type isn't known, even for the method itself, and doesn't matter at all.
     *
     * @return the currently selected component, if a component selection exists
     * @see HoveredItemStack#getComponents()
     * @see HoveredItemStack#getComponentSelection()
     */
    @SuppressWarnings("java:S1452")
    public Optional<TypedDataComponent<?>> getSelectedComponent() {
        Optional<Selection> optionalComponentSelection = this.getComponentSelection();

        if (optionalComponentSelection.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.getComponents().get(
            optionalComponentSelection.orElseThrow().getSelectedIndex()
        ));
    }
}
