package com.example.cookinggame;

import androidx.annotation.DrawableRes;

/**
 * The workstation shown on the right side of the kitchen table for a step.
 * To use real art, replace the matching res/drawable/station_*.xml with a PNG of the same name.
 */
public enum Station {
    BOARD("CUTTING BOARD", R.drawable.station_board),
    BOWL("BOWL", R.drawable.station_bowl),
    PAN("PAN", R.drawable.station_pan),
    BLENDER("BLENDER", R.drawable.station_blender),
    CUP("CUP", R.drawable.station_cup),
    PLATE("PLATE", R.drawable.station_plate);

    public final String label;
    @DrawableRes
    public final int imageRes;

    Station(String label, @DrawableRes int imageRes) {
        this.label = label;
        this.imageRes = imageRes;
    }
}
