package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipKeepSelection implements OptionEnum {

    @SerializedName("index") INDEX("componentviewer.config.tooltip.keep_selection.index"),
    @SerializedName("type") TYPE("componentviewer.config.tooltip.keep_selection.type"),
    @SerializedName("never") NEVER("componentviewer.config.tooltip.keep_selection.never");

    private final String serializedName;
    private final String translationKey;

    private TooltipKeepSelection(String translationKey) {
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
