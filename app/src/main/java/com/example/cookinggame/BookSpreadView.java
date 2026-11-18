package com.example.cookinggame;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;

/**
 * Lays out a screen like a physical book, for any size foldable.
 *
 * Children are found by id:
 *   bookCover     - shown when the phone is folded (the outside of the book)
 *   bookLeftPage  - left page when open (top page on a top/bottom "flip" fold)
 *   bookRightPage - right page when open (bottom page on a flip fold)
 *   bookSpine     - optional; drawn exactly over the phone's hinge
 *
 * The split between pages comes from the real hinge position reported by
 * Jetpack WindowManager, so the spine lines up with the fold on every device.
 * Big screens with no hinge (tablets, landscape phones) split down the middle.
 */
public class BookSpreadView extends ViewGroup {

    public enum State {
        /** Folded / narrow screen: show the cover. */
        COVER,
        /** Open part way, like holding a book (Device Posture "half opened"). */
        HALF_OPENED,
        /** Fully open and flat. */
        OPEN
    }

    public interface OnStateChangedListener {
        void onBookStateChanged(State state);
    }

    /** Screens at least this wide (with no hinge) show as an open book. */
    private static final int MIN_SPREAD_WIDTH_DP = 600;
    /** Spine is at least this thick, even when the hinge is a zero-width line. */
    private static final int SPINE_MIN_WIDTH_DP = 12;
    /** Cover-colored border around the open pages. */
    private static final int PAGE_MARGIN_DP = 12;

    private View cover;
    private View leftPage;
    private View rightPage;
    private View spine;

    private FoldingFeature fold;
    private State state = State.COVER;
    private boolean stateReported = false;
    private OnStateChangedListener listener;

    private WindowInfoTrackerCallbackAdapter windowTracker;
    private final Consumer<WindowLayoutInfo> layoutInfoConsumer = this::onWindowLayoutInfo;

    private final Rect coverRect = new Rect();
    private final Rect leftRect = new Rect();
    private final Rect rightRect = new Rect();
    private final Rect spineRect = new Rect();
    private final int[] windowLocation = new int[2];

    public BookSpreadView(Context context) {
        super(context);
    }

    public BookSpreadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookSpreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public State getState() {
        return state;
    }

    /** Listener is called on the main thread whenever the book opens, closes or half-opens. */
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cover = findViewById(R.id.bookCover);
        leftPage = findViewById(R.id.bookLeftPage);
        rightPage = findViewById(R.id.bookRightPage);
        spine = findViewById(R.id.bookSpine);
    }

    // ---- Fold tracking ----

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Activity activity = findActivity(getContext());
        if (activity == null) return; // e.g. the Android Studio layout preview

        windowTracker = new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(activity));
        windowTracker.addWindowLayoutInfoListener(
                activity, ContextCompat.getMainExecutor(activity), layoutInfoConsumer);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (windowTracker != null) {
            windowTracker.removeWindowLayoutInfoListener(layoutInfoConsumer);
            windowTracker = null;
        }
        super.onDetachedFromWindow();
    }

    private void onWindowLayoutInfo(WindowLayoutInfo info) {
        fold = null;
        for (DisplayFeature feature : info.getDisplayFeatures()) {
            if (feature instanceof FoldingFeature) {
                fold = (FoldingFeature) feature;
                break;
            }
        }
        requestLayout();
    }

    // ---- Layout ----

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        computeLayout(width, height);

        boolean closed = state == State.COVER;
        setShown(cover, closed);
        setShown(leftPage, !closed);
        setShown(rightPage, !closed);
        setShown(spine, !closed);

        if (closed) {
            measureInto(cover, coverRect);
        } else {
            measureInto(leftPage, leftRect);
            measureInto(rightPage, rightRect);
            measureInto(spine, spineRect);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (state == State.COVER) {
            layoutInto(cover, coverRect);
        } else {
            layoutInto(leftPage, leftRect);
            layoutInto(rightPage, rightRect);
            layoutInto(spine, spineRect);
        }
    }

    /** Works out the state and where the cover / pages / spine go, in this view's coordinates. */
    private void computeLayout(int width, int height) {
        float density = getResources().getDisplayMetrics().density;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = width - getPaddingRight();
        int bottom = height - getPaddingBottom();
        coverRect.set(left, top, right, bottom);

        State newState;
        boolean splitLeftRight;
        Rect hinge;

        if (fold != null) {
            // Hinge bounds are in window coordinates; convert to ours.
            getLocationInWindow(windowLocation);
            hinge = new Rect(fold.getBounds());
            hinge.offset(-windowLocation[0], -windowLocation[1]);
            newState = fold.getState() == FoldingFeature.State.HALF_OPENED
                    ? State.HALF_OPENED : State.OPEN;
            splitLeftRight = fold.getOrientation() == FoldingFeature.Orientation.VERTICAL;
        } else if ((right - left) / density >= MIN_SPREAD_WIDTH_DP) {
            int middle = (left + right) / 2;
            hinge = new Rect(middle, top, middle, bottom);
            newState = State.OPEN;
            splitLeftRight = true;
        } else {
            setState(State.COVER);
            return;
        }

        int margin = Math.round(PAGE_MARGIN_DP * density);
        int spineMin = Math.round(SPINE_MIN_WIDTH_DP * density);
        int pagesLeft = left + margin;
        int pagesTop = top + margin;
        int pagesRight = right - margin;
        int pagesBottom = bottom - margin;

        if (splitLeftRight) {
            int half = Math.max(hinge.width(), spineMin) / 2;
            int center = hinge.centerX();
            spineRect.set(center - half, pagesTop, center + half, pagesBottom);
            leftRect.set(pagesLeft, pagesTop, spineRect.left, pagesBottom);
            rightRect.set(spineRect.right, pagesTop, pagesRight, pagesBottom);
        } else {
            int half = Math.max(hinge.height(), spineMin) / 2;
            int center = hinge.centerY();
            spineRect.set(pagesLeft, center - half, pagesRight, center + half);
            leftRect.set(pagesLeft, pagesTop, pagesRight, spineRect.top);
            rightRect.set(pagesLeft, spineRect.bottom, pagesRight, pagesBottom);
        }
        setState(newState);
    }

    private void setState(State newState) {
        if (stateReported && newState == state) return;
        state = newState;
        stateReported = true;
        // Report after this layout pass so listeners can safely change views.
        post(() -> {
            if (listener != null) listener.onBookStateChanged(state);
        });
    }

    private static void setShown(View view, boolean shown) {
        if (view == null) return;
        int visibility = shown ? VISIBLE : GONE;
        if (view.getVisibility() != visibility) view.setVisibility(visibility);
    }

    private static void measureInto(View view, Rect rect) {
        if (view == null) return;
        view.measure(
                MeasureSpec.makeMeasureSpec(Math.max(rect.width(), 0), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Math.max(rect.height(), 0), MeasureSpec.EXACTLY));
    }

    private static void layoutInto(View view, Rect rect) {
        if (view == null) return;
        view.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    private static Activity findActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) return (Activity) context;
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
