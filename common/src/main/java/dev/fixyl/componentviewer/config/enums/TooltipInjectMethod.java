package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipInjectMethod implements OptionEnum {

    @SerializedName("replace") REPLACE("componentviewer.config.tooltip.inject_method.replace"),
    @SerializedName("append") APPEND("componentviewer.config.tooltip.inject_method.append");

    private final String serializedName;
    private final String translationKey;

    private TooltipInjectMethod(String translationKey) {
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
