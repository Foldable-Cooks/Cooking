package com.example.cookinggame;

import android.view.View;

import androidx.annotation.LayoutRes;

/**
 * One stage of gameplay (Assembly, Cooking or Results).
 *
 * A stage supplies a layout for the left page and one for the right page.
 * Gameplay inflates them into the open book and calls the hooks below.
 *
 * To add a mechanic (dragging, timers, shaking, folding...):
 *   1. find your views in onEnter(),
 *   2. react to the current step in onStepChanged() / to folding in onBookStateChanged(),
 *   3. call game.completeCurrentStep() when the player has done the step.
 */
public abstract class GameStage {

    protected final Gameplay game;
    protected View leftPage;
    protected View rightPage;

    protected GameStage(Gameplay game) {
        this.game = game;
    }

    public abstract Stage getType();

    @LayoutRes
    public abstract int getLeftPageLayout();

    @LayoutRes
    public abstract int getRightPageLayout();

    /** Called by Gameplay right after the pages are inflated. */
    final void attach(View leftPage, View rightPage) {
        this.leftPage = leftPage;
        this.rightPage = rightPage;
    }

    /** The pages are on screen; find views and set them up here. */
    public void onEnter() {}

    /**
     * A new step started while this stage is showing. step is null on the Results stage.
     * stepIndex counts from 0 across the whole recipe.
     */
    public void onStepChanged(Recipe.Step step, int stepIndex) {}

    /** The phone was folded (COVER), half opened or fully opened. */
    public void onBookStateChanged(BookSpreadView.State state) {}

    /** The game came back on screen (e.g. after the phone was folded and reopened). */
    public void onResume() {}

    /** The stage is about to be removed; stop timers, sensors, sounds here. */
    public void onExit() {}
}
