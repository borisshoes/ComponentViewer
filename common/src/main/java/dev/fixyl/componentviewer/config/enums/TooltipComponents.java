package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipComponents implements OptionEnum {

    @SerializedName("all") ALL("componentviewer.config.tooltip.components.all"),
    @SerializedName("default") DEFAULT("componentviewer.config.tooltip.components.default"),
    @SerializedName("changes") CHANGES("componentviewer.config.tooltip.components.changes");

    private final String serializedName;
    private final String translationKey;

    private TooltipComponents(String translationKey) {
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
