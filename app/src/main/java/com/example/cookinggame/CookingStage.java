package com.example.cookinggame;

/**
 * COOKING: heat, blend, flip on the kitchen table (see KitchenStage).
 * The timer text above the station is shown on TIMER steps.
 */
public class CookingStage extends KitchenStage {

    public CookingStage(Gameplay game) {
        super(game);
    }

    @Override
    public Stage getType() {
        return Stage.COOKING;
    }

    @Override
    public void onStepChanged(Recipe.Step step, int stepIndex) {
        super.onStepChanged(step, stepIndex);
        // PLACEHOLDER: TIMER steps start a countdown in timerText here and
        // call game.completeCurrentStep() when it rings.
        timerText.setText(R.string.timer_placeholder);
    }
}
