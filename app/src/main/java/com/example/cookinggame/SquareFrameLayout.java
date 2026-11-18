package com.example.cookinggame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/** A FrameLayout that is always square: as big as fits in the space it's given. */
public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean widthKnown = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED;
        boolean heightKnown = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (widthKnown && heightKnown) {
            size = Math.min(width, height);
        } else if (widthKnown) {
            size = width;
        } else if (heightKnown) {
            size = height;
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int square = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(square, square);
    }
}
