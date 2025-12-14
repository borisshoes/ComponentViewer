package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum TooltipPurpose implements OptionEnum {

    @SerializedName("components") COMPONENTS("componentviewer.config.tooltip.purpose.components"),
    @SerializedName("item_stack") ITEM_STACK("componentviewer.config.tooltip.purpose.item_stack");

    private final String serializedName;
    private final String translationKey;

    private TooltipPurpose(String translationKey) {
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
