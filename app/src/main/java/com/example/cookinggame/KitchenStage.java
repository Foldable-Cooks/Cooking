package com.example.cookinggame;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The kitchen table, shared by the Assembly and Cooking stages.
 *
 * Left side: square ingredient tiles. Right side: the step's station
 * (board, bowl, pan...). Drag a tile to the right side to drop it on the station.
 * A DRAG step finishes once everything in step.needs has been dropped.
 * A fold-to-split step finishes when the phone is folded and reopened: the fruit
 * plays its cut-open animation and becomes "Halved ...".
 * Other steps (shake, tap, timer, plain folds) are finished by their own mechanic.
 */
public abstract class KitchenStage extends GameStage {

    private static final int DROPPED_ITEM_DP = 96;
    private static final long NEXT_STEP_DELAY_MS = 600;
    private static final long MESSAGE_MS = 1500;

    protected GridLayout tray;
    protected View station;
    protected ImageView stationImage;
    protected TextView stationLabel;
    protected FrameLayout stationItems;
    protected View stationHighlight;
    protected TextView stationMessage;
    protected TextView timerText;

    private Station currentStation;
    private final List<TileView> tiles = new ArrayList<>();
    /** Items dropped on the station during the current step. */
    private final Set<String> dropped = new HashSet<>();
    /** Everything currently sitting on the station, by item name. */
    private final Map<String, ImageView> stationSprites = new HashMap<>();
    /** Fold-to-split step in progress: waiting for the phone to fold, then reopen. */
    private boolean awaitingSplit = false;
    private boolean foldedForSplit = false;
    /** Reopened while the game was hidden: play the split once it's visible. */
    private boolean splitWhenVisible = false;
    private HingeWatcher hinge;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int[] screenPos = new int[2];

    private static class TileView {
        final String name;
        final View root;
        final View square;

        TileView(String name, View root, View square) {
            this.name = name;
            this.root = root;
            this.square = square;
        }
    }

    protected KitchenStage(Gameplay game) {
        super(game);
    }

    @Override
    public int getLeftPageLayout() {
        return R.layout.stage_kitchen_left;
    }

    @Override
    public int getRightPageLayout() {
        return R.layout.stage_kitchen_right;
    }

    @Override
    public void onEnter() {
        tray = leftPage.findViewById(R.id.ingredientTray);
        station = rightPage.findViewById(R.id.station);
        stationImage = rightPage.findViewById(R.id.stationImage);
        stationLabel = rightPage.findViewById(R.id.stationLabel);
        stationItems = rightPage.findViewById(R.id.stationItems);
        stationHighlight = rightPage.findViewById(R.id.stationHighlight);
        stationMessage = rightPage.findViewById(R.id.stationMessage);
        timerText = rightPage.findViewById(R.id.timerText);

        // The whole right side accepts drops (they land on the station).
        rightPage.setOnDragListener(this::onDrag);

        hinge = new HingeWatcher(game, new HingeWatcher.Listener() {
            @Override
            public void onFolded() {
                if (awaitingSplit) foldedForSplit = true;
            }

            @Override
            public void onReopened() {
                if (awaitingSplit && foldedForSplit) requestSplit();
            }
        });
    }

    @Override
    public void onStepChanged(Recipe.Step step, int stepIndex) {
        handler.removeCallbacksAndMessages(null);
        dropped.clear();
        stationMessage.setText(null);

        // Keep what's on the station while the station stays the same (e.g. building a quesadilla).
        if (step.station != currentStation) {
            currentStation = step.station;
            PixelArt.setImage(stationImage, step.station.imageRes);
            stationLabel.setText(step.station.label);
            stationItems.removeAllViews();
            stationSprites.clear();
        }

        timerText.setVisibility(step.action == Recipe.Action.TIMER ? View.VISIBLE : View.GONE);
        fillTray(step);

        awaitingSplit = step.splits != null;
        foldedForSplit = false;
        splitWhenVisible = false;
        hinge.stop();
        if (awaitingSplit) {
            hinge.start();
            // Make sure the fruit is on the station (e.g. after the app was restarted).
            if (!stationSprites.containsKey(step.splits)) {
                stationItems.post(() -> placeOnStation(step.splits,
                        stationItems.getWidth() / 2f, stationItems.getHeight() / 2f));
            }
            stationMessage.setText(R.string.kitchen_fold_to_split);
        }
    }

    @Override
    public void onBookStateChanged(BookSpreadView.State state) {
        if (!awaitingSplit) return;
        if (state != BookSpreadView.State.OPEN) {
            // Folded shut or bent half open: that's the "snap".
            foldedForSplit = true;
        } else if (foldedForSplit) {
            // Opened again: show the fruit breaking in half.
            requestSplit();
        }
    }

    @Override
    public void onResume() {
        if (splitWhenVisible) {
            splitWhenVisible = false;
            splitFruit();
        }
    }

    @Override
    public void onExit() {
        handler.removeCallbacksAndMessages(null);
        if (hinge != null) hinge.stop();
    }

    // ---- Fold to split ----

    /** Splits now if the game is on screen, otherwise as soon as it comes back. */
    private void requestSplit() {
        if (!awaitingSplit) return;
        awaitingSplit = false;
        hinge.stop();
        if (game.isOnScreen()) {
            splitFruit();
        } else {
            splitWhenVisible = true;
        }
    }

