package dev.fixyl.componentviewer.keyboard.keybinding;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.util.OptionEnum;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.control.notification.EnumOptionToast;

public class EnumOptionKeyBinding<E extends Enum<E> & OptionEnum> extends AdvancedKeyBinding {

    private final EnumOption<E> option;

    private @Nullable EnumOptionToast<E> optionToast;

    public EnumOptionKeyBinding(String translationKey, int code, String category, EnumOption<E> option) {
        super(translationKey, code, category);

        this.option = option;
    }

    public void cycleValueOnPressed() {
        if (this.optionToast != null && this.optionToast.getWantedVisibility() == Toast.Visibility.HIDE) {
            this.optionToast = null;
        }

        this.onPressed(() -> {
            this.option.cycleValue();

            if (this.optionToast == null) {
                this.optionToast = EnumOptionToast.dispatch(option, this.getName());
            } else {
                this.optionToast.resetTimer();
            }
        });
    }
}
