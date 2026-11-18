package com.example.cookinggame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;

import java.util.Locale;

/**
 * Pictures for ingredients (and in-between foods like "Batter").
 *
 * To add real art for an ingredient, put a PNG in res/drawable named
 * "ingredient_" + the name in lower case with spaces/symbols as underscores:
 *   "Chocolate bar"    -> ingredient_chocolate_bar.png
 *   "Onion & cilantro" -> ingredient_onion_cilantro.png
 * No code changes needed. Until then a colored square placeholder is drawn.
 * Pixel art goes in res/drawable-nodpi so Android doesn't blur it by rescaling.
 */
public final class FoodArt {

    private FoodArt() {}

    public static Drawable ingredient(Context context, String name) {
        int res = drawableFor(context, name);
        if (res != 0) {
            return PixelArt.crisp(ContextCompat.getDrawable(context, res));
        }

        float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable square = new GradientDrawable();
        square.setShape(GradientDrawable.RECTANGLE);
        square.setColor(placeholderColor(name));
        square.setStroke(Math.round(3 * density), ContextCompat.getColor(context, R.color.book_ink));
        return square;
    }

    /** A soft color picked from the name, so the same ingredient always matches. */
    private static int placeholderColor(String name) {
        int h = name.hashCode();
        h ^= h >>> 16;
        h *= 0x45d9f3b;
        h ^= h >>> 16;
        float hue = (h & 0xFFFF) / 65536f * 360f;
        return Color.HSVToColor(new float[]{hue, 0.45f, 0.88f});
    }

    /**
     * The "cut open" animation strip for an item, or 0 if there is none.
     * Named "anim_open_" + slug, e.g. "Dragon fruit" -> anim_open_dragon_fruit.png
     * (10 frames side by side, from the Pixel Fruits: Cut & Peel pack).
     */
    public static int openAnimation(Context context, String name) {
        return lookup(context, "anim_open_" + slug(name));
    }

    private static int drawableFor(Context context, String name) {
        return lookup(context, "ingredient_" + slug(name));
    }

    private static String slug(String name) {
        return name.toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }

    // Looking art up by name lets new PNGs work without touching any code.
    @SuppressLint("DiscouragedApi")
    private static int lookup(Context context, String resourceName) {
        return context.getResources().getIdentifier(
                resourceName, "drawable", context.getPackageName());
    }
}
