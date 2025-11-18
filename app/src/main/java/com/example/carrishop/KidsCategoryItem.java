package com.example.carrishop;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * Modelo simple para cada botón interactivo del modo niños.
 */
public class KidsCategoryItem {
    private final String id;
    private final String label;
    private final String symbol;
    @ColorInt
    private final int iconColor;
    private final String audioName;

    public KidsCategoryItem(@NonNull String id,
                            @NonNull String label,
                            String symbol,
                            @ColorInt int iconColor,
                            String audioName) {
        this.id = id;
        this.label = label;
        this.symbol = symbol;
        this.iconColor = iconColor;
        this.audioName = audioName;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getIconColor() {
        return iconColor;
    }

    public String getAudioName() {
        return audioName;
    }
}
