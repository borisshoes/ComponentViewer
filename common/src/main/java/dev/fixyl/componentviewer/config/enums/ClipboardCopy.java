package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum ClipboardCopy implements OptionEnum {

    @SerializedName("component_value") COMPONENT_VALUE("componentviewer.config.clipboard.copy.component_value"),
    @SerializedName("item_stack") ITEM_STACK("componentviewer.config.clipboard.copy.item_stack"),
    @SerializedName("give_command") GIVE_COMMAND("componentviewer.config.clipboard.copy.give_command"),
    @SerializedName("disabled") DISABLED("componentviewer.config.clipboard.copy.disabled");

    private final String serializedName;
    private final String translationKey;

    private ClipboardCopy(String translationKey) {
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
