package dev.fixyl.componentviewer.keyboard.keybinding;

import net.minecraft.client.KeyMapping;

public class AdvancedKeyBinding extends KeyMapping {

    public AdvancedKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    public void onPressed(Runnable runnable) {
        while (this.consumeClick()) {
            runnable.run();
        }
    }
}
