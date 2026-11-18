package com.example.cookinggame;

/** ASSEMBLY: putting ingredients together on the kitchen table (see KitchenStage). */
public class AssemblyStage extends KitchenStage {

    public AssemblyStage(Gameplay game) {
        super(game);
    }

    @Override
    public Stage getType() {
        return Stage.ASSEMBLY;
    }
}
