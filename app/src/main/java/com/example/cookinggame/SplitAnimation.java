package com.example.cookinggame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

/**
 * Plays a fruit's "cut open in the middle" animation from a strip of 10 frames
 * side by side (Pixel Fruits: Cut & Peel pack, 64x32 per frame).
 */
public final class SplitAnimation {

    private SplitAnimation() {}

    /** Frame timings suggested by the asset pack: whole, knife flash, split, open, rest. */
    private static final int[] FRAME_MS = {260, 70, 70, 70, 80, 80, 90, 100, 120, 600};

    /** Starts the animation in view and returns how long it lasts in ms. */
    public static int play(ImageView view, @DrawableRes int stripRes) {
        Resources res = view.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap strip = BitmapFactory.decodeResource(res, stripRes, options);

        int frames = FRAME_MS.length;
        int frameWidth = strip.getWidth() / frames;
        AnimationDrawable animation = new AnimationDrawable();
        animation.setOneShot(true);

        int total = 0;
        for (int i = 0; i < frames; i++) {
            Bitmap frame = Bitmap.createBitmap(strip, i * frameWidth, 0, frameWidth, strip.getHeight());
            BitmapDrawable drawable = new BitmapDrawable(res, frame);
            drawable.setFilterBitmap(false); // keep pixels sharp
            animation.addFrame(drawable, FRAME_MS[i]);
            total += FRAME_MS[i];
        }

        view.setImageDrawable(animation);
        animation.start();
        return total;
    }
}
