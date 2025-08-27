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

package dev.fixyl.componentviewer.mixin;

import static net.minecraft.world.item.TooltipFlag.*;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Tooltip;
import dev.fixyl.componentviewer.event.MixinEvents;

@Mixin(value = RecipeButton.class, priority = Integer.MAX_VALUE)
public final class RecipeButtonMixin {

    private RecipeButtonMixin() {}

    @Redirect(method = "getTooltipText(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;getTooltipFromItem(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private static List<Component> getTooltipFromItem(Minecraft minecraftClient, ItemStack stack) {
        return stack.getTooltipLines(
            TooltipContext.of(minecraftClient.level),
            minecraftClient.player,
            minecraftClient.options.advancedItemTooltips ? ADVANCED : NORMAL
        );
    }

    @Inject(method = "getTooltipText(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "RETURN"))
    private void getTooltipText(ItemStack stack, CallbackInfoReturnable<List<Component>> callback) {
        MixinEvents.TOOLTIP_EVENT.invoker().onTooltip(stack, new Tooltip(callback.getReturnValue()));
    }
}
