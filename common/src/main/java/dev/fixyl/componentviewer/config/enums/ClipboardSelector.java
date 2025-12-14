package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum ClipboardSelector implements OptionEnum {

    @SerializedName("everyone") EVERYONE("componentviewer.config.clipboard.selector.everyone"),
    @SerializedName("nearest") NEAREST("componentviewer.config.clipboard.selector.nearest"),
    @SerializedName("self") SELF("componentviewer.config.clipboard.selector.self"),
    @SerializedName("player") PLAYER("componentviewer.config.clipboard.selector.player") {
        @Override
        public Component getCaption() {
            return Component.translatable(this.getTranslationKey(), Minecraft.getInstance().getGameProfile().name());
        }
    };

    private final String serializedName;
    private final String translationKey;

    private ClipboardSelector(String translationKey) {
        this.serializedName = OptionEnum.createSerializedName(this);
        this.translationKey = translationKey;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }
}
