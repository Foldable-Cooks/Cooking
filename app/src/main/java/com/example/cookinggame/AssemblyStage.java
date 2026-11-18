package com.example.cookinggame;

import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * ASSEMBLY: putting ingredients together.
 * Left page: current step + ingredient tray. Right page: the cutting board.
 */
public class AssemblyStage extends GameStage {

    /** Drop zone on the right page where ingredients get dragged to. */
    protected FrameLayout board;
    protected GridLayout tray;

    public AssemblyStage(Gameplay game) {
        super(game);
    }

    @Override
    public Stage getType() {
        return Stage.ASSEMBLY;
    }

    @Override
    public int getLeftPageLayout() {
        return R.layout.stage_assembly_left;
    }

    @Override
    public int getRightPageLayout() {
        return R.layout.stage_assembly_right;
    }

    @Override
    public void onEnter() {
        tray = leftPage.findViewById(R.id.ingredientTray);
        board = rightPage.findViewById(R.id.assemblyBoard);

        // PLACEHOLDER tiles: one per ingredient. Dragging onto `board` goes here.
        LayoutInflater inflater = LayoutInflater.from(leftPage.getContext());
        for (String ingredient : game.getRecipe().ingredients) {
            TextView tile = (TextView) inflater.inflate(R.layout.item_ingredient_tile, tray, false);
            tile.setText(ingredient);
            tray.addView(tile);
        }
    }
}
