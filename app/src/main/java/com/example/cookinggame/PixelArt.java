package com.example.cookinggame;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

/** Helpers for showing pixel art without blurring it. */
public final class PixelArt {

    private PixelArt() {}

    /** Sets an image without smoothing, so pixel art stays crisp when scaled up. */
    public static void setImage(ImageView view, @DrawableRes int drawableRes) {
        view.setImageDrawable(crisp(ContextCompat.getDrawable(view.getContext(), drawableRes)));
    }

    /** Turns off smoothing on a bitmap drawable (no effect on shape placeholders). */
    public static Drawable crisp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            ((BitmapDrawable) drawable).setFilterBitmap(false);
            ((BitmapDrawable) drawable).setAntiAlias(false);
        }
        return drawable;
    }
}
