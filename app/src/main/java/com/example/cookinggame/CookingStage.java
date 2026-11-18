package com.example.cookinggame;

import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * COOKING: heat, blend, flip.
 * Left page: current step + timer. Right page: the stove / pan area.
 */
public class CookingStage extends GameStage {

    /** Slot on the left page for the timer UI. */
    protected FrameLayout timerArea;
    protected TextView timerText;
    /** Area on the right page for the pan / blender. */
    protected FrameLayout stove;

    public CookingStage(Gameplay game) {
        super(game);
    }

    @Override
    public Stage getType() {
        return Stage.COOKING;
    }

    @Override
    public int getLeftPageLayout() {
        return R.layout.stage_cooking_left;
    }

    @Override
    public int getRightPageLayout() {
        return R.layout.stage_cooking_right;
    }

    @Override
    public void onEnter() {
        timerArea = leftPage.findViewById(R.id.timerArea);
        timerText = leftPage.findViewById(R.id.timerText);
        stove = rightPage.findViewById(R.id.stoveArea);
    }

    @Override
    public void onStepChanged(Recipe.Step step, int stepIndex) {
        // PLACEHOLDER: timer steps start a countdown here and call
        // game.completeCurrentStep() when it rings.
        timerText.setText(R.string.timer_placeholder);
    }
}
