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

package dev.fixyl.componentviewer;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.control.ControlFlow;
import dev.fixyl.componentviewer.event.KeyComboEvents;
import dev.fixyl.componentviewer.event.MixinEvents;
import dev.fixyl.componentviewer.keyboard.KeyBindings;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

@Mod(value = "componentviewer", dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public final class ComponentViewer {

    private static ComponentViewer instance;

    public final Logger logger;
    public final Configs configs;
    public final KeyBindings keyBindings;

    public ComponentViewer(ModContainer modContainer) {
        ComponentViewer.setInstance(this);

        this.logger = LoggerFactory.getLogger(this.getClass());
        this.configs = new Configs(FMLPaths.CONFIGDIR.get(), this.logger);
        this.keyBindings = new KeyBindings(this.configs);

        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (container, lastScreen) -> new MainConfigScreen(lastScreen, this.configs)
        );
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent clientSetupEvent) {
        Minecraft minecraftClient = Minecraft.getInstance();
        ComponentViewer instance = ComponentViewer.getInstance();
        ControlFlow controlFlow = new ControlFlow(minecraftClient, instance.configs);

        instance.configs.loadFromDisk();

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> instance.keyBindings.onClientTick(minecraftClient));

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> controlFlow.onClientTick());
        NeoForge.EVENT_BUS.addListener(MixinEvents.TooltipEvent.class, event -> controlFlow.onTooltip(event.itemStack, event.tooltip));
        NeoForge.EVENT_BUS.addListener(MixinEvents.MouseScrollEvent.class, event -> event.setResult(controlFlow.onMouseScroll(event.yOffset)));
        NeoForge.EVENT_BUS.addListener(KeyComboEvents.CycleComponentEvent.class, event -> controlFlow.onCycleComponent(event.cycleType));
        NeoForge.EVENT_BUS.addListener(KeyComboEvents.CopyActionEvent.class, event -> controlFlow.onCopyAction());
    }

    public static ComponentViewer getInstance() {
        if (ComponentViewer.instance == null) {
            throw new IllegalStateException(String.format(
                "'%s' hasn't been instantiated yet!",
                ComponentViewer.class.getName()
            ));
        }

        return ComponentViewer.instance;
    }

    private static void setInstance(ComponentViewer instance) {
        if (ComponentViewer.instance != null) {
            throw new IllegalStateException(String.format(
                "Cannot instantiate '%s' twice!",
                ComponentViewer.class.getName()
            ));
        }

        ComponentViewer.instance = instance;
    }
}
