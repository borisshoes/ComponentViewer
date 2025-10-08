package dev.fixyl.componentviewer.config.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * An {@link AdvancedKeyMapping} is a regular {@link KeyMapping} with
 * added functionality.
 * <p>
 * This key mapping is the baseline for key mappings used by this mod.
 */
public class AdvancedKeyMapping extends KeyMapping {

    public static final Category GENERAL_CATEGORY = AdvancedKeyMapping.registerCategory("controls");
    public static final Category CONFIG_CATEGORY = AdvancedKeyMapping.registerCategory("controls.cycle_configs");

    private static final String CATEGORY_NAMESPACE = "componentviewer";

    private final ConflictContext conflictContext;

    public AdvancedKeyMapping(String translationKey, int keyCode, Category category, ConflictContext conflictContext) {
        super(translationKey, keyCode, category);

        this.conflictContext = conflictContext;
    }

    public AdvancedKeyMapping(String translationKey, int keyCode, Category category) {
        this(translationKey, keyCode, category, ConflictContext.getDefault());
    }

    /**
     * Get the {@link ConflictContext} in which this key mapping
     * consideres itself conflicting with others.
     * <p>
     * Conflict contexts have no use-case on the Fabric platform.
     *
     * @return the conflict context of this mapping
     */
    public ConflictContext getConfictContext() {
        return this.conflictContext;
    }

    /**
     * Check whether the provided key event represents a key that is
     * the same as the one currently associated with this key mapping.
     *
     * @param keyEvent the key event to match
     * @return {@code true} if the key event matches, {@code false} otherwise
     */
    public boolean matchesKeyEvent(KeyEvent keyEvent) {
        return this.key.equals(InputConstants.getKey(keyEvent));
    }

    /**
     * Check whether the key, associated with this mapping, is currently
     * held down while the game is running.
     * <p>
     * Unlike {@link KeyMapping#isDown()}, this method will
     * also return {@code true} if the key is pressed inside screens,
     * or basically anywhere from within the game.
     *
     * @return {@code true} if the key is currently held down, {@code false} otherwise
     */
    public boolean isDownAnywhere() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) {
            return false;
        }

        Window window = minecraftClient.getWindow();
        if (window == null) {
            return false;
        }

        return InputConstants.isKeyDown(window, this.key.getValue());
    }

    /**
     * Defines on what layer keys are considered conflicting.
     * <p>
     * This has no use-case on the Fabric platform.
     */
    public enum ConflictContext {
        /**
         * Everywhere
         */
        UNIVERSAL,
        /**
         * When in a screen
         */
        IN_SCREEN,
        /**
         * When not in any screen (in game)
         */
        IN_GAME;

        public static ConflictContext getDefault() {
            return UNIVERSAL;
        }
    }

    private static Category registerCategory(String id) {
        return Category.register(
            ResourceLocation.fromNamespaceAndPath(CATEGORY_NAMESPACE, id)
        );
    }
}
