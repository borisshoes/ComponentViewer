package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipFormatting implements OptionEnum {

    @SerializedName("snbt") SNBT("componentviewer.config.tooltip.formatting.snbt"),
    @SerializedName("json") JSON("componentviewer.config.tooltip.formatting.json"),
    @SerializedName("object") OBJECT("componentviewer.config.tooltip.formatting.object");

    private final String serializedName;
    private final String translationKey;

    private TooltipFormatting(String translationKey) {
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
