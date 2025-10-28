package dev.fixyl.componentviewer.control.keyboard;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;

import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.keymapping.CycleSelectionKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.EnumOptionKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.KeyMappings;
import dev.fixyl.componentviewer.config.keymapping.TickedKeyMapping;
import dev.fixyl.componentviewer.config.option.BooleanOption;
import dev.fixyl.componentviewer.event.EventDispatcher;
import dev.fixyl.componentviewer.screen.ConfigScreen;

/**
 * The base keyboard class which handles all logic related to
 * key presses and key mappings in general.
 * <p>
 * This class handles platform-agnostic logic only and needs to be
 * extended to add platform-specific logic, like registering
 * key mappings.
 */
public abstract class Keyboard {

    protected final Minecraft minecraftClient;
    protected final DisablableMod disablableMod;
    protected final EventDispatcher eventDispatcher;

    protected final List<TickedKeyMapping> tickedKeys;
    protected final List<CycleSelectionKeyMapping> cycleSelectionKeys;
    /*
     * Suppress warning for usage of a raw type.
     * There is no feasible way to fix this,
     * and the parameterized type doesn't matter in this case.
     */
    @SuppressWarnings("rawtypes")
    protected final List<EnumOptionKeyMapping> enumOptionKeys;

    private BooleanOption alternativeCopyModifierKey;
    private BooleanOption allowCyclingOptionsWhileInScreen;

    protected Keyboard(
        Minecraft minecarftClient,
        DisablableMod disablableMod,
        EventDispatcher eventDispatcher,
        KeyMappings keyMappings,
        BooleanOption alternativeCopyModifierKey,
        BooleanOption allowCyclingOptionsWhileInScreen
    ) {
        this.minecraftClient = minecarftClient;
        this.disablableMod = disablableMod;
        this.eventDispatcher = eventDispatcher;

        this.tickedKeys = keyMappings.getSubClassKeyMappings(TickedKeyMapping.class);
        this.cycleSelectionKeys = keyMappings.getSubClassKeyMappings(CycleSelectionKeyMapping.class);
        this.enumOptionKeys = keyMappings.getSubClassKeyMappings(EnumOptionKeyMapping.class);

        this.alternativeCopyModifierKey = alternativeCopyModifierKey;
        this.allowCyclingOptionsWhileInScreen = allowCyclingOptionsWhileInScreen;
    }

    /**
     * This method should be called once per client-tick.
     * <p>
     * Specifically, after all other client-logic has run.
     *
     * @implNote
     * Ticked key mappings are not disabled when this mod
     * is disabled. This is to allow opening the config
     * screen, and to allow potential option cycling
     * key mappings to update their toast references normally.
     */
    public void onEndClientTick() {
        for (TickedKeyMapping tickedKey : this.tickedKeys) {
            tickedKey.onEndClientTick();
        }
    }

    /**
     * This method should be called each time a keyboard key is pressed
     * or held. That key is then passed as an argument.
     *
     * @param keyEvent the key as a key event that was pressed or held
     */
    public void onKeyPress(KeyEvent keyEvent) {
        if (this.disablableMod.isModDisabled()) {
            return;
        }

        Key key = InputConstants.getKey(keyEvent);
        this.onInput(key);

        if (key.getType() == Type.KEYSYM && this.isCopy(keyEvent)) {
            this.eventDispatcher.invokeCopyActionEvent();
        }
    }

    /**
     * This method should be called each time a mouse button is
     * pressed or held. That button is then passed as an argument.
     *
     * @param mouseButtonInfo the mouse button that was pressed or held
     */
    public void onButtonPress(MouseButtonInfo mouseButtonInfo) {
        if (this.disablableMod.isModDisabled()) {
            return;
        }

        Key key = Type.MOUSE.getOrCreate(mouseButtonInfo.button());
        this.onInput(key);
    }

    /**
     * This will clear all toasts currently playing for any
     * option cycle key mapping.
     *
     * @see EnumOptionKeyMapping#clearToast()
     */
    public void clearAllOptionCycleToasts() {
        for (EnumOptionKeyMapping<?> enumOptionKey : this.enumOptionKeys) {
            enumOptionKey.clearToast();
        }
    }

    private void onInput(Key key) {
        for (CycleSelectionKeyMapping cycleKey : this.cycleSelectionKeys) {
            if (cycleKey.matchesKey(key)) {
                this.eventDispatcher.invokeCycleComponentEvent(cycleKey.getCycleType());
            }
        }

        if (this.isCyclingOptionsPossible()) {
            for (EnumOptionKeyMapping<?> enumOptionKey : this.enumOptionKeys) {
                enumOptionKey.cycleEnumIfKeyMatches(key);
            }
        }
    }

    private boolean isCopy(KeyEvent keyEvent) {
        return keyEvent.key() == GLFW_KEY_C && (
            (this.alternativeCopyModifierKey.getBooleanValue())
                ? keyEvent.hasAltDown()
                : keyEvent.hasControlDown()
        );
    }

    private boolean isCyclingOptionsPossible() {
        // Not possible when not in any world
        if (this.minecraftClient.level == null || this.minecraftClient.player == null) {
            return false;
        }

        // Possible if no screen is open
        if (this.minecraftClient.screen == null) {
            return true;
        }

        // Possible if option allows in-screen cycling and
        // the current screen is not a config screen of this mod
        return (
            this.allowCyclingOptionsWhileInScreen.getBooleanValue()
            && !(this.minecraftClient.screen instanceof ConfigScreen)
        );
    }
}
