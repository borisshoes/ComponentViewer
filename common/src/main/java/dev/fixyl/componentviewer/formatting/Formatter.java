package dev.fixyl.componentviewer.formatting;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public interface Formatter {

    public static final Style NO_COLOR_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);

    public <T> String componentToString(TypedDataComponent<T> component, int indentation, String linePrefix);

    public <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored, String linePrefix);

    public String itemStackToString(ItemStack itemStack, int indentation, String linePrefix);

    public List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored, String linePrefix);

    public default <T> String componentToString(TypedDataComponent<T> component, int indentation) {
        return this.componentToString(component, indentation, "");
    }

    public default <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored) {
        return this.componentToText(component, indentation, colored, "");
    }

    public default String itemStackToString(ItemStack itemStack, int indentation) {
        return this.itemStackToString(itemStack, indentation, "");
    }

    public default List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored) {
        return this.itemStackToText(itemStack, indentation, colored, "");
    }
}