    private void splitFruit() {
        Recipe.Step step = game.getCurrentStep();
        if (step == null || step.splits == null) return;

        String item = step.splits;
        String result = Recipe.halved(item);
        ImageView sprite = stationSprites.remove(item);
        if (sprite == null) {
            game.completeCurrentStep();
            return;
        }

        // The animation and the halves are twice as wide as the whole fruit.
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sprite.getLayoutParams();
        int centerX = params.leftMargin + params.width / 2;
        params.width = params.height * 2;
        params.leftMargin = clamp(centerX - params.width / 2, 0, stationItems.getWidth() - params.width);
        sprite.setLayoutParams(params);

        int animRes = FoodArt.openAnimation(sprite.getContext(), item);
        int duration = animRes != 0 ? SplitAnimation.play(sprite, animRes) : 0;
        stationMessage.setText(null);

        handler.postDelayed(() -> {
            sprite.setImageDrawable(FoodArt.ingredient(sprite.getContext(), result));
            sprite.setContentDescription(result);
            stationSprites.put(result, sprite);
            showMessage(R.string.kitchen_nice);
            handler.postDelayed(game::completeCurrentStep, NEXT_STEP_DELAY_MS);
        }, duration);
    }

    // ---- Left side: ingredient tiles ----

    /** Recipe ingredients, plus any in-between food this step needs (e.g. "Batter"). */
    private void fillTray(Recipe.Step step) {
        List<String> items = new ArrayList<>();
        for (String ingredient : game.getRecipe().ingredients) items.add(ingredient);
        for (String need : step.needs) {
            if (!items.contains(need)) items.add(need);
        }

        tray.removeAllViews();
        tiles.clear();
        LayoutInflater inflater = LayoutInflater.from(tray.getContext());
        for (String name : items) {
            View root = inflater.inflate(R.layout.item_ingredient_tile, tray, false);
            View square = root.findViewById(R.id.tileSquare);
            ((ImageView) root.findViewById(R.id.tileImage))
                    .setImageDrawable(FoodArt.ingredient(tray.getContext(), name));
            ((TextView) root.findViewById(R.id.tileName)).setText(name);

            TileView tile = new TileView(name, root, square);
            setUpDrag(tile);
            tray.addView(root);
            tiles.add(tile);
        }
        updateTileHighlights();
    }

    /** Gold border on items still needed for this step. */
    private void updateTileHighlights() {
        Recipe.Step step = game.getCurrentStep();
        for (TileView tile : tiles) {
            boolean needed = step != null && step.needs(tile.name) && !dropped.contains(tile.name);
            tile.square.setSelected(needed);
        }
    }

    // Drag starts the moment a tile is touched, which feels best for a game.
    @SuppressLint("ClickableViewAccessibility")
    private void setUpDrag(TileView tile) {
        tile.root.setOnTouchListener((v, event) -> {
            if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
            ClipData data = ClipData.newPlainText("ingredient", tile.name);
            tile.square.startDragAndDrop(data, new View.DragShadowBuilder(tile.square), tile.name, 0);
            return true;
        });
    }

    // ---- Right side: dropping on the station ----

    private boolean onDrag(View target, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Only accept our own ingredient drags.
                return event.getLocalState() instanceof String;
            case DragEvent.ACTION_DRAG_ENTERED:
                stationHighlight.setVisibility(View.VISIBLE);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                stationHighlight.setVisibility(View.GONE);
                return true;
            case DragEvent.ACTION_DROP:
                stationHighlight.setVisibility(View.GONE);
                return onDrop((String) event.getLocalState(), target, event.getX(), event.getY());
            default:
                return true;
        }
    }

    /** Returns false to reject the drop (the item flies back to the tray). */
    private boolean onDrop(String item, View target, float x, float y) {
        Recipe.Step step = game.getCurrentStep();
        if (step == null) return false;

        if (!step.needs(item)) {
            showMessage(R.string.kitchen_not_needed);
            return false;
        }
        if (dropped.contains(item)) {
            showMessage(R.string.kitchen_already_added);
            return false;
        }

        dropped.add(item);
        // Drop point is relative to the right side; convert to the station.
        target.getLocationOnScreen(screenPos);
        float screenX = screenPos[0] + x;
        float screenY = screenPos[1] + y;
        stationItems.getLocationOnScreen(screenPos);
        placeOnStation(item, screenX - screenPos[0], screenY - screenPos[1]);
        updateTileHighlights();

        if (dropped.size() == step.needs.length) {
            showMessage(R.string.kitchen_nice);
            handler.postDelayed(game::completeCurrentStep, NEXT_STEP_DELAY_MS);
        }
        return true;
    }

    /**
     * Adds a sprite of the item centered on (x, y) in station coordinates, kept inside
     * the station. Wide art (like halved fruit) gets a wider sprite.
     */
    private void placeOnStation(String item, float x, float y) {
        float density = stationItems.getResources().getDisplayMetrics().density;
        int height = Math.round(DROPPED_ITEM_DP * density);

        Drawable art = FoodArt.ingredient(stationItems.getContext(), item);
        int width = height;
        if (art.getIntrinsicWidth() > art.getIntrinsicHeight() && art.getIntrinsicHeight() > 0) {
            width = height * art.getIntrinsicWidth() / art.getIntrinsicHeight();
        }

        int left = clamp(Math.round(x - width / 2f), 0, stationItems.getWidth() - width);
        int top = clamp(Math.round(y - height / 2f), 0, stationItems.getHeight() - height);

        ImageView sprite = new ImageView(stationItems.getContext());
        sprite.setImageDrawable(art);
        sprite.setContentDescription(item);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.leftMargin = left;
        params.topMargin = top;
        stationItems.addView(sprite, params);

        // Same item dropped again on a later step replaces the old one.
        ImageView old = stationSprites.put(item, sprite);
        if (old != null) stationItems.removeView(old);
    }

    private void showMessage(int textRes) {
        stationMessage.setText(textRes);
        handler.postDelayed(() -> stationMessage.setText(null), MESSAGE_MS);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, Math.max(min, max)));
    }
}
