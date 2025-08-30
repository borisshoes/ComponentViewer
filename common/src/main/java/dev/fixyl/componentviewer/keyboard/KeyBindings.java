package dev.fixyl.componentviewer.keyboard;

import static org.lwjgl.glfw.GLFW.*;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.ClipboardFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipInjectMethod;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.keyboard.keybinding.AdvancedKeyBinding;
import dev.fixyl.componentviewer.keyboard.keybinding.EnumOptionKeyBinding;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

public final class KeyBindings {

    private static final String GENERAL_CATEGORY = "componentviewer.keybind.general";
    private static final String CONFIG_CATEGORY = "componentviewer.keybind.config";

    public final AdvancedKeyBinding configScreenKey;
    public final EnumOptionKeyBinding<TooltipDisplay> tooltipDisplayConfigKey;
    public final EnumOptionKeyBinding<TooltipPurpose> tooltipPurposeConfigKey;
    public final EnumOptionKeyBinding<TooltipComponents> tooltipComponentsConfigKey;
    public final EnumOptionKeyBinding<TooltipFormatting> tooltipFormattingConfigKey;
    public final EnumOptionKeyBinding<TooltipInjectMethod> tooltipInjectMethodConfigKey;
    public final EnumOptionKeyBinding<ClipboardCopy> clipboardCopyConfigKey;
    public final EnumOptionKeyBinding<ClipboardFormatting> clipboardFormattingConfigKey;

    private final Configs configs;

    public KeyBindings(Configs configs) {
        this.configs = configs;

        this.configScreenKey = new AdvancedKeyBinding(
            "componentviewer.keybind.general.config_screen",
            GLFW_KEY_J,
            GENERAL_CATEGORY
        );
        this.tooltipDisplayConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.tooltip_display",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.tooltipDisplay
        );
        this.tooltipPurposeConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.tooltip_purpose",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.tooltipPurpose
        );
        this.tooltipComponentsConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.tooltip_components",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.tooltipComponents
        );
        this.tooltipFormattingConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.tooltip_formatting",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.tooltipFormatting
        );
        this.tooltipInjectMethodConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.tooltip_inject_method",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.tooltipInjectMethod
        );
        this.clipboardCopyConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.clipboard_copy",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.clipboardCopy
        );
        this.clipboardFormattingConfigKey = new EnumOptionKeyBinding<>(
            "componentviewer.keybind.config.clipboard_formatting",
            GLFW_KEY_UNKNOWN,
            CONFIG_CATEGORY,
            configs.clipboardFormatting
        );
    }

    public KeyMapping[] getBindings() {
        return new KeyMapping[] {
            this.configScreenKey,
            this.tooltipDisplayConfigKey,
            this.tooltipPurposeConfigKey,
            this.tooltipComponentsConfigKey,
            this.tooltipFormattingConfigKey,
            this.tooltipInjectMethodConfigKey,
            this.clipboardCopyConfigKey,
            this.clipboardFormattingConfigKey
        };
    }

    public void onClientTick(Minecraft minecraftClient) {
        this.configScreenKey.onPressed(() -> minecraftClient.setScreen(new MainConfigScreen(null, this.configs)));

        this.tooltipDisplayConfigKey.cycleValueOnPressed();
        this.tooltipPurposeConfigKey.cycleValueOnPressed();
        this.tooltipComponentsConfigKey.cycleValueOnPressed();
        this.tooltipFormattingConfigKey.cycleValueOnPressed();
        this.tooltipInjectMethodConfigKey.cycleValueOnPressed();
        this.clipboardCopyConfigKey.cycleValueOnPressed();
        this.clipboardFormattingConfigKey.cycleValueOnPressed();
    }
}
