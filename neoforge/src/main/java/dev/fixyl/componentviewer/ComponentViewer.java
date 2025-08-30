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
