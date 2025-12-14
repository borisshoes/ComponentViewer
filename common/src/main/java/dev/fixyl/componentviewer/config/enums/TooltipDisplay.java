package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipDisplay implements OptionEnum {

    @SerializedName("hold") HOLD("componentviewer.config.tooltip.display.hold"),
    @SerializedName("always") ALWAYS("componentviewer.config.tooltip.display.always"),
    @SerializedName("never") NEVER("componentviewer.config.tooltip.display.never");

    private final String serializedName;
    private final String translationKey;

    private TooltipDisplay(String translationKey) {
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
