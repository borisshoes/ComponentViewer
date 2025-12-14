package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public enum ClipboardFormatting implements OptionEnum {

    @SerializedName("sync") SYNC("componentviewer.config.clipboard.formatting.sync"),
    @SerializedName("snbt") SNBT("componentviewer.config.clipboard.formatting.snbt"),
    @SerializedName("json") JSON("componentviewer.config.clipboard.formatting.json"),
    @SerializedName("object") OBJECT("componentviewer.config.clipboard.formatting.object");

    private final String serializedName;
    private final String translationKey;

    private ClipboardFormatting(String translationKey) {
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
