package com.example.cookinggame;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * RESULTS: shown after the last step.
 * Left page: the finished dish. Right page: score, then back to the recipe book.
 */
public class ResultsStage extends GameStage {

    public ResultsStage(Gameplay game) {
        super(game);
    }

    @Override
    public Stage getType() {
        return Stage.RESULTS;
    }

    @Override
    public int getLeftPageLayout() {
        return R.layout.stage_results_left;
    }

    @Override
    public int getRightPageLayout() {
        return R.layout.stage_results_right;
    }

    @Override
    public void onEnter() {
        Recipe recipe = game.getRecipe();

        PixelArt.setImage(leftPage.findViewById(R.id.resultDishImage), recipe.imageRes);
        ((TextView) leftPage.findViewById(R.id.resultDishName)).setText(recipe.name);

        // PLACEHOLDER score: always 3 stars until there is real scoring.
        ((TextView) rightPage.findViewById(R.id.resultStars)).setText(R.string.results_stars_placeholder);

        int steps = recipe.steps.length;
        ((TextView) rightPage.findViewById(R.id.resultSteps)).setText(
                game.getString(R.string.results_steps, steps, steps));

        long seconds = game.getElapsedMillis() / 1000;
        ((TextView) rightPage.findViewById(R.id.resultTime)).setText(
                game.getString(R.string.results_time, (int) (seconds / 60), (int) (seconds % 60)));

        rightPage.findViewById(R.id.cookAgainButton).setOnClickListener(v -> game.cookAgain());
        rightPage.findViewById(R.id.backToRecipesButton).setOnClickListener(v -> game.returnToRecipeBook());
    }
}
