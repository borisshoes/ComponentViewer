package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum DisableMod implements OptionEnum {

    @SerializedName("never") NEVER("componentviewer.config.disable_mod.never"),
    @SerializedName("in_survival") IN_SURVIVAL("componentviewer.config.disable_mod.in_survival"),
    @SerializedName("on_server") ON_SERVER("componentviewer.config.disable_mod.on_server"),
    @SerializedName("both") BOTH("componentviewer.config.disable_mod.both");

    private final String serializedName;
    private final String translationKey;

    private DisableMod(String translationKey) {
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
